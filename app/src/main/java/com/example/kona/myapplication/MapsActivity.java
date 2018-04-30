package com.example.kona.myapplication;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.SystemClock;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.example.kona.myapplication.AppConfig.GEOMETRY;
import static com.example.kona.myapplication.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.example.kona.myapplication.AppConfig.LATITUDE;
import static com.example.kona.myapplication.AppConfig.LOCATION;
import static com.example.kona.myapplication.AppConfig.LONGITUDE;
import static com.example.kona.myapplication.AppConfig.NAME;
import static com.example.kona.myapplication.AppConfig.OK;
import static com.example.kona.myapplication.AppConfig.PLACE_ID;
import static com.example.kona.myapplication.AppConfig.PROXIMITY_RADIUS;
import static com.example.kona.myapplication.AppConfig.QUEST_RADIUS;
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
    double questlatitude, questlongitude, poilat, poilong;
    GoogleMap mMap;
    Marker locicon, questmarker, barmarker, shopmarker;
    public boolean icons, quest, randomized, newquest;
    double userlongitude, userlatitude;
    JSONObject place, questplace;
    LatLng QLatLng, loc;
    public long Qtime;
    public Quest Questobject = new Quest();
    public Encounter randdenc = new Encounter();
    public String enemyname, QplaceName, Qvicinity;
    MediaPlayer Tune;

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

    public long getQtime() {
        return Qtime;
    }

    public void setQtime(long qtime) {
        Qtime = qtime;
    }

    private static final int REQUEST_FINE_LOCATION = 0;
    private View mLayout;
    private TextView enemytext, mTextField;
    private LocationRequest mLocationRequest;
    Button greenBtn, redBtn, locbutton;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 500; /* 2 sec */
    private long DISTANCE_CHANGE_FOR_UPDATES = 8;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userinfo = auth.getUid();
    JSONArray questlist = new JSONArray();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();

    //nää hommat
    ProgressBar healthbar;
    View menuView;
    boolean menuVisible;
    private int health;

    /**
     * used for setting the player's health for the health bar
     **/
    public void setHealth(int health) {
        this.health = health;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLayout = findViewById(R.id.text);
        enemytext = findViewById(R.id.enemytext);
        greenBtn = findViewById(R.id.fight);
        redBtn = findViewById(R.id.escape);
        locbutton = findViewById(R.id.button4);
        mTextField = findViewById(R.id.Qtimer);

        /**get player's current health for the healthbar**/
        final DatabaseReference hpRef = database.getReference("Player");
        hpRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long currHealth = (long) dataSnapshot.child(auth.getUid()).child("HP").getValue();
                setHealth((int) currHealth);

                final ProgressBar healthBar = findViewById(R.id.healthbar);
                healthBar.setMax(100);
                healthBar.setProgress(health);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /**menu overlay**/
        menuView = findViewById(R.id.menuOverlay);
        menuView.setVisibility(View.GONE);

        greenBtn.setVisibility(View.GONE);
        redBtn.setVisibility(View.GONE);
        enemytext.setVisibility(View.GONE);
        mTextField.setVisibility(View.GONE);


        locbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startLocationUpdates();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(loc)
                        .zoom(19)                    // Sets the orientation of the camera to east
                        .tilt(25)                   // Sets the tilt of the camera to 30 degrees
                        .bearing(1)                // Sets the orientation of the camera to east
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });

        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomized = false;

                greenBtn.setVisibility(View.GONE);
                redBtn.setVisibility(View.GONE);
                enemytext.setVisibility(View.GONE);
            }
        });
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomized = false;
                greenBtn.setVisibility(View.GONE);
                redBtn.setVisibility(View.GONE);
                enemytext.setVisibility(View.GONE);
                Intent fightscreen = new Intent(MapsActivity.this, Gridactivity.class);
                startActivity(fightscreen);

            }
        });
        final DatabaseReference nameRef = database.getReference("Player");
        nameRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String screenname = (String) dataSnapshot.child(auth.getUid()).child("name").getValue();
                TextView pelaajanimi = (TextView) findViewById(R.id.username);
                pelaajanimi.setText(screenname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Start the location update requests from the system
     */
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

    /**
     * Update database when users location changes
     *
     * @param location
     */
    public void onLocationChanged(Location location) {


        if (randdenc.Randomize() && !randomized) {
            final DatabaseReference myRef = database.getReference("Enemies");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Haetaan vihun tiedot, ensin nimi

                    int enemgen = (int) (Math.random() * (4 - 1)) + 1;
                    String enemID = String.valueOf(enemgen);
                    enemyname = (String) dataSnapshot.child("Type").child(enemID).child("Name").getValue();
                    myRef.child("Current").setValue(enemyname);
                    enemytext.setText("You Encountered " + enemyname + "!");
                    randomized = true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            enemytext.setVisibility(View.VISIBLE);
            greenBtn.setVisibility(View.VISIBLE);
            redBtn.setVisibility(View.VISIBLE);
        }

        if (icons) {
            icons = false;
        } else {
            icons = true;
        }
        //set current location to database
        try {
            setUserlatitude(60.164457);
            setUserlongitude(24.933092);
            //setUserlatitude(location.getLatitude() + 0.001);
            //setUserlongitude(location.getLongitude() + 0.001);
            DatabaseReference userRef = database.getReference("Player");
            userRef.child("User").child(userinfo).child("latitude").setValue(getUserlatitude());
            userRef.child("User").child(userinfo).child("longitude").setValue(getUserlongitude());

            //loc = new LatLng(location.getLatitude(), location.getLongitude());
            loc = new LatLng(getUserlatitude(), getUserlongitude());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newquest = (Boolean) dataSnapshot.child("User").child(userinfo).child("Quest").child("newQuest").getValue();

                    loadNearBySupermarket(getUserlatitude(), getUserlongitude(), false);
                    loadNearByshops(getUserlatitude(), getUserlongitude(), false);
                    loadNearByRestaurant(getUserlatitude(), getUserlongitude(), false);
                    loadNearByQuest(getUserlatitude(), getUserlongitude(), newquest);


                        locicon = mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .snippet("")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.locpic2)));


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //camera adjustments
        } catch (Exception e) {
        }

        // New location has now been determined

    }

    protected void onStop() {
        super.onStop();
        Tune.stop();
        Tune.release();


    }

    @Override
    protected void onResume() {
        super.onResume();

        Tune = MediaPlayer.create(getApplicationContext(), R.raw.crimson_idle);
        Tune.start();


        getLastLocation();
        LatLng locat = new LatLng(getUserlatitude(), getUserlongitude());


        if (randdenc.isVictory()) {
            Toast.makeText(getApplicationContext(), " You dealt with " + randdenc.enemyName + " you got 5 money!",
                    Toast.LENGTH_SHORT).show();


        }

        /**piilottaa status barin**/
        /*
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
*/
    }

    /**
     * // Get last known recent location using new Google Play Services SDK (v11+)
     */
    public void getLastLocation() {

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        if (checkPermission()) {

            setUserlatitude(60.164457);
            setUserlongitude(24.933092);

            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {

                                setUserlatitude(60.164457);
                                setUserlongitude(24.933092);

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(getUserlatitude(), getUserlongitude()))
                                        .zoom(19)                    // Sets the orientation of the camera to east
                                        .tilt(25)                   // Sets the tilt of the camera to 30 degrees
                                        .bearing(1)                // Sets the orientation of the camera to east
                                        .build();                   // Creates a CameraPosition from the builder
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                onLocationChanged(location);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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

    /**
     * Get the nearby shops
     *
     * @param latitude
     * @param longitude
     * @param quest     = false
     */
    private void loadNearByshops(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat = latitude;
        this.poilong = longitude;
        mMap.clear();

//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
        // String type = "convenience_store";
        String type = "convenience_store";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(QUEST_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=").append(GOOGLE_BROWSER_API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        parseLocationResult(result, "shop");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    /**
     * Get the nearby supermarkets
     *
     * @param latitude
     * @param longitude
     * @param quest     =  false
     */
    private void loadNearBySupermarket(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat = latitude;
        this.poilong = longitude;
        mMap.clear();

//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
        // String type = "convenience_store";
        String type = "supermarket";
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

                        parseLocationResult(result, "shop");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    /**
     * Get the nearby Restaurants
     *
     * @param latitude
     * @param longitude
     * @param quest     = false
     */
    private void loadNearByRestaurant(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat = latitude;
        this.poilong = longitude;
        mMap.clear();

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

                        parseLocationResult(result, "bar");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }

    private void loadNearByQuest(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat = latitude;
        this.poilong = longitude;
        mMap.clear();

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

                        parseLocationResult(result, "bar");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }

    /**
     * Get nearby restaurants, and make it to a Quest
     * @param latitude
     * @param longitude
     * @param quest determines, if player already has a quest.
     */

    /**
     * handle result from places web Api
     */
    private void parseLocationResult(JSONObject result, String type) {

        String id, place_id, placeName = null, reference, icon, vicinity = null;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {
                if (Questobject.getisQuest()) {
                    DatabaseReference QuestRef = database.getReference("Player");
                    QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
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

                if (quest && type.equals("bar")) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        place = jsonArray.getJSONObject(i);
                        questlist.put(place);
                    }
                    // if player has a quest, get the current quest (Marker) to the map


                    // random generate new quest

                    try {
                        Random rng = new Random();
                        int index = rng.nextInt(questlist.length());
                        questplace = (JSONObject) questlist.get(index);
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
                        QLatLng = new LatLng(questlatitude, questlongitude);
                        questmarker = mMap.addMarker(new MarkerOptions()
                                .title(QplaceName + ": " + Qvicinity)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.quest))
                                .position(QLatLng)
                                .snippet("Get here to reclaim your reward"));


                        Questobject.newQuest(false);
                        Questobject.setQuest(questlatitude, questlongitude, QplaceName, Qvicinity, 90000, true);
                        quest = false;

                    } catch (Exception e) {


                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        place = jsonArray.getJSONObject(i);
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
                            } else {
                                barmarker = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beericon))
                                        .position(latLng)
                                        .snippet("You can find friends and Beer here!")
                                        .title(placeName + " : " + vicinity));

                            }

                        }

                    }

                } else {
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
                        if (type.equals("shop")) {
                            LatLng latLng = new LatLng(poilat, poilong);


                            shopmarker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon))
                                    .position(latLng)
                                    .snippet("You can shop here!")
                                    .title(placeName + " : " + vicinity));


                        } else if (type.equals("bar")) {
                            LatLng latLng = new LatLng(poilat, poilong);
                            if (latLng.equals(Questobject.getQuestLatLng())) {
                                barmarker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .visible(false));

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

        DatabaseReference MyRef = database.getReference("Player");
        MyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test customer marker

                long QendTime = (long) dataSnapshot.child("User").child(userinfo).child("Quest").child("QuestTimeEnd").getValue();
                long resTime = (QendTime) - Questobject.getQuestTime();

                timer(resTime);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        mMap.getUiSettings().setMapToolbarEnabled(false);


        if (loc != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(loc)
                    .zoom(18)                    // Sets the orientation of the camera to east
                    .tilt(25)                   // Sets the tilt of the camera to 30 degrees
                    .bearing(1)                // Sets the orientation of the camera to east
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        try {
            // Customised styling of the base map using a JSON object defined
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            MapsActivity.this, R.raw.new_style));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        //A listener to the location icon to get the updates if they stop
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //if shop is close enough, enter
                if (marker.getSnippet().equals("You can shop here!")) {
                    LatLng shoplatlng = marker.getPosition();
                    double latneartop = (getUserlatitude() + 0.00130);
                    double latnearbot = (getUserlatitude() - 0.00130);
                    double longneartop = (getUserlongitude() + 0.00130);
                    double longnearbot = (getUserlongitude() - 0.00130);

                    if ((shoplatlng.latitude >= latnearbot) &&
                            shoplatlng.latitude <= latneartop &&
                            shoplatlng.longitude >= longnearbot &&
                            shoplatlng.longitude <= longneartop) {
                        Intent intent = new Intent(MapsActivity.this, ShopView.class);
                        startActivity(intent);
                        return true;
                    }

                    return false;
                }
                //if bar is close enough, enter
                else if (marker.getSnippet().equals("You can find friends and Beer here!")) {
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
                    } else {
                        return false;
                    }

                }

                //if quest is close enough, enter
                else if (marker.getSnippet().equals("Get here to reclaim your reward")) {
                    LatLng qlatlng = marker.getPosition();
                    double latneartop = (getUserlatitude() + 0.00230);
                    double latnearbot = (getUserlatitude() - 0.00230);
                    double longneartop = (getUserlongitude() + 0.00230);
                    double longnearbot = (getUserlongitude() - 0.00230);

                    if ((qlatlng.latitude >= latnearbot) &&
                            qlatlng.latitude <= latneartop &&
                            qlatlng.longitude >= longnearbot &&
                            qlatlng.longitude <= longneartop) {

                        Intent fightscreen = new Intent(MapsActivity.this, Gridactivity.class);
                        startActivity(fightscreen);

                        Transaction questmoney = new Transaction();
                        questmoney.addMoney(20);
                        Toast.makeText(getApplicationContext(), "Quest completed!",
                                Toast.LENGTH_SHORT).show();
                        marker.remove();
                        Questobject.setQuest(0.01, 0.01, "noquest", "noquest", 0, false);
                        Questobject.newQuest(false);
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

    /**
     * Check if location permission is granted.
     *
     * @return
     */
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    /**
     * Request the system to grant permission for location.
     */
    private void requestPermissions() {
        // Request the permission
        ActivityCompat.requestPermissions(MapsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    /**
     * @param view
     */
    //toggles menu overlay on
    public void toggleMenu(View view) {
        if (!menuVisible) {
            menuView.setVisibility(View.VISIBLE);
            menuVisible = true;
        } else {
            menuView.setVisibility(View.GONE);
            menuVisible = false;
        }
    }

    public void showJob(View view) {

        if (Questobject.getQuestLatLng().latitude > (0.01)) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(Questobject.getQuestLatLng())
                    .zoom(18)                    // Sets the orientation of the camera to east
                    .tilt(25)                   // Sets the tilt of the camera to 30 degrees
                    .bearing(1)                // Sets the orientation of the camera to east
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        try {
            if (!menuVisible) {
                mTextField.setVisibility(View.VISIBLE);
                menuVisible = true;
            } else {
                mTextField.setVisibility(View.GONE);
                menuVisible = false;
            }

        } catch (Exception e) {

        }


    }

    //opens user profile
    public void profileView(View view) {
        Intent logout = new Intent(this, ProfileActivity.class);
        startActivity(logout);
    }

    public void inventoryView(View view) {
        Intent inventory = new Intent(this, InventoryActivity.class);
        startActivity(inventory);
    }


    public void timer(long time) {

        if (time > 0) {
            new CountDownTimer(time, 1000) {

                public void onTick(long millisUntilFinished) {

                    mTextField.setText("" + String.format("0%d : %d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                    DatabaseReference myRef = database.getReference("Player");
                    myRef.child("User").child(userinfo).child("Quest").child("QuestTimeStart").setValue(SystemClock.uptimeMillis());
                }


                public void onFinish() {
                    mTextField.setText("");

                    Toast.makeText(getApplicationContext(), "You ran out of time!",
                            Toast.LENGTH_SHORT).show();
                    setQtime(0);
                    Questobject.setQuest(0.01, 0.01, "noquest", "noquest", (0), false);
                    Questobject.newQuest(false);


                }
            }.start();
        }
    }


}