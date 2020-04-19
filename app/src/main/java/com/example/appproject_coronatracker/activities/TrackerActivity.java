package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.adapter.CoronaTrackerAdapter;
import com.example.appproject_coronatracker.models.Country;
import com.example.appproject_coronatracker.service.CoronaTrackerService;

import java.util.ArrayList;

import static com.example.appproject_coronatracker.service.CoronaTrackerService.ARRAY_LIST;

public class TrackerActivity extends AppCompatActivity implements CoronaTrackerAdapter.OnItemListener {

    private int wordClickedIndex;
    private ArrayList<Country> mCountryArrayList = new ArrayList<>();
    private CoronaTrackerAdapter mAdapter;
    public static final String TAG = "coronatrackerservice";
    private RecyclerView mRecyclerView;
    CoronaTrackerService coronaTrackerService = new CoronaTrackerService();
    private ServiceConnection boundService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        setUpRecyclerView();
        boundServiceSetupFunction();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindingToTheService();

        IntentFilter filter = new IntentFilter();
        filter.addAction(CoronaTrackerService.BROADCAST_BACKGROUND_SERIVE_ARRAYLIST);
        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindingFromTheService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
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

    public void backBtn (View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CoronaTrackerAdapter(mCountryArrayList, this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initialData() {

        String server_url = "https://corona.lmao.ninja/v2/countries/";
        String country = "denmark";

        coronaTrackerService.volleyRequest(server_url + country);

    }

    private void boundServiceSetupFunction() {
        boundService = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {

                CoronaTrackerService.LocalBinder binder = (CoronaTrackerService.LocalBinder) service;
                coronaTrackerService = binder.getService();
                mBound = true;
                Log.d(TAG, "Boundservice connected - TrackerActivity");
                initialData();

            }

            public void onServiceDisconnected(ComponentName className) {
                coronaTrackerService = null;
                mBound = false;
                Log.d(TAG, "Boundservice disconnected - TrackerActivity");
            }
        };
    }

    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getBundleExtra("Bundle");
            mCountryArrayList = (ArrayList<Country>) bundle.getSerializable(ARRAY_LIST);

            Log.d(TAG, "onReceive: " + mCountryArrayList.get(0).getCountry());

            mAdapter.updateData(mCountryArrayList);

        }
    };


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DetailsActivity.class);
        Country clickedCountry = mCountryArrayList.get(position);
        wordClickedIndex = position;

        intent.putExtra(getString(R.string.key_flag), clickedCountry.getCountryInfo().getFlag());
        intent.putExtra(getString(R.string.key_country), clickedCountry.getCountry());
        intent.putExtra(getString(R.string.key_totalcases), clickedCountry.getCases());
        intent.putExtra(getString(R.string.key_todayscases), clickedCountry.getTodayCases());
        intent.putExtra(getString(R.string.key_totaldeaths), clickedCountry.getDeaths());
        intent.putExtra(getString(R.string.key_totalcritical), clickedCountry.getCritical());
        intent.putExtra(getString(R.string.key_totalrecovered), clickedCountry.getRecovered());


        startActivity(intent);


    }
}
