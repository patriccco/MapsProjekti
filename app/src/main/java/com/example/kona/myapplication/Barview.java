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
    private ListView mainListView;
    long bet;
    private ValueEventListener mListener;


    Button armchal,accept,decline, turnChallenge, acceptTurn, declineTurn;
    TextView playertext,challenger;

    Quest Questobject = new Quest();
    String UserPlace;
    String dbPlace, curUser, player,challengedplayer,opponent,opponentid;
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
            accept = findViewById(R.id.acceptarm);

            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                        Intent intent = new Intent(Barview.this, MapsActivity.class);
                        startActivity(intent);
                        if (!Questobject.getisQuest())
                            Questobject.newQuest(true);


                }
            });

//TODO Tämä lista pitää vaihtaa semmoseks et saadaan sieltä samalla se userID(HashMap?), jota voidaan käyttää jatkossa kun jaetaan peleissä pisteitä yms.
            final ArrayAdapter <String> adapter  = new ArrayAdapter<String>(this,R.layout.simplerow,PlaceNames);

        final DatabaseReference myRef = database.getReference("Player");
        mListener = myRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserPlace = dataSnapshot.child(auth.getUid()).child("Place").getValue().toString();
                curUser = dataSnapshot.child(auth.getUid()).child("name").getValue().toString();

                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                    //Loop 1 to go through all the child nodes of users
                    dbPlace = uniqueKeySnapshot.child("Place").getValue().toString();
                    String name = uniqueKeySnapshot.child("name").getValue().toString();
                    opponentid = uniqueKeySnapshot.child("id").getValue().toString();
                    String email = uniqueKeySnapshot.child("email").getValue().toString();
                    User player = new User(opponentid,name,email,dbPlace);


                    if (dbPlace.equals(UserPlace) && UserPlace != "moving" && !name.equals(curUser)) {
                        adapter.add(player.getName());
                    }
                }
                String challengeOn = dataSnapshot.child(auth.getUid()).child("challenged").getValue().toString();
                if(!challengeOn.equals("no")){
                    ChallengedYouWindow(challengeOn, opponentid);
                    myRef.child(auth.getUid()).child("challenged").removeEventListener(mListener);

                    if(challengeOn.equals("start")){
                        ChallengertoArmGame();
                    }
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
        //TODO tehdäänkö molemmille peleille omat challengit vai käytetäänkö if/switch
        //BUTTON TO TURN BASED GAME
        turnChallenge = findViewById(R.id.turnchallenge);
        turnChallenge.setVisibility(View.VISIBLE);

    }
    public void ChallengedYouWindow(final String opponent , final String id) {
        final DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bet = (long)dataSnapshot.child(auth.getUid()).child("challengedBet").getValue();

                decline= findViewById(R.id.declinearm);
                challenger = findViewById(R.id.armchallenger);
                challenger.setText( opponent + "Challenged " + "\n" + "You" + " for " + bet + "!");
                challenger.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
                decline.setVisibility(View.VISIBLE);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //TODO tehdäänkö molemmille peleille omat challengit vai käytetäänkö if/switch
        //BUTTONS TO TURN BASED GAME
        acceptTurn = findViewById(R.id.acceptTurn);
        declineTurn = findViewById(R.id.declinTurn);
        acceptTurn.setVisibility(View.VISIBLE);
        declineTurn.setVisibility(View.VISIBLE);



    }
    public void challengeaccepted(){

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
            final DatabaseReference myRef = database.getReference("Player");
            myRef.child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                        //Loop 1 to go through all the child nodes of users
                        String challengedName = uniqueKeySnapshot.child("name").getValue().toString();
                        String challengedid = uniqueKeySnapshot.child("id").getValue().toString();

                        String ischallenged = uniqueKeySnapshot.child("challenged").getValue().toString();
                        if(challengedName.equals(challengedplayer)){
                            myRef.child("User").child(challengedid).child("challenged").setValue(curUser);


                        }
                        }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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

    /**
     * This method is the entry to turn based game
     * @param curplayer current user
     * @param Opponent challenged user
     */
    public void toTurnGame(String curplayer, String Opponent){
        //TODO tämä metodi pitää saada oikeeseen kohtaan käyttöön ja tarvitaan myös se userID
    TurnBasedGame turnGame = new TurnBasedGame();
    turnGame.CreateGame(curplayer, Opponent);

}
    public void ChallengedtoArmGame(long bet ,String curplayer, String opponent){
        this.opponent = opponent;
        DatabaseReference GameRef = database.getReference("Game");
        GameRef.child(curplayer);
        GameRef.child("Score").setValue(0);
        GameRef.child("bet").setValue(bet);
        GameRef.child("1").setValue(curplayer);
        GameRef.child("2").setValue(opponent);

        GameRef.child(curplayer).child("clicks").setValue(0);
        GameRef.child(opponent).child("clicks").setValue(0);

        Intent in = new Intent(this, ArmGameActivity.class);
        startActivity(in);
    }
    public void ChallengertoArmGame(){

        Intent in = new Intent(this, ArmGameActivity.class);
        startActivity(in);

    }
    public void directarmgame(View view){

        DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").child(opponentid).child("challenged").setValue("start");
        Log.d("" , "AA" + opponentid);



        //ChallengedtoArmGame(bet,curUser,opponent);


    }

}

