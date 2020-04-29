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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;

// reference: https://firebase.google.com/docs/auth/web/manage-users
// reference: spinner: https://www.youtube.com/watch?v=on_OrrX7Nw4
// reference: spinner: https://developer.android.com/guide/topics/ui/controls/spinner
// reference for Google Maps + API: https://developers.google.com/maps/documentation/android-sdk/get-api-key
// reference for Google Maps, User location: https://www.youtube.com/watch?v=pNeuuImirHY
// reference for getting all markers shown: https://stackoverflow.com/questions/56837831/marker-from-firestore-map-data-type

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
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
    private BitmapDescriptor markerColor;

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

        initializeSpinnerLogic();

        initializeButtonLogic();

    }

    private void initializeButtonLogic() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate inputs
                String gender = spinGender.getSelectedItem().toString();
                if(gender.equals("Choose gender") || gender.equals("Vælg køn")){
                    spinGender.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_gender_not_chosen, Toast.LENGTH_LONG).show();
                    return;
                }

                String age = spinAge.getSelectedItem().toString();
                if(age.equals("Choose age") || age.equals("Vælg alder")){
                    spinAge.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_age_not_chosen, Toast.LENGTH_LONG).show();
                    return;
                }

                String otherDiseases = spinOtherdiseases.getSelectedItem().toString();
                if(otherDiseases.equals("Other diseases?") || otherDiseases.equals("Andre sygdomme?")){
                    spinOtherdiseases.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_other_diseases_not_chosen, Toast.LENGTH_LONG).show();
                    return;
                }

                String healthStatus = spinStatus.getSelectedItem().toString();
                if(healthStatus.equals("Status?")){
                    spinStatus.requestFocus();
                    Toast.makeText(MapsActivity.this, R.string.spinner_status_not_chosen, Toast.LENGTH_LONG).show();
                    return;
                }

                String notes = etNotesField.getText().toString();

                // fetch current position
                GeoPoint geoPoint = new GeoPoint(userLatLong.latitude, userLatLong. longitude);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> newCase = new HashMap();
                newCase.put("Gender", gender);
                newCase.put("Age", age);
                newCase.put("OtherDiseases", otherDiseases);
                newCase.put("Status", healthStatus);
                newCase.put("Notes", notes);
                newCase.put("Geopoint", geoPoint);

                db.collection("maps_corona_data")
                        .add(newCase)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Added data with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed adding spot", e);
                            }
                        });
                Toast.makeText(MapsActivity.this, "Data added!", Toast.LENGTH_LONG).show();
                clearFields();
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
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("maps_corona_data")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                        HashMap hashMap = (HashMap) documentSnapshot.getData();
                                        String gender = (String) hashMap.get("Gender");
                                        String age = (String) hashMap.get("Age");
                                        String otherDiseases = (String) hashMap.get("OtherDiseases");
                                        String status = (String) hashMap.get("Status");
                                        String notes = (String) hashMap.get("Notes");
                                        GeoPoint geo = (GeoPoint) hashMap.get("Geopoint");

                                        LatLng latLng = new LatLng(geo.getLatitude(), geo.getLongitude());

                                        // set marker color according to the status
                                        if(status.equals("Clean") || status.equals("Rask")){
                                            markerColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                        } else if(status.equals("Infected") || status.equals("Smittet")){
                                            markerColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                                        } else if(status.equals("Dead") || status.equals("Død")) {
                                            markerColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                        }
                                        gMap.addMarker(new MarkerOptions().position(latLng).title(status).snippet("Gender: " + gender + ", " + "Age: " + age).icon(markerColor))
                                                .setDraggable(true);
                                        }
                                }else {
                                    // TODO: handle error here
                                }
                                if (task.getResult().isEmpty()) {
                                    Toast.makeText(MapsActivity.this, R.string.no_markers_registered_yet, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // Sets up spinners logic, when pressed.
    private void initializeSpinnerLogic() {
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

    // Required in regards to use of spinners
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
