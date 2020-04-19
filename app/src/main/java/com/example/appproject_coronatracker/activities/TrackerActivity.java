package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;

import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.adapter.CoronaTrackerAdapter;
import com.example.appproject_coronatracker.models.Country;
import com.example.appproject_coronatracker.service.CoronaTrackerService;

import java.util.ArrayList;

public class TrackerActivity extends AppCompatActivity {

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
    }

    public void backBtn (View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CoronaTrackerAdapter(mCountryArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
