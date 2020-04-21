package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appproject_coronatracker.R;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }



    public void trackerBtn(View v) {
        Intent intent = new Intent(MenuActivity.this, TrackerActivity.class);
        startActivity(intent);
    }

    public void mapsBtn(View v) {
        Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void logoutBtn(View v) {
        Log.d(TAG, "User: " + FirebaseAuth.getInstance().getCurrentUser() + " logged out"); // TODO: not working... Make it work.
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, R.string.logging_out_message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP); // prevent going back + prevent issues with double-tap
        startActivity(intent);
    }
}
