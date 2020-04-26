package com.example.appproject_coronatracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.appproject_coronatracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

// reference: https://firebase.google.com/docs/auth/web/manage-users
// reference: spinner: https://www.youtube.com/watch?v=on_OrrX7Nw4
// reference for Google Maps + API: https://developers.google.com/maps/documentation/android-sdk/get-api-key
// reference for Google Maps, User location: https://www.youtube.com/watch?v=pNeuuImirHY
// reference for getting all markers shown: https://stackoverflow.com/questions/56837831/marker-from-firestore-map-data-type

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    // Maps
    MapView mapView;
    GoogleMap gMap;
    float mapZoomLevel = 16;
    boolean cameraSet = false;
    private FusedLocationProviderClient mFusedLocationClient;

    // To get users current location
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng userLatLong;

    // Widgets
    Spinner spinAge, spinGender, spinOtherdiseases, spinStatus;
    EditText etNotesField;
    Button btnBack, btnAdd, btnMine, btnAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(MapsActivity.this, LoginActivity.class));
        }

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser == null){
                    startActivity(new Intent(MapsActivity.this, MenuActivity.class));
                }
            }
        };

        mapView = findViewById(R.id.mapview_maps);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(MapsActivity.this);

        btnAdd = findViewById(R.id.btn_add_maps);
        btnBack = findViewById(R.id.btn_back_maps);
        btnMine = findViewById(R.id.btn_mine_maps);
        btnAll = findViewById(R.id.btn_all_maps);
        etNotesField = findViewById(R.id.et_notesfield_maps);

        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        btnMine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    public void backBtn (View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsActivity.this.gMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!cameraSet) {
                    userLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.clear();
                    gMap.addMarker(new MarkerOptions().position(userLatLong).title("X"))
                            .setDraggable(true);

                    gMap.getUiSettings().setZoomControlsEnabled(true);
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, mapZoomLevel));
                    cameraSet = true;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Asking for permission..
        askLocationPermission();

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("New Marker");
                gMap.clear();
                gMap.addMarker(marker);
                userLatLong = new LatLng(point.latitude, point.longitude);
            }
        });
    }

    private void askLocationPermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLatLong = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                gMap.clear(); // clear old location marker
                gMap.addMarker(new MarkerOptions().position(userLatLong).title("Your location!"));
                gMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).check();
    }
}
