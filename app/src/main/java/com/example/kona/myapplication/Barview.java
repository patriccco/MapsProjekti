package com.example.kona.myapplication;

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
import android.widget.TextView;

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


    Button armchal,accept,decline;
    TextView playertext,challenger;

    Quest Questobject = new Quest();
    String UserPlace;
    String dbPlace, curUser, player,challengedplayer;
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
                curUser = dataSnapshot.child(auth.getUid()).child("name").getValue().toString();

                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                    //Loop 1 to go through all the child nodes of users
                    dbPlace = uniqueKeySnapshot.child("Place").getValue().toString();
                    player = uniqueKeySnapshot.child("name").getValue().toString();
                    Object playerUid = uniqueKeySnapshot.getChildren();

                    adapter.remove(player);
                    adapter.remove(curUser);

                    if (dbPlace.equals(UserPlace) && UserPlace != "moving") {
                        adapter.add(player);

                    }

                }

                String challengeOn = dataSnapshot.child(auth.getUid()).child("challenged").getValue().toString();
                if(!challengeOn.equals("no")){
                    ChallengedYouWindow();
                }

                // Find the ListView resource.
                mainListView = findViewById( R.id.lista);
                // Set the ArrayAdapter as the ListView's adapter.
                mainListView.setAdapter(adapter);

                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        challengedplayer = adapter.getItem(i);
                        Log.e("User", "" + challengedplayer);
                        challengewindow(challengedplayer);


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
    public void challengewindow(String player){
        armchal = findViewById(R.id.armchallenge);
        playertext = findViewById(R.id.challengedplayer);
        playertext.setText("Challenge " + "\n" + player);
        playertext.setVisibility(View.VISIBLE);
        armchal.setVisibility(View.VISIBLE);


    }
    public void ChallengedYouWindow() {
        accept = findViewById(R.id.acceptarm);
        decline= findViewById(R.id.declinearm);
        challenger = findViewById(R.id.armchallenger);
        challenger.setText("Challenge " + "\n" + player);
        challenger.setVisibility(View.VISIBLE);
        accept.setVisibility(View.VISIBLE);
        decline.setVisibility(View.VISIBLE);


    }

    public void betButtons(View view){
        armchal.setVisibility(View.GONE);
        Button bet20 = findViewById(R.id.bet20);
        Button bet30 = findViewById(R.id.bet30);
        bet20.setVisibility(View.VISIBLE);
        bet30.setVisibility(View.VISIBLE);
    }

    public void setBet(View view){

        if(view == findViewById(R.id.bet30)){

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference chlngRef = database.getReference(challengedplayer);
            chlngRef.child("challenged").setValue(curUser);


        }
        else if (view == findViewById(R.id.bet20)){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference chlngRef = database.getReference(challengedplayer);

            chlngRef.child("challenged").setValue(curUser);
        }

    }
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

    public void toArmGame(int bet ,String curplayer, String opponent){

        DatabaseReference GameRef = database.getReference("Game");
        DatabaseReference newGameRef = GameRef.push();
        GameRef.child("Score").setValue(0);
        GameRef.child("bet").setValue(bet);
        GameRef.child("1").setValue(curplayer);
        GameRef.child("2").setValue(opponent);

        GameRef.child(curplayer).child("clicks").setValue(0);
        GameRef.child(opponent).child("clicks").setValue(0);

        Intent in = new Intent(this, ArmGameActivity.class);
        startActivity(in);

    }

}

