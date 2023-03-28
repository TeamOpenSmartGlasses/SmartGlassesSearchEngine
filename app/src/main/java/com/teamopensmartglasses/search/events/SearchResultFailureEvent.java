package com.teamopensmartglasses.search.events;
public class SearchResultFailureEvent {
    public String reason;
    public SearchResultFailureEvent(String myReason){
        reason = myReason;}
}
