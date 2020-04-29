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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.firebase.firestore.GeoPoint;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

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

        spinGender = findViewById(R.id.spin_gender_maps);
        spinAge = findViewById(R.id.spin_age_maps);
        spinOtherdiseases = findViewById(R.id.spin_otherdiseases_maps);
        spinStatus = findViewById(R.id.spin_status_maps);

        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Validate inputs
                String gender = spinGender.getSelectedItem().toString();
                if(gender.equals("Choose gender") || gender.equals("Vælg køn")){
                    spinGender.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_gender_not_chosen, Toast.LENGTH_LONG).show();
                }

                String age = spinAge.getSelectedItem().toString();
                if(age.equals("Choose age") || age.equals("Vælg alder")){
                    spinAge.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_age_not_chosen, Toast.LENGTH_LONG).show();
                }

                String otherDiseases = spinOtherdiseases.getSelectedItem().toString();
                if(otherDiseases.equals("Other diseases?") || otherDiseases.equals("Andre sygdomme?")){
                    spinOtherdiseases.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_other_diseases_not_chosen, Toast.LENGTH_LONG).show();
                }

                String healthStatus = spinStatus.getSelectedItem().toString();
                if(healthStatus.equals("Status?")){
                    spinStatus.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_status_not_chosen, Toast.LENGTH_LONG).show();
                }

                // fetch current position
                GeoPoint geoPoint = new GeoPoint(userLatLong.latitude, userLatLong. longitude);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // TODO: Find out what collection, we use in firebase.

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearFields();
                startActivity(new Intent(MapsActivity.this, MenuActivity.class));
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

                // Below is spinner-related
        ArrayAdapter<CharSequence> spinAgeAdapter = ArrayAdapter.createFromResource(this, R.array.spin_age_maps, android.R.layout.simple_spinner_item);
        spinAgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAge.setAdapter(spinAgeAdapter);
        spinAge.setOnItemSelectedListener(MapsActivity.this);

        ArrayAdapter<CharSequence> spinOtherDiseases = ArrayAdapter.createFromResource(this, R.array.spin_otherdiseases_maps, android.R.layout.simple_spinner_item);
        spinOtherDiseases.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinOtherdiseases.setAdapter(spinOtherDiseases);
        spinOtherdiseases.setOnItemSelectedListener(MapsActivity.this);

        ArrayAdapter<CharSequence> spinGenderAdapter = ArrayAdapter.createFromResource(this, R.array.spin_gender_maps, android.R.layout.simple_spinner_item);
        spinGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGender.setAdapter(spinGenderAdapter);
        spinGender.setOnItemSelectedListener(MapsActivity.this);

        ArrayAdapter<CharSequence> spinStatusAdapter = ArrayAdapter.createFromResource(this, R.array.spin_status_maps, android.R.layout.simple_spinner_item);
        spinStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStatus.setAdapter(spinStatusAdapter);
        spinStatus.setOnItemSelectedListener(MapsActivity.this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    public void onNothingSelected(AdapterView<?> arg0) {}


    private void clearFields() {
        spinAge.setSelection(0);
        spinGender.setSelection(0);
        spinOtherdiseases.setSelection(0);
        spinStatus.setSelection(0);
        etNotesField.setText("");
        gMap.clear();
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

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
