package com.teamopensmartglasses.searchengine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "SmartGlassesSearchEngineApp_MainActivity";
    Button killServiceButton;
    boolean mBound;
    public SearchEngineService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        killServiceButton = (Button)findViewById(R.id.killServiceButton);
        killServiceButton.setOnClickListener(
                v -> stopSearchEngineService());

        mBound = false;
        startSearchEngineService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //bind to foreground service
        bindSearchEngineService();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unbind foreground service
        unbindSearchEngineService();
    }

    public void stopSearchEngineService() {
        unbindSearchEngineService();
        if (!isMyServiceRunning(SearchEngineService.class)) return;
        Intent stopIntent = new Intent(this, SearchEngineService.class);
        stopIntent.setAction(SearchEngineService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(stopIntent);
    }

    public void sendSearchEngineServiceMessage(String message) {
        if (!isMyServiceRunning(SearchEngineService.class)) return;
        Intent messageIntent = new Intent(this, SearchEngineService.class);
        messageIntent.setAction(message);
        startService(messageIntent);
    }

    public void startSearchEngineService() {
        if (isMyServiceRunning(SearchEngineService.class)){
            Log.d(TAG, "Not starting service.");
            return;
        }
        Log.d(TAG, "Starting service.");
        Intent startIntent = new Intent(this, SearchEngineService.class);
        startIntent.setAction(SearchEngineService.ACTION_START_FOREGROUND_SERVICE);
        startService(startIntent);
        bindSearchEngineService();
    }

    //check if service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void bindSearchEngineService(){
        if (!mBound){
            Intent intent = new Intent(this, SearchEngineService.class);
            bindService(intent, searchAppServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void unbindSearchEngineService() {
        if (mBound){
            unbindService(searchAppServiceConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection searchAppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SearchEngineService.LocalBinder sgmLibServiceBinder = (SearchEngineService.LocalBinder) service;
            mService = (SearchEngineService) sgmLibServiceBinder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}