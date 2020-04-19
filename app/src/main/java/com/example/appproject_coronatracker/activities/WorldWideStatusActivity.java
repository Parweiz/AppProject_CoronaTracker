package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;

import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.service.CoronaTrackerService;

public class WorldWideStatusActivity extends AppCompatActivity {

    public static final String TAG = "coronatrackerservice";
    CoronaTrackerService coronaTrackerService = new CoronaTrackerService();
    private ServiceConnection boundService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_wide_status);
    }

    private void bindingToTheService() {
        Log.d(TAG, "Binding to the service from DetailsActivity ");
        Intent intent = new Intent(this, CoronaTrackerService.class);
        bindService(intent, boundService, Context.BIND_AUTO_CREATE);
    }

    private void unbindingFromTheService() {
        Log.d(TAG, "Unbinding to the service from DetailsActivity ");
        if (mBound) {
            unbindService(boundService);
            mBound = false;
        }
    }

}
