package com.teamopensmartglasses.searchengine.searchengine;

import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccess(JSONObject result);
    void onFailure();
}
