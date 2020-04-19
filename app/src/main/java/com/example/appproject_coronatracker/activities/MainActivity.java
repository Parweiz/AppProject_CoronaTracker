package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.service.CoronaTrackerService;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "coronatrackerservice";
    CoronaTrackerService coronaTrackerService = new CoronaTrackerService();
    private ServiceConnection boundService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boundServiceSetupFunction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startingAndBindingToTheService();
    }

    private void startingAndBindingToTheService() {
        Log.d(TAG, "Registering receivers and binding to the service");
        Intent intent = new Intent(this, CoronaTrackerService.class);
        startService(intent);
        bindService(intent, boundService, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindingFromTheSerivce();
    }

    private void unBindingFromTheSerivce() {
        Log.d(TAG, "Unregistering receivers and unbinding to the service");

        if (mBound) {
            unbindService(boundService);
            mBound = false;
        }
    }

    public void exitBtn(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void boundServiceSetupFunction() {
        boundService = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {

                CoronaTrackerService.LocalBinder binder = (CoronaTrackerService.LocalBinder) service;
                coronaTrackerService = binder.getService();
                mBound = true;
                Log.d(TAG, "Boundservice connected - ListActivity");

            }

            public void onServiceDisconnected(ComponentName className) {
                coronaTrackerService = null;
                mBound = false;
                Log.d(TAG, "Boundservice disconnected - ListActivity");
            }
        };
    }

    public void menuBtn(View v) {
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(i);
    }
}
