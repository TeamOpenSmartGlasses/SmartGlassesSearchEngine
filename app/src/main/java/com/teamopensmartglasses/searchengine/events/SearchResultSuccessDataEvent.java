package com.teamopensmartglasses.searchengine.events;

public class SearchResultSuccessDataEvent {
    public String title;
    public String body;
    public SearchResultSuccessDataEvent(String myTitle, String myBody){
        title = myTitle;
        body = myBody;
    }
}
