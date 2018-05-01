package com.example.kona.myapplication;

/**
 * Created by kona on 1.3.2018.
 */

public final class AppConfig {

    public static final String TAG = "gplaces";
    public static final String RESULTS = "results";
    public static final String STATUS = "status";

    public static final String OK = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";

    //    Key for nearby places json from google
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String SUPERMARKET_ID = "id";
    public static final String NAME = "name";
    public static final String PLACE_ID = "place_id";
    public static final String VICINITY = "vicinity";

    // remember to change the browser api key
    public static final String GOOGLE_BROWSER_API_KEY =
            "AIzaSyCjnCj2kRnT5SPL4AsyEld_3yFPUTjHdMM";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int SHOP_RADIUS = 300;
    public static final int BAR_RADIUS = 450;
    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 8; // 8 meters
    // The minimum time between updates in milliseconds

    public static final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    public static final long FASTEST_INTERVAL = 500; /* 2 sec */
    public static final int REQUEST_FINE_LOCATION = 0;

}