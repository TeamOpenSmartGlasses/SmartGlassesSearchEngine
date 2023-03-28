package com.teamopensmartglasses.search.events;

import org.json.JSONObject;

public class SearchResultSuccessDataEvent {
    public JSONObject result;
    public SearchResultSuccessDataEvent(JSONObject res){
        result = res;
    }
}
