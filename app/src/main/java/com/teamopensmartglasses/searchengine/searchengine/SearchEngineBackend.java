package com.teamopensmartglasses.searchengine.searchengine;

import android.content.Context;
import android.util.Log;

import com.teamopensmartglasses.searchengine.events.SearchResultFailureEvent;
import com.teamopensmartglasses.searchengine.events.SearchResultSuccessDataEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
Adapted from:
https://github.com/emexlabs/WearableIntelligenceSystem/blob/master/android_smart_phone/main/app/src/main/java/com/wearableintelligencesystem/androidsmartphone/GLBOXRepresentative.java
 */

public class SearchEngineBackend {
    final public String TAG = "SearchApp_SearchEngine";
    private RestComms restComms;

    public SearchEngineBackend(Context ctx){
        restComms = new RestComms(ctx);
    }

    public void sendQuery(String query){
        Log.d(TAG, "Running sendSearchEngineQuery");
        try{
            restComms.restRequest(query, new VolleyCallback(){
                @Override
                public void onSuccess(JSONObject result){
                    Log.d(TAG, "GOT search engine REST RESULT:");
                    Log.d(TAG, result.toString());
                    try {
                        parseGoogleResult(result);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void onFailure(){
                    Log.d(TAG, "SOME FAILURE FAILURE HAPPENED");
                    EventBus.getDefault().post(new SearchResultFailureEvent("No connection"));
                }

            });
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    // this is fine :)
    public void parseGoogleResult(JSONObject response) throws JSONException {
        JSONArray itemList = response.getJSONArray("itemListElement");
        if(itemList.length() == 0){
            EventBus.getDefault().post(new SearchResultFailureEvent("No results"));
            return;
        }

        JSONObject item = itemList.getJSONObject(0).getJSONObject("result");
        Log.d(TAG, "RESULT: " + item.toString());

        String title = "";
        String body = "";

        try {
            title = item.getString("name");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            body = item.getJSONObject("detailedDescription").getString("articleBody");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            Log.d(TAG, "TheTitle: " + title);
            Log.d(TAG, "TheBody: " + body);
            EventBus.getDefault().post(new SearchResultSuccessDataEvent(title, body));
        }
    }
}
