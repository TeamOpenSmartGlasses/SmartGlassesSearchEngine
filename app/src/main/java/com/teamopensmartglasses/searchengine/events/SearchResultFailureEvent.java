package com.teamopensmartglasses.searchengine.events;
public class SearchResultFailureEvent {
    public String reason;
    public SearchResultFailureEvent(String myReason){
        reason = myReason;}
}
