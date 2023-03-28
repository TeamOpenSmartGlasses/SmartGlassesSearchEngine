package com.teamopensmartglasses.search.search;

import android.content.Context;
import android.util.Log;

import com.teamopensmartglasses.search.events.SearchResultFailureEvent;
import com.teamopensmartglasses.search.events.SearchResultSuccessDataEvent;
import com.teamopensmartglasses.search.events.SearchResultSuccessEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/*
Adapted from:
https://github.com/emexlabs/WearableIntelligenceSystem/blob/master/android_smart_phone/main/app/src/main/java/com/wearableintelligencesystem/androidsmartphone/GLBOXRepresentative.java
 */

public class SearchEngine {
    final public String TAG = "SearchApp_SearchEngine";
    private RestServerComms restServerComms;

    public SearchEngine(Context ctx){
        restServerComms = new RestServerComms(ctx);
    }

    public void sendQuery(String query)  {
        JSONObject obj = new JSONObject();

        try {
            obj.put(MessageTypes.TEXT_QUERY, query);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        sendSearchEngineQuery(obj);
    }

    private void sendSearchEngineQuery(JSONObject data){
        Log.d(TAG, "Running sendSearchEngineQuery");
        try{
            JSONObject restMessage = new JSONObject();
            restMessage.put("query", data.get(MessageTypes.TEXT_QUERY));
            restServerComms.restRequest(RestServerComms.SEARCH_ENGINE_QUERY_SEND_ENDPOINT, restMessage, new VolleyCallback(){
                @Override
                public void onSuccess(JSONObject result){
                    Log.d(TAG, "GOT search engine REST RESULT:");
                    Log.d(TAG, result.toString());
                    //asgRep.sendCommandResponse("Search success, displaying results.");
                    EventBus.getDefault().post(new SearchResultSuccessEvent());
                    try{
                        //asgRep.sendSearchEngineResults(result.getJSONObject("response"));
                        EventBus.getDefault().post(new SearchResultSuccessDataEvent(result.getJSONObject("response")));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(){
                    Log.d(TAG, "BIG FAT FAILURE");
                    //asgRep.sendCommandResponse("Search failed, please try again.");
                    //EventBus.getDefault().post(new SearchResultFailureEvent());
                    EventBus.getDefault().post(new SearchResultSuccessDataEvent(restMessage));
                }

            });
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
