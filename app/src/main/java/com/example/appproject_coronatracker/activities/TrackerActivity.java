package com.example.appproject_coronatracker.activities;

import androidx.annotation.NonNull;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.adapter.CoronaTrackerAdapter;
import com.example.appproject_coronatracker.models.Country;
import com.example.appproject_coronatracker.service.CoronaTrackerService;

import java.util.ArrayList;

import static com.example.appproject_coronatracker.service.CoronaTrackerService.ARRAY_LIST;

public class TrackerActivity extends AppCompatActivity implements CoronaTrackerAdapter.OnItemListener {

    private ArrayList<Country> mCountryArrayList = new ArrayList<>();
    private CoronaTrackerAdapter mAdapter;
    public static final String TAG = "coronatrackerservice";
    private RecyclerView mRecyclerView;
    CoronaTrackerService coronaTrackerService = new CoronaTrackerService();
    private ServiceConnection boundService;
    private boolean mBound = false;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        if (savedInstanceState != null) {
            mCountryArrayList = savedInstanceState.getParcelableArrayList(getString(R.string.key_orientationchange));
        }

        setUpRecyclerView();
        boundServiceSetupFunction();
        searchFunc();
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

    private void searchFunc() {
        editText = findViewById(R.id.editTextSearch);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


    }

    private void filter(String text) {
        ArrayList<Country> filteredList = new ArrayList<>();

        for (Country item : mCountryArrayList) {
            if (item.getCountry().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        mAdapter.updateData(filteredList);
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

    public void backBtn(View v) {
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


    public void addDataBtn(View v) {
        String country = editText.getText().toString();
        coronaTrackerService.addCountry(country);
    }

    private void boundServiceSetupFunction() {
        boundService = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {

                CoronaTrackerService.LocalBinder binder = (CoronaTrackerService.LocalBinder) service;
                coronaTrackerService = binder.getService();
                mBound = true;
                Log.d(TAG, "Boundservice connected - TrackerActivity");

                coronaTrackerService.getAllCountries();
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

            mCountryArrayList = intent.getParcelableArrayListExtra(getString(R.string.key_broadcast_arraylist));
            mAdapter.updateData(mCountryArrayList);
        }
    };


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DetailsActivity.class);
        Country clickedCountry = mCountryArrayList.get(position);

        intent.putExtra(getString(R.string.key_flag), clickedCountry.getCountryInfo().getFlag());
        intent.putExtra(getString(R.string.key_country), clickedCountry.getCountry());
        intent.putExtra(getString(R.string.key_totalcases), clickedCountry.getCases());
        intent.putExtra(getString(R.string.key_todayscases), clickedCountry.getTodayCases());
        intent.putExtra(getString(R.string.key_totaldeaths), clickedCountry.getDeaths());
        intent.putExtra(getString(R.string.key_totalcritical), clickedCountry.getCritical());
        intent.putExtra(getString(R.string.key_totalrecovered), clickedCountry.getRecovered());
        intent.putExtra(getString(R.string.activecases), clickedCountry.getActive());
        intent.putExtra(getString(R.string.key_totaltests), clickedCountry.getTests());

        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.key_orientationchange), mCountryArrayList);
        super.onSaveInstanceState(outState);
    }
}
