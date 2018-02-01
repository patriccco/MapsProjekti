package com.example.kona.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public double koord;
    public double koord2;
    private static final String TAG = "MyActivity";

    public double getKoord() {
        return koord;
    }

    public void setKoord(double koord) {
        this.koord = koord;
    }

    public double getKoord2() {
        return koord2;
    }

    public void setKoord2(double koord2) {
        this.koord2 = koord2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {







        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Koordinaatit");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    koord = (Double) dataSnapshot.child("K1").getValue();
                    koord2 = (Double) dataSnapshot.child("K2").getValue();
                    Log.d(TAG, "Value is: " + getKoord());
                    Log.d(TAG, "Value is: " + koord2);
                } else {
                    koord = 100;
                }

                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(koord, koord2);
                Log.d(TAG, "Value is: " + getKoord());
                Log.d(TAG, "Value is: " + sydney);
                mMap.addMarker(new MarkerOptions().position(sydney).title("EI Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
