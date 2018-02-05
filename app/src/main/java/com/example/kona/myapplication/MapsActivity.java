package com.example.kona.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueNetwork.setText(longitudeNetwork + "");
                    latitudeValueNetwork.setText(latitudeNetwork + "");
                    Toast.makeText(MainActivity.this, "Network Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

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
                    koord = (Double) dataSnapshot.child("K1").getValue();
                    koord2 = (Double) dataSnapshot.child("K2").getValue();
                    Log.d(TAG, "Value is: " + getKoord());
                    Log.d(TAG, "Value is: " + koord2);


                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(koord, koord2);
                Log.d(TAG, "Value is: " + getKoord());
                Log.d(TAG, "Value is: " + sydney);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Bootyhill"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
}

}
