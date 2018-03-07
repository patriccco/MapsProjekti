package com.example.kona.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import static com.example.kona.myapplication.AppConfig.GEOMETRY;
import static com.example.kona.myapplication.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.example.kona.myapplication.AppConfig.ICON;
import static com.example.kona.myapplication.AppConfig.LATITUDE;
import static com.example.kona.myapplication.AppConfig.LOCATION;
import static com.example.kona.myapplication.AppConfig.LONGITUDE;
import static com.example.kona.myapplication.AppConfig.NAME;
import static com.example.kona.myapplication.AppConfig.OK;
import static com.example.kona.myapplication.AppConfig.PLACE_ID;
import static com.example.kona.myapplication.AppConfig.PROXIMITY_RADIUS;
import static com.example.kona.myapplication.AppConfig.QUEST_RADIUS;
import static com.example.kona.myapplication.AppConfig.REFERENCE;
import static com.example.kona.myapplication.AppConfig.STATUS;
import static com.example.kona.myapplication.AppConfig.SUPERMARKET_ID;
import static com.example.kona.myapplication.AppConfig.VICINITY;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    GoogleApiClient sGoogleApiClient;
    protected GeoDataClient mGeoDataClient;
    private LocationManager locationManager;
    private static final String TAG = "MyActivity";
    double koord,koord2;
    LatLng boot;
    double questlatitude,questlongitude,poilat,poilong;
    GoogleMap mMap;
    Marker locicon,boothill,questmarker,barmarker,shopmarker;
    boolean icons,quest;
    double userlongitude,userlatitude;
    JSONObject place,questplace;
    LatLng loc,QLatLng;
    String QplaceName,Qvicinity;
    Quest Questobject = new Quest();


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
    private long FASTEST_INTERVAL = 500; /* 2 sec */
    private long DISTANCE_CHANGE_FOR_UPDATES = 8;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userinfo = auth.getUid();
    JSONArray questlist = new JSONArray();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLayout = findViewById(R.id.text);
        final Button button = findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startLocationUpdates();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(loc)
                        .zoom(18)                    // Sets the orientation of the camera to east
                        .tilt(25)                   // Sets the tilt of the camera to 30 degrees
                        .bearing(90)                // Sets the orientation of the camera to east
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE_CHANGE_FOR_UPDATES);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (statusOfGPS == false){
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
        }
        else {
            getLastLocation();
        }
    }

    public void onLocationChanged(Location location) {



        if (icons){
            icons = false;
        }
        else{
            icons = true;
        }

        //set current location to database
        try {

            setUserlatitude(location.getLatitude()+0.001);
            setUserlongitude(location.getLongitude()+0.001);
            DatabaseReference userRef = database.getReference("Player");
            userRef.child("User").child(userinfo).child("latitude").setValue(getUserlatitude());
            userRef.child("User").child(userinfo).child("longitude").setValue(getUserlongitude());





        if (locicon != null)
        {
            locicon.remove();
        }
        // Testisijainti koulu
        //setUserlatitude(60.164380);
        //setUserlongitude(24.933080);

        this.loc = new LatLng(getUserlatitude(), getUserlongitude());

            loadNearBySupermarket(getUserlatitude(),getUserlongitude(),false);
            loadNearByshops(getUserlatitude(), getUserlongitude(),false);
            loadNearByRestaurant(getUserlatitude(), getUserlongitude(),false);

            if(Questobject.getisQuest()) {
                loadNearByQuest(getUserlatitude(), getUserlongitude(), true);
            }


        if(icons) {
            locicon = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon)));
        }
        else {
            locicon = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon2)));
        }

        //camera adjustments

        }catch (Exception e) {
        }

        // New location has now been determined

    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        if (checkPermission()){

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
                });}
    }

    private void loadNearByshops(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat =latitude;
        this.poilong =longitude;
        mMap.clear();

//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
        // String type = "convenience_store";
        String type = "convenience_store";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=").append(GOOGLE_BROWSER_API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        parseLocationResult(result,"shop");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }
    private void loadNearBySupermarket(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat =latitude;
        this.poilong =longitude;
        mMap.clear();

//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
        // String type = "convenience_store";
        String type = "supermarket";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyCjnCj2kRnT5SPL4AsyEld_3yFPUTjHdMM");

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        parseLocationResult(result,"shop");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    // get points of interests from places web api
    private void loadNearByRestaurant(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat =latitude;
        this.poilong =longitude;
        mMap.clear();


        boothill = mMap.addMarker(new MarkerOptions()
                .position(boot)
                .title("Bootyhill")
                .snippet("Bootyhill"));
//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
        String type = "bar";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyCjnCj2kRnT5SPL4AsyEld_3yFPUTjHdMM");

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        parseLocationResult(result,"bar");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }
    private void loadNearByQuest(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat =latitude;
        this.poilong =longitude;
        mMap.clear();

        boothill = mMap.addMarker(new MarkerOptions()
                .position(boot)
                .title("Bootyhill")
                .snippet("Bootyhill"));
//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
        String type = "bar";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(QUEST_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyCjnCj2kRnT5SPL4AsyEld_3yFPUTjHdMM");

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        parseLocationResult(result,"bar");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }
    //handle result from places web Api
    private void parseLocationResult(JSONObject result, String type) {

        String id, place_id, placeName = null, reference, icon, vicinity = null;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                if (quest && type.equals("bar")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            place = jsonArray.getJSONObject(i);
                            questlist.put(place);
                        }
                    // if player has a quest, get the current quest to the map
                    if(Questobject.getisQuest()){

                        Quest newQuestobject = new Quest();

                        DatabaseReference QuestRef = database.getReference("Player");
                        QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Test customer marker
                                double qlat = (double) dataSnapshot.child("User").child(userinfo).child("Quest").child("latitude").getValue();
                                double qlong = (double) dataSnapshot.child("User").child(userinfo).child("Quest").child("longitude").getValue();
                                LatLng qlatlng = new LatLng(qlat, qlong);

                                questmarker = mMap.addMarker(new MarkerOptions()
                                        .title("Quest")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.quest))
                                        .position(qlatlng)
                                        .snippet("Get here to reclaim your reward"));

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });




                    }

                    // random generate new quest
                    else {

                        Random rng = new Random();
                        int index = rng.nextInt(questlist.length());
                        questplace = (JSONObject) questlist.get(index);

                        id = questplace.getString(SUPERMARKET_ID);
                        place_id = questplace.getString(PLACE_ID);
                        if (!questplace.isNull(NAME)) {
                            QplaceName = questplace.getString(NAME);
                        }
                        if (!questplace.isNull(VICINITY)) {
                            Qvicinity = questplace.getString(VICINITY);

                        }
                        questlatitude = questplace.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                                .getDouble(LATITUDE);
                        questlongitude = questplace.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                                .getDouble(LONGITUDE);

                        reference = questplace.getString(REFERENCE);
                        icon = questplace.getString(ICON);

                        QLatLng = new LatLng(questlatitude, questlongitude);

                        questmarker = mMap.addMarker(new MarkerOptions()
                                .title(QplaceName + ": " + Qvicinity)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.quest))
                                .position(QLatLng)
                                .snippet("Get here to reclaim your reward"));

                        Questobject.setQuest(questlatitude,questlongitude,QplaceName,Qvicinity,true);


                    }

                    //if user icon is walking, make it running
                    if(icons) {
                        locicon.remove();
                        locicon = mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon)));
                    }
                    else {
                        locicon.remove();
                        locicon = mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon2)));
                    }




                    for (int i = 0; i < jsonArray.length(); i++) {
                        place = jsonArray.getJSONObject(i);

                        id = place.getString(SUPERMARKET_ID);
                        place_id = place.getString(PLACE_ID);
                        if (!place.isNull(NAME)) {
                            placeName = place.getString(NAME);
                        }
                        if (!place.isNull(VICINITY)) {
                            vicinity = place.getString(VICINITY);
                        }
                        poilat = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                                .getDouble(LATITUDE);
                        poilong = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                                .getDouble(LONGITUDE);
                        reference = place.getString(REFERENCE);
                        icon = place.getString(ICON);

                        //if type is shop, create shopmarker
                        if (type.equals("shop")) {
                            LatLng latLng = new LatLng(poilat, poilong);
                                shopmarker = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon))
                                        .position(latLng)
                                        .snippet("You can shop here!")
                                        .title(placeName + " : " + vicinity));


                               // if type is bar, create barmarker
                        } else if (type.equals("bar")) {
                            LatLng latLng = new LatLng(poilat, poilong);
                            //
                                if (latLng.equals(Questobject.getQuestLatLng())) {
                                }
                            else {
                                barmarker = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beericon))
                                        .position(latLng)
                                        .snippet("You can find friends and Beer here!")
                                        .title(placeName + " : " + vicinity));

                            }

                        }

                    }

                } else{
                        for (int i = 0; i < jsonArray.length(); i++) {
                            place = jsonArray.getJSONObject(i);
                            id = place.getString(SUPERMARKET_ID);
                            place_id = place.getString(PLACE_ID);
                            if (!place.isNull(NAME)) {
                                placeName = place.getString(NAME);
                            }
                            if (!place.isNull(VICINITY)) {
                                vicinity = place.getString(VICINITY);
                            }
                            poilat = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                                    .getDouble(LATITUDE);
                            poilong = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                                    .getDouble(LONGITUDE);
                            reference = place.getString(REFERENCE);
                            icon = place.getString(ICON);
                            if (type.equals("shop")) {
                                LatLng latLng = new LatLng(poilat, poilong);


                                    shopmarker = mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon))
                                            .position(latLng)
                                            .snippet("You can shop here!")
                                            .title(placeName + " : " + vicinity));


                            } else if (type.equals("bar")) {
                                LatLng latLng = new LatLng(poilat, poilong);
                                if(Questobject.getisQuest()){
                                if (latLng.equals(Questobject.getQuestLatLng())) {
                                    barmarker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .visible(false));
                                }
                                } else {
                                    barmarker = mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.beericon))
                                            .position(latLng)
                                            .snippet("You can find friends and Beer here!")
                                            .title(placeName + " : " + vicinity));

                                }
                            }

                        }
                     }
            }
        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        boot = new LatLng(60.164685, 24.933300);
        mMap = map;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(boot)
                .zoom(18)                    // Sets the orientation of the camera to east
                .tilt(25)                   // Sets the tilt of the camera to 30 degrees
                .bearing(90)                // Sets the orientation of the camera to east
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //mMap.setOnPoiClickListener(this);
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
        DatabaseReference myRef = database.getReference("Koordinaatit");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test customer marker
                koord = (Double) dataSnapshot.child("K1").getValue();
                koord2 = (Double) dataSnapshot.child("K2").getValue();
                boot = new LatLng(koord, koord2);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //A listener to the location icon to get the updates if they stop
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                //Testmarker
                if(marker.equals(boothill)) {
                    loadNearByshops(getUserlatitude(), getUserlongitude(),false);
                    loadNearBySupermarket(getUserlatitude(),getUserlongitude(),false);
                    loadNearByRestaurant(getUserlatitude(), getUserlongitude(),false);
                    loadNearByQuest(getUserlatitude(),getUserlongitude(),true);

                    return true;
                }

                else if(marker.equals(locicon)){
                    return false;
                }

                //if shop is close enough, enter
                else if(marker.getSnippet().equals("You can shop here!")){
                    LatLng shoplatlng = marker.getPosition();
                    double latneartop = (getUserlatitude() + 0.00130);
                    double latnearbot = (getUserlatitude() - 0.00130);
                    double longneartop = (getUserlongitude() + 0.00130);
                    double longnearbot = (getUserlongitude() - 0.00130);

                    if ((shoplatlng.latitude >= latnearbot) &&
                            shoplatlng.latitude <= latneartop &&
                            shoplatlng.longitude >= longnearbot &&
                            shoplatlng.longitude <= longneartop) {
                        return true;
                    }

                    return false;
                    }
                    //if bar is close enough, enter
                else if(marker.getSnippet().equals("You can find friends and Beer here!")){
                    LatLng Barlatlng = marker.getPosition();
                    double latneartop = (getUserlatitude() + 0.00130);
                    double latnearbot = (getUserlatitude() - 0.00130);
                    double longneartop = (getUserlongitude() + 0.00130);
                    double longnearbot = (getUserlongitude() - 0.00130);

                    if ((Barlatlng.latitude >= latnearbot) &&
                            Barlatlng.latitude <= latneartop &&
                            Barlatlng.longitude >= longnearbot &&
                            Barlatlng.longitude <= longneartop) {

                        DatabaseReference myRef = database.getReference("Player");
                        myRef.child("User").child(userinfo).child("Place").setValue(marker.getTitle());
                        Intent bar = new Intent(MapsActivity.this, Barview.class);
                        startActivity(bar);
                        return true;
                    }
                    else{
                        return false;
                    }

                    }

                //if quest is close enough, enter
                else if(marker.getSnippet().equals("Get here to reclaim your reward")){
                    LatLng qlatlng = marker.getPosition();
                    double latneartop = (getUserlatitude() + 0.00130);
                    double latnearbot = (getUserlatitude() - 0.00130);
                    double longneartop = (getUserlongitude() + 0.00130);
                    double longnearbot = (getUserlongitude() - 0.00130);

                    if ((qlatlng.latitude >= latnearbot) &&
                            qlatlng.latitude <= latneartop &&
                            qlatlng.longitude >= longnearbot &&
                            qlatlng.longitude <= longneartop) {

                        Transaction questmoney = new Transaction();
                        questmoney.addMoney(15);
                        Toast.makeText(getApplicationContext(), "Quest completed!",
                                Toast.LENGTH_SHORT).show();
                        marker.remove();
                        Questobject.setQuest(0,0, "noquest", "noquest", false);
                        startLocationUpdates();
                        return true;
                    }

                    return false;
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


    /**buttons**/

    /** Called when the user taps the Log Out button */
    public void TologOut(View view) {
        Intent logout = new Intent(this, LogOutActivity.class);

        startActivity(logout);
    }

    /**opens main menu view**/
    public void openMenu(View view) {
        // opens main menu
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }


}