package com.example.kona.myapplication;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnPoiClickListener {
    private LocationManager locationManager;
    private static final String TAG = "MyActivity";
    double koord, koord2;
    GoogleMap mMap;
    Marker locicon;
    double userlongitude, userlatitude;

    public double getUserlongitude() {
        return userlongitude;
    }

    public void setUserlongitude(double longitude) {
        this.userlongitude = longitude;
    }

    public double getUserlatitude() {
        return userlatitude;
    }

    public void setUserlatitude(double latitude) {
        this.userlatitude = latitude;
    }

    private static final int REQUEST_FINE_LOCATION = 0;
    private View mLayout;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private String userkey, placeValue;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userinfo = auth.getUid();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLayout = findViewById(R.id.text);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    protected void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (statusOfGPS == false) {
            Snackbar.make(mLayout, "Turn on GPS.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    getLastLocation();
                }
            }).show();

        }

        if (checkPermission()) {
            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        } else {
            getLastLocation();
        }
    }

    public void onLocationChanged(Location location) {
        if (auth.getUid() != null) {
            DatabaseReference userRef = database.getReference("Player");
            userRef.child("User").child(auth.getUid()).child("latitude").setValue(getUserlatitude());
            userRef.child("User").child(auth.getUid()).child("longitude").setValue(getUserlongitude());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String UID = (String) dataSnapshot.child(userinfo).getValue();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if (locicon != null) {
                locicon.remove();
            }
            setUserlatitude(location.getLatitude());
            setUserlongitude(location.getLongitude());
            LatLng loc = new LatLng(getUserlatitude(), getUserlongitude());


            locicon = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title("You")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_lautaus)));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(loc)
                    .zoom(17)                    // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .bearing(90)                // Sets the orientation of the camera to east
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            // New location has now been determined
        }
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        if (checkPermission()) {
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                onLocationChanged(location);


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();

                            Snackbar.make(mLayout, "Location is required to display the preview.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Request the permission
                                    ActivityCompat.requestPermissions(MapsActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_FINE_LOCATION);
                                }
                            }).show();
                        }
                    });
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnPoiClickListener(this);


        try {
            // Customised styling of the base map using a JSON object defined

            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            MapsActivity.this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        // Write a message to the database
        DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {

                    //Loop 1 to go through all the child nodes of users
                    for (DataSnapshot placeSnapshot : uniqueKeySnapshot.getChildren()) {
                        //loop 2 to go through all the child nodes of books node

                        userkey = placeSnapshot.getKey().toString();
                        placeValue = placeSnapshot.getValue().toString();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        myRef = database.getReference("Koordinaatit");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                koord = (Double) dataSnapshot.child("K1").getValue();
                koord2 = (Double) dataSnapshot.child("K2").getValue();

                LatLng boot = new LatLng(koord, koord2);
                mMap.addMarker(new MarkerOptions().position(boot).title("Bootyhill"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.equals(locicon)) {
                    startLocationUpdates();
                    return true;
                }
                return false;

            }
        });
        startLocationUpdates();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        // Request the permission
        ActivityCompat.requestPermissions(MapsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {

        double latneartop = (getUserlatitude() + 0.00150);
        double latnearbot = (getUserlatitude() - 0.00150);
        double longneartop = (getUserlongitude() + 0.00150);
        double longnearbot = (getUserlongitude() - 0.00150);

        if ((poi.latLng.latitude >= latnearbot) &&
                poi.latLng.latitude <= latneartop &&
                poi.latLng.longitude >= longnearbot &&
                poi.latLng.longitude <= longneartop) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Player");
            myRef.child("User").child(auth.getUid()).child("Place").setValue(poi.placeId);

            Intent bar = new Intent(MapsActivity.this, Barview.class);
            startActivity(bar);


            Toast.makeText(getApplicationContext(), "Radius noin 40 metriä" +
                            "/n" + userkey +
                            "/n" + placeValue,
                    Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Called when the user taps the Log Out button
     */
    public void TologOut(View view) {
        //Do something in response to button

        Intent logout = new Intent(this, LogOutActivity.class);
        super.finish();

        startActivity(logout);


    }


}