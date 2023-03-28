package com.teamopensmartglasses.search;

import android.os.Handler;
import android.util.Log;

import com.teamopensmartglasses.search.events.SearchResultFailureEvent;
import com.teamopensmartglasses.search.events.SearchResultSuccessDataEvent;
import com.teamopensmartglasses.search.events.SearchResultSuccessEvent;
import com.teamopensmartglasses.search.search.SearchEngine;
import com.teamopensmartglasses.sgmlib.SGMCommand;
import com.teamopensmartglasses.sgmlib.SGMLib;
import com.teamopensmartglasses.sgmlib.SmartGlassesAndroidService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class SearchService extends SmartGlassesAndroidService {
    public final String TAG = "SearchApp_SearchService";
    static final String appName = "Search";
    public SearchEngine searchEngine;

    //our instance of the SGM library
    public SGMLib sgmLib;

    public SearchService(){
        super(MainActivity.class,
                "search_app",
                1008,
                appName,
                "Search for smartglasses", com.google.android.material.R.drawable.notify_panel_notification_icon_bg);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* Handle SGMLib specific things */

        //Create SGMLib instance with context: this
        sgmLib = new SGMLib(this);

        //Define command with a UUID
        UUID commandUUID = UUID.fromString("5b824bb6-d3b3-417d-8c74-3b103efb4033");

        //Define list of phrases to be used to trigger the command
        String[] triggerPhrases = new String[]{"search", "search for"};

        //Create command object
        SGMCommand command = new SGMCommand(appName, commandUUID, triggerPhrases, "Search the web on smartglasses!");

        //Register the command
        sgmLib.registerCommand(command, this::searchCommandCallback);

        Log.d(TAG, "SEARCH SERVICE STARTED");

        /* Handle SmartGlassesSearch specific things */

        EventBus.getDefault().register(this);

        searchEngine = new SearchEngine(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void searchCommandCallback(String args, long commandTriggeredTime) {
        Log.d(TAG,"Search callback called");
        Log.d(TAG, "CMDARGS: "+ args);
        Log.d(TAG, "TIME: " + commandTriggeredTime);

        if(args == "" || args == null){
            sgmLib.sendReferenceCard(appName, "No search query detected.\nTry again with a seach query.");
            return;
        }

        searchEngine.sendQuery(args);
    }

    @Subscribe
    public void onSearchResultSuccessEvent(SearchResultSuccessEvent receivedEvent){
        sgmLib.sendReferenceCard(appName, "Search success!");
    }

    @Subscribe
    public void onSearchResultSuccessDataEvent(SearchResultSuccessDataEvent receivedEvent){
        Log.d(TAG,"BODY: " + receivedEvent.title);
        String title = receivedEvent.title == "" ? appName : receivedEvent.title;
        sgmLib.sendReferenceCard(title, receivedEvent.body);
    }

    @Subscribe
    public void onSearchResultFailureEvent(SearchResultFailureEvent receivedEvent){
        sgmLib.sendReferenceCard(appName, "Search failed.\n" + receivedEvent.reason);
    }

}
