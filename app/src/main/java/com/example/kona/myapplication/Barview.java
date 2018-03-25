package com.example.kona.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * This class creates the barview and shows the players in that bar.
 */

public class Barview extends AppCompatActivity{
    ArrayList <String> PlaceNames = new ArrayList<>();
    private final static String TAG = "TÄÄ";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ListView mainListView ;
    Quest Questobject = new Quest();
    String UserPlace;
    String dbPlace;
    private MediaPlayer Tune;

    /**
     * Empty main constructor.
     */
    public Barview(){
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_bar);
            Button button = findViewById(R.id.getjob);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                        Intent intent = new Intent(Barview.this, MapsActivity.class);
                        startActivity(intent);
                        if (!Questobject.getisQuest())
                            Questobject.newQuest(true);


                }
            });

            final ArrayAdapter <String> adapter  = new ArrayAdapter<String>(this,R.layout.simplerow,PlaceNames);

        final DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserPlace = dataSnapshot.child(auth.getUid()).child("Place").getValue().toString();


                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                    //Loop 1 to go through all the child nodes of users
                    dbPlace = uniqueKeySnapshot.child("Place").getValue().toString();
                    String player = uniqueKeySnapshot.child("name").getValue().toString();
                    adapter.remove(player);

                    if (dbPlace.equals(UserPlace) && UserPlace != "moving") {
                        adapter.add(player);
                    }

                }

                // Find the ListView resource.
                mainListView = findViewById( R.id.lista);
                Log.d(TAG, PlaceNames.toString());
                // Set the ArrayAdapter as the ListView's adapter.
                mainListView.setAdapter(adapter);

                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String item = adapter.getItem(i);
                        Log.e("User", "" + item);


                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        }




    /**
     /**
     * This method sets MapsActivity to pause.
     */

    @Override
    protected void onPause() {

        Tune.stop();
        Tune.release();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").child(auth.getUid()).child("Place").setValue("moving");
        super.onPause();
    }

    /**
     * starts musicplayback when entering or coming back to the activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        Tune = MediaPlayer.create(getApplicationContext(), R.raw.bartune);
        Tune.start();

        /**piilottaa status barin**/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     *
     * @param view
     * When pressing return button in a barview it takes user back to MapsActivity.
     */
    public void backToMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void toArmGame(View view){
        Intent in = new Intent(this, ArmGameActivity.class);
        startActivity(in);

    }

}

