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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Barview extends AppCompatActivity{
    ArrayList <String> PlaceNames = new ArrayList<>();
    private final static String TAG = "TÄÄ";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ListView mainListView ;
    public PlaceID place = new PlaceID();

    public Barview(){

    }

    public Barview(PlaceID place) {
        this.place=place;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_bar);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Player");



        final ArrayAdapter <String> adapter  = new ArrayAdapter<String>(this,R.layout.simplerow,PlaceNames);

        myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserPlace = dataSnapshot.child(auth.getUid()).child("Place").getValue().toString();


                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){

                    //Loop 1 to go through all the child nodes of users
                        String dbplace = uniqueKeySnapshot.child("Place").getValue().toString();
                        String player = uniqueKeySnapshot.child("name").getValue().toString();

                    if (dbplace == UserPlace){
                        adapter.add(player);

                }}}
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            // Find the ListView resource.
            mainListView = findViewById( R.id.lista);
            Log.d(TAG, PlaceNames.toString());
            // Set the ArrayAdapter as the ListView's adapter.
            mainListView.setAdapter(adapter);
        }
    @Override
    protected void onPause() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").child(auth.getUid()).child("Place").setValue("moving");
        super.onPause();
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
