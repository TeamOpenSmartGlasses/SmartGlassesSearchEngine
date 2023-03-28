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

    final Handler handler = new Handler();

    final int delay = 1000; // 1000 milliseconds == 1 second

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

        EventBus.getDefault().register(this);

        searchEngine = new SearchEngine(this);

        searchCommandCallback("Dogs",0);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void searchCommandCallback(String args, long commandTriggeredTime) {
        Log.d(TAG,"Search callback called");
        searchEngine.sendQuery(args);
    }

    @Subscribe
    public void onSearchResultSuccessEvent(SearchResultSuccessEvent receivedEvent){
        sgmLib.sendReferenceCard(appName, "Search success!");
    }

    @Subscribe
    public void onSearchResultSuccessDataEvent(SearchResultSuccessDataEvent receivedEvent){
        JSONObject obj = receivedEvent.result;
        String result = obj.toString(); //TODO: evaluate
        sgmLib.sendReferenceCard(appName, result);
    }

    @Subscribe
    public void onSearchResultFailureEvent(SearchResultFailureEvent receivedEvent){
        sgmLib.sendReferenceCard(appName, "Search failed.");
    }

}
