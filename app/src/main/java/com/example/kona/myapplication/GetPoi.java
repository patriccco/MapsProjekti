package com.example.kona.myapplication;

import android.widget.Toast;

import com.google.android.gms.maps.model.PointOfInterest;

/**
 * Created by kona on 13.2.2018.
 */

public class GetPoi extends MapsActivity {
    double latneartop = (getUserlongitude() - 0.00150);
    double latnearbot = (getUserlatitude() - 0.00150);
    double longneartop = (getUserlatitude() + 0.00150);
    double longnearbot = (getUserlatitude() + 0.00150);

    public void onPoiClick(PointOfInterest poi) {

        if ((poi.latLng.latitude >= latnearbot )) {


            Toast.makeText(getApplicationContext(), "Clicked: " +
                            poi.name + "\nPlace ID:" + poi.placeId +
                            "\nLatitude:" + poi.latLng.latitude +
                            " Longitude:" + poi.latLng.longitude,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
