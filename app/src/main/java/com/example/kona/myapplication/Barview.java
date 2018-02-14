package com.example.kona.myapplication;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kona.myapplication.LogOutActivity;
import com.example.kona.myapplication.Login;
import com.example.kona.myapplication.MapsActivity;
import com.example.kona.myapplication.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;


public class Barview extends AppCompatActivity{
    ArrayList <String> PlaceNames = new ArrayList<>();
    private final static String TAG = "TÄÄ";

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_bar);

            // Find the ListView resource.
            mainListView = findViewById( R.id.lista);

            // Create and populate a List of planet names.
            String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
                    "Jupiter", "Saturn", "Uranus", "Neptune"};
            ArrayList<String> planetList = new ArrayList<String>();

            // Create ArrayAdapter using the planet list.
            listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

            // Add more planets. If you passed a String[] instead of a List<String>
            // into the ArrayAdapter constructor, you must not add more items.
            // Otherwise an exception will occur.
            listAdapter.add( "Ceres" );
            listAdapter.add( "Pluto" );
            listAdapter.add( "Haumea" );
            listAdapter.add( "Makemake" );
            listAdapter.add( "Eris" );

            // Set the ArrayAdapter as the ListView's adapter.
            mainListView.setAdapter( listAdapter );
        }
    }
/*
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bar);

        ListView barListView = findViewById(R.id.lista);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Player");

        final ArrayAdapter <String> adapter  = new ArrayAdapter<String>(this,R.layout.simplerow,PlaceNames);
        myRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){

                    //Loop 1 to go through all the child nodes of users
                    for(DataSnapshot placeSnapshot : uniqueKeySnapshot.getChildren()){
                        //loop 2 to go through all the child nodes of books node

                        String place = placeSnapshot.getValue().toString();
                        adapter.add(place);
                    }
                }}
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.v(TAG, (PlaceNames.toString()));

        barListView.setAdapter(adapter);



                    }




}*/
