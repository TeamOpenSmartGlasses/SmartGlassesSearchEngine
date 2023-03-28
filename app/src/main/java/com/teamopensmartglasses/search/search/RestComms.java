package com.teamopensmartglasses.search.search;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/*
Adapted from:
https://github.com/emexlabs/WearableIntelligenceSystem/blob/master/android_smart_phone/main/app/src/main/java/com/wearableintelligencesystem/androidsmartphone/comms/RestServerComms.java
 */
public class RestComms {
    private String TAG = "SmartGlassesSearch_RestComms";

    private static RestComms restComms;

    //volley vars
    public RequestQueue mRequestQueue;
    private Context mContext;
    private String serverUrl;
    private int requestTimeoutPeriod = 10000;
    public static final String KNOWLEDGE_GRAPH_API_KEY = "AIzaSyB4p2qYaHXcaKwuyUNv3Y23iN0HNM4wTXk"; //Pretty please don't abuse this? :)

    public static RestComms getInstance(Context c){
        if (restComms == null){
            restComms = new RestComms(c);
        }
        return restComms;
    }

    public RestComms(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    //handles requesting data, sending data
    public void restRequest(String query, VolleyCallback callback) throws JSONException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("kgsearch.googleapis.com")
                .appendPath("v1")
                .appendEncodedPath("entities:search")
                .appendQueryParameter("limit", "1")
                .appendQueryParameter("query", query)
                .appendQueryParameter("key", KNOWLEDGE_GRAPH_API_KEY);
        String myUrl = builder.build().toString();

        int requestType = Request.Method.GET;

        JSONObject datas = new JSONObject();
        // Request a json response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(requestType, myUrl, datas,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"GOT A RESPONSE: " + response.toString());
                        // Display the first 500 characters of the response string.
                            if (response.length() != 0){
                                callback.onSuccess(response);
                            } else{
                                callback.onFailure();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "Failure sending data.");
//                if (retry < 3) {
//                    retry += 1;
//                    refresh();
//                    search(query);
//                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                requestTimeoutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

       mRequestQueue.add(request);
    }

}
