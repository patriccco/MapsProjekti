package com.example.kona.myapplication;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Looper;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import android.widget.ImageView;
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

import static com.example.kona.myapplication.AppConfig.BAR_RADIUS;
import static com.example.kona.myapplication.AppConfig.GEOMETRY;
import static com.example.kona.myapplication.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.example.kona.myapplication.AppConfig.LATITUDE;
import static com.example.kona.myapplication.AppConfig.LOCATION;
import static com.example.kona.myapplication.AppConfig.LONGITUDE;
import static com.example.kona.myapplication.AppConfig.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.example.kona.myapplication.AppConfig.NAME;
import static com.example.kona.myapplication.AppConfig.OK;
import static com.example.kona.myapplication.AppConfig.REQUEST_FINE_LOCATION;
import static com.example.kona.myapplication.AppConfig.SHOP_RADIUS;
import static com.example.kona.myapplication.AppConfig.STATUS;
import static com.example.kona.myapplication.AppConfig.VICINITY;
import static com.example.kona.myapplication.AppConfig.UPDATE_INTERVAL;
import static com.example.kona.myapplication.AppConfig.FASTEST_INTERVAL;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

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
    public Transaction transaction = new Transaction();
    public Quest Questobject = new Quest();
    public Encounter questencounter = new Encounter();
    public Avatar avatar = new Avatar();
    public String enemyname, QplaceName, Qvicinity;
    MediaPlayer Tune;
    double latneartop, latnearbot, longneartop, longnearbot;

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

    public void setQtime(long qtime) {
        Qtime = qtime;
    }

    private View mLayout;
    private ImageView profileavatar;
    private TextView enemytext, mTextField, questinfo, Moneyview;
    private LocationRequest mLocationRequest;
    Button greenBtn, redBtn, locbutton;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userinfo = auth.getUid();
    JSONArray questlist = new JSONArray();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();

    View menuView;
    boolean menuVisible;
    private int health;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

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
        questinfo = findViewById(R.id.questinfo);
        transaction.getPlayerMoney();
        avatar.getDatabaseAvatar();
        Moneyview = findViewById(R.id.profilemoney);
        profileavatar = findViewById(R.id.avatar);
        enemyname = "no";


        /**get player's current health for the healthbar**/
        final DatabaseReference hpRef = database.getReference("Player");
        hpRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long currHealth = (long) dataSnapshot.child(auth.getUid()).child("HP").getValue();


                int showhealth = (int) currHealth;


                ProgressBar healthBar = findViewById(R.id.healthbar);
                healthBar.setMax(100);
                healthBar.setProgress(showhealth);
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

                final DatabaseReference encRef = database.getReference("User");
                encRef.child(auth.getUid()).child("enemy").setValue(enemyname);

                Intent fightscreen = new Intent(MapsActivity.this, Gridactivity.class);
                startActivity(fightscreen);

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
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES);

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

        latneartop = (getUserlatitude() + 0.00130);
        latnearbot = (getUserlatitude() - 0.00130);
        longneartop = (getUserlongitude() + 0.00130);
        longnearbot = (getUserlongitude() - 0.00130);
        avatar.getDatabaseAvatar();
        Questobject.getQuestVicinity();

        if (questencounter.Randomize() && !randomized && enemyname.equals("no")) {
            final DatabaseReference myRef = database.getReference("Enemies");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Haetaan vihun tiedot, ensin nimi

                    int enemgen = (int) (Math.random() * (4 - 1)) + 1;
                    String enemID = String.valueOf(enemgen);
                    enemyname = (String) dataSnapshot.child("Type").child(enemID).child("Name").getValue();
                    myRef.child("Current").setValue(enemyname);

                    final DatabaseReference encRef = database.getReference("Player");
                    encRef.child("User").child(auth.getUid()).child("enemy").setValue(enemyname);
                    enemytext.setText(getString(R.string.youencountered) + enemyname + "!");
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

        //set current location to database
        try {
            //setUserlatitude(60.164457);
            //setUserlongitude(24.933092);
            setUserlatitude(location.getLatitude() + 0.001);
            setUserlongitude(location.getLongitude() + 0.001);
            DatabaseReference userRef = database.getReference("Player");
            userRef.child("User").child(userinfo).child("latitude").setValue(getUserlatitude());
            userRef.child("User").child(userinfo).child("longitude").setValue(getUserlongitude());

            loc = new LatLng(getUserlatitude(), getUserlongitude());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newquest = (Boolean) dataSnapshot.child("User").child(userinfo).child("Quest").child("newQuest").getValue();
                    LoadNearByPlaces(getUserlatitude(), getUserlongitude(), false);
                    loadNearByBar(getUserlatitude(), getUserlongitude(), newquest);

                    //get avatar from avatarClass
                    String avatarname = avatar.toBallAvatar();
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(GetImage(MapsActivity.this, avatarname));
                    transaction.getPlayerMoney();
                    int profilemoney = transaction.getMoney();
                    Moneyview.setText(profilemoney + " ");

                    profileavatar.setBackground(GetImage(MapsActivity.this, avatarname));
                    //Set avatar to location icon
                    locicon = mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .snippet("")
                            .icon(markerIcon));


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
        Tune.release();


    }

    protected void onPause() {
        super.onPause();
        Tune.stop();
        Tune.release();

    }

    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tune = MediaPlayer.create(getApplicationContext(), R.raw.crimson_idle);
        Tune.start();
        getLastLocation();

        if (questencounter.isVictory()) {
            Toast.makeText(getApplicationContext(), R.string.questreward + "\n" + " you got 20 money!",
                    Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * // Get last known recent location using new Google Play Services SDK (v11+)
     */
    public void getLastLocation() {

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        if (checkPermission()) {


            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {

                                setUserlatitude(location.getLatitude());
                                setUserlongitude(location.getLongitude());

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
    private void LoadNearByPlaces(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat = latitude;
        this.poilong = longitude;
        mMap.clear();

        String type = "convenience_store,supermarket,liquor_store";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(SHOP_RADIUS);
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
     * This method gets the reuslts of nearby bars
     *
     * @param latitude
     * @param longitude
     * @param quest     if quest ha been taken, one of the bars will be a quest marker
     */
    private void loadNearByBar(double latitude, double longitude, boolean quest) {
        this.quest = quest;
        this.poilat = latitude;
        this.poilong = longitude;
        mMap.clear();

        String type = "bar";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(BAR_RADIUS);
        googlePlacesUrl.append("&type=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=").append(GOOGLE_BROWSER_API_KEY);

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
        Questobject.isQuest();

        String placeName = null, vicinity = null;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {
                if (Questobject.getIsquest()) {
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

                        questinfo.setText(Qvicinity);
                        Questobject.newQuest(false);
                        Questobject.setQuest(questlatitude, questlongitude, QplaceName, Qvicinity, 90000, true);
                        quest = false;

                    } catch (Exception e) {


                    }
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
                                .snippet(getString(R.string.shopmarker))
                                .title(placeName + " : " + vicinity));


                        // if type is bar, create barmarker
                    } else if (type.equals("bar")) {
                        LatLng latLng = new LatLng(poilat, poilong);
                        //
                        {
                            barmarker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.beericon))
                                    .position(latLng)
                                    .snippet(getString(R.string.beermarker))
                                    .title(placeName + " : " + vicinity));
                            Questobject.getQuestLatLng();
                            if (latLng.equals(Questobject.qlatlng)) {
                                barmarker.remove();
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
                String screenname = dataSnapshot.child("User").child(auth.getUid()).child("name").getValue().toString();
                TextView pelaajanimi = findViewById(R.id.username);
                if(screenname.equals("newPlayer")){
                    Intent toprofile = new Intent(MapsActivity.this, ProfileActivity.class);
                    startActivity(toprofile);
                    finish();}

                pelaajanimi.setText(screenname);
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
                if (marker.getSnippet().equals(getString(R.string.shopmarker))) {
                    LatLng shoplatlng = marker.getPosition();

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
                else if (marker.getSnippet().equals(getString(R.string.beermarker))) {
                    LatLng Barlatlng = marker.getPosition();

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
                else if (marker.getSnippet().equals(getString(R.string.questmarker))) {
                    LatLng qlatlng = marker.getPosition();

                    if ((qlatlng.latitude >= latnearbot) &&
                            qlatlng.latitude <= latneartop &&
                            qlatlng.longitude >= longnearbot &&
                            qlatlng.longitude <= longneartop) {

                        Intent fightscreen = new Intent(MapsActivity.this, Gridactivity.class);
                        startActivity(fightscreen);

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
        if (!Questobject.getVicinity().equals("noquest")) {
            questinfo.setText(Questobject.getVicinity());
            questinfo.setVisibility(View.VISIBLE);
            Questobject.getQuestLatLng();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(Questobject.qlatlng)
                    .zoom(18)                    // Sets the orientation of the camera to east
                    .tilt(25)                   // Sets the tilt of the camera to 30 degrees
                    .bearing(1)                // Sets the orientation of the camera to east
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            try {
                if (menuVisible) {
                    mTextField.setVisibility(View.VISIBLE);
                    questinfo.setVisibility(View.VISIBLE);
                    menuVisible = false;
                } else {
                    mTextField.setVisibility(View.GONE);
                    questinfo.setVisibility(View.GONE);
                    menuVisible = true;
                }

            } catch (Exception e) {

            }

        } else {
            mTextField.setVisibility(View.GONE);
            questinfo.setVisibility(View.GONE);

        }
    }

    //opens user profile
    public void profileView(View view) {
        Intent logout = new Intent(this, ProfileActivity.class);
        startActivity(logout);
        finish();
    }

    public void inventoryView(View view) {
        Intent inventory = new Intent(this, InventoryActivity.class);
        startActivity(inventory);
        finish();
    }

    /**
     * make the drawable path for location icon
     *
     * @param drawable
     * @return
     */
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void timer(long time) {
        if (time > 0) {
            new CountDownTimer(time, 1000) {

                public void onTick(long millisUntilFinished) {
                    mTextField.setText("" + String.format("0%d : %d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                }

                public void onFinish() {
                    mTextField.setText("");
                    questinfo.setText("");

                    Toast.makeText(getApplicationContext(), R.string.outoftime,
                            Toast.LENGTH_SHORT).show();
                    setQtime(0);
                    Questobject.setQuest(0.01, 0.01, "noquest", "noquest", (0), false);
                    Questobject.newQuest(false);
                    questinfo.setText("");
                }
            }.start();
        }
    }


}