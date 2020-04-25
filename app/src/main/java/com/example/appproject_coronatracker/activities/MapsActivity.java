package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import com.example.appproject_coronatracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.LatLng;

public class MapsActivity extends AppCompatActivity {

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    // Maps
    MapView mapView;
    GoogleMap gMap;
    float mapZoomLevel = 16;
    boolean cameraSet = false;

    // To get users current location
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng userLatLong;

    // Widgets


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }

    public void backBtn (View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
