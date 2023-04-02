package com.teamopensmartglasses.searchengine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.Manifest;
import android.widget.Toast;

import com.teamopensmartglasses.searchengine.R;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "SmartGlassesSearchEngineApp_MainActivity";
    boolean mBound;
    public SearchEngineService mService;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checkPermissions();

        mBound = false;
        startSearchService();
    }

    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        int permission3 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }

        if(permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED)
        {
            startSearchService();
        }
        else
        {
            String txt = "NOT ENOUGH PERMISSIONS. VERY SAD SO BAD AND SAD ;(";
            Toast.makeText(this, txt, Toast.LENGTH_LONG);
            Log.d(TAG, txt);
           // finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //bind to foreground service
        bindSearchService();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unbind foreground service
        unbindSearchService();
    }

    public void stopSearchService() {
        unbindSearchService();
        if (!isMyServiceRunning(SearchEngineService.class)) return;
        Intent stopIntent = new Intent(this, SearchEngineService.class);
        stopIntent.setAction(SearchEngineService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(stopIntent);
    }

    public void sendSearchServiceMessage(String message) {
        if (!isMyServiceRunning(SearchEngineService.class)) return;
        Intent messageIntent = new Intent(this, SearchEngineService.class);
        messageIntent.setAction(message);
        startService(messageIntent);
    }

    public void startSearchService() {
        if (isMyServiceRunning(SearchEngineService.class)){
            Log.d(TAG, "Not starting service.");
            return;
        }
        Log.d(TAG, "Starting service.");
        Intent startIntent = new Intent(this, SearchEngineService.class);
        startIntent.setAction(SearchEngineService.ACTION_START_FOREGROUND_SERVICE);
        startService(startIntent);
        bindSearchService();
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

    public void bindSearchService(){
        if (!mBound){
            Intent intent = new Intent(this, SearchEngineService.class);
            bindService(intent, searchAppServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void unbindSearchService() {
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