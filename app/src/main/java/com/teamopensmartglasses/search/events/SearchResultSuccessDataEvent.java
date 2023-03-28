package com.teamopensmartglasses.search.events;

import org.json.JSONObject;

public class SearchResultSuccessDataEvent {
    public String title;
    public String body;
    public SearchResultSuccessDataEvent(String myTitle, String myBody){
        title = myTitle;
        body = myBody;
    }
}
