package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;


import com.bumptech.glide.Glide;
import com.example.appproject_coronatracker.R;


public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView txtCountry, txtTotalCases, txtTodaysCount, txtTotalDeaths, txtTotalCritical, txtTotalRecovered, txtActiveCases, txtTotalTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.mDetailsFlag);
        txtCountry = findViewById(R.id.mDetailsCountry);
        txtTotalCases = findViewById(R.id.mDetailsTotalCasesText);
        txtTodaysCount = findViewById(R.id.mDetailsTodaysCountText);
        txtTotalDeaths = findViewById(R.id.mDetailsTotalDeathsText);
        txtTotalCritical = findViewById(R.id.mDetailsTotalCriticalText);
        txtTotalRecovered = findViewById(R.id.mDetailsTotalRecoveredText);
        txtActiveCases = findViewById(R.id.mDetailsActiveCasesText);
        txtTotalTests = findViewById(R.id.mDetailsTotalTestsText);

        gettingDataFromTrackerActivity();
    }

    private void gettingDataFromTrackerActivity() {
        Intent intent = getIntent();

        String picture = intent.getStringExtra(getString(R.string.key_flag));
        Glide.with(imageView.getContext()).load(picture).into(imageView);
        txtCountry.setText(intent.getStringExtra(getString(R.string.key_country)));

        txtTotalCases.setText(String.valueOf(intent.getIntExtra(getString(R.string.key_totalcases), 0)));
        txtTodaysCount.setText(String.valueOf(intent.getIntExtra(getString(R.string.key_todayscases), 0)));
        txtTotalDeaths.setText(String.valueOf(intent.getIntExtra(getString(R.string.key_totaldeaths), 0)));
        txtTotalCritical.setText(String.valueOf(intent.getIntExtra(getString(R.string.key_totalcritical), 0)));
        txtTotalRecovered.setText(String.valueOf(intent.getIntExtra(getString(R.string.key_totalrecovered), 0)));
        txtActiveCases.setText(String.valueOf(intent.getIntExtra(getString(R.string.activecases), 0)));
        txtTotalTests.setText(String.valueOf(intent.getIntExtra(getString(R.string.key_totaltests), 0)));
    }

    public void mDetailsBackBtn(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
