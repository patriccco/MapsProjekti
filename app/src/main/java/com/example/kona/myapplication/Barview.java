package com.example.kona.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class creates the barview and shows the players in that bar.
 */

public class Barview extends AppCompatActivity {
    ArrayList<String> PlaceNames = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ListView mainListView;
    long bet;
    private ValueEventListener mListener;
    Transaction transaction = new Transaction();
    Button armchal, accept, decline, turnChallenge, acceptTurn, declineTurn,bet20,bet30,bet40;
    TextView playertext, challengerTextView,TimeTextView,Questbox,questno,questyes,questText,Moneyview;
    Boolean inarmgame, inturngame;
    Quest Questobject = new Quest();
    String UserPlace;
    String dbPlace, curUser, player, challengedplayer, opponent, opponentid, challengeOn,challengedId, TurnchallengeOn, OpponentAvatar, curUserAvatar;
    private MediaPlayer Tune;
    final DatabaseReference myRef = database.getReference("Player");

    /**
     * Empty main constructor.
     */
    public Barview() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bar);
        final TextView barName = findViewById(R.id.barname);
        barName.setText(UserPlace);
        transaction.getPlayerMoney();
        Moneyview = findViewById(R.id.money);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simplerow, PlaceNames);
        mListener = myRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int money = transaction.getMoney();
                Moneyview.setText(money + " ");
                challengeOn = dataSnapshot.child(auth.getUid()).child("challenged").getValue().toString();
                TurnchallengeOn = dataSnapshot.child(auth.getUid()).child("challengedTurn").getValue().toString();
                adapter.clear();

                UserPlace = dataSnapshot.child(auth.getUid()).child("Place").getValue().toString();
                curUser = dataSnapshot.child(auth.getUid()).child("name").getValue().toString();
                inarmgame = (Boolean)dataSnapshot.child(auth.getUid()).child("inarmgame").getValue();
                inturngame = (Boolean)dataSnapshot.child(auth.getUid()).child("inturngame").getValue();

                if (inarmgame == true && challengeOn.equals("start")) {
                    ChallengertoArmGame();
                    return;
                }else if(inturngame == true && TurnchallengeOn.equals("startTurn")){
                    ChallengerToTurnGame();
                    myRef.removeEventListener(mListener);
                    return;
                }

                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    //Loop 1 to go through all the child nodes of users
                    dbPlace = uniqueKeySnapshot.child("Place").getValue().toString();
                    String name = uniqueKeySnapshot.child("name").getValue().toString();
                    opponentid = uniqueKeySnapshot.child("id").getValue().toString();
                    String email = uniqueKeySnapshot.child("email").getValue().toString();
                    User player = new User(opponentid, name, email, dbPlace);


                    if (dbPlace.equals(UserPlace) && UserPlace != "moving" && !name.equals(curUser)) {
                        adapter.add(player.getName());
                    }
                }
                if (!challengeOn.equals("no")&&!challengeOn.equals("start")&&inarmgame==false) {
                    ChallengedYouWindow(challengeOn);

                }else if(!TurnchallengeOn.equals("no")&&!TurnchallengeOn.equals("startTurn")&&inturngame==false){

                    Log.e("","sisällä");
                    challengedToTurngame(TurnchallengeOn);
                }


                // Find the ListView resource.
                mainListView = findViewById(R.id.lista);
                // Set the ArrayAdapter as the ListView's adapter.
                mainListView.setAdapter(adapter);
                barName.setText(UserPlace);

                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        challengedplayer = adapter.getItem(i);
                        challengewindow(challengedplayer);


                    }

                });

                //in case of false entry to the bar direct back to map.
                if(UserPlace.equals("moving") && challengeOn.equals("no") && TurnchallengeOn.equals("no")){
                    Intent intent = new Intent(Barview.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * /**
     * This method sets MapsActivity to pause.
     */
    public void challengewindow(String player) {
        armchal = findViewById(R.id.armchallenge);
        playertext = findViewById(R.id.challengedplayer);
        playertext.setText(getString(R.string.challenge) + "\n" + player);
        playertext.setVisibility(View.VISIBLE);
        armchal.setVisibility(View.VISIBLE);
        //TODO tehdäänkö molemmille peleille omat challengit vai käytetäänkö if/switch
        //BUTTON TO TURN BASED GAME
        turnChallenge = findViewById(R.id.turnchallenge);
        turnChallenge.setVisibility(View.VISIBLE);

    }

    /**
     * Shows options for challenged player after challenger sets the bet
     * @param opponent displayname of the opponent
     */
    public void ChallengedYouWindow(final String opponent) {



        final DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bet = (long) dataSnapshot.child(auth.getUid()).child("challengedBet").getValue();
                accept = findViewById(R.id.acceptarm);
                decline = findViewById(R.id.declinearm);
                challengerTextView = findViewById(R.id.armchallenger);
                TimeTextView = findViewById(R.id.time);
                challengerTextView.setText(opponent + getString(R.string.challenged) + "\n" + getString(R.string.you) + getString(R.string.foramount) + bet + "!");
                challengerTextView.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
                decline.setVisibility(View.VISIBLE);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        opponenttimer.start();
        //TODO tehdäänkö molemmille peleille omat challengit vai käytetäänkö if/switch
        //BUTTONS TO TURN BASED GAME
        /*acceptTurn = findViewById(R.id.acceptTurn);
        declineTurn = findViewById(R.id.declinTurn);
        acceptTurn.setVisibility(View.VISIBLE);
        declineTurn.setVisibility(View.VISIBLE);*/

    }
    public void betButtons(View view) {

        armchal.setVisibility(View.GONE);
        turnChallenge.setVisibility(View.GONE);
        bet20 =findViewById(R.id.bet20);
        bet30 =findViewById(R.id.bet30);
        bet40 =findViewById(R.id.bet40);
        bet40.setVisibility(View.VISIBLE);
        bet20.setVisibility(View.VISIBLE);
        bet30.setVisibility(View.VISIBLE);
    }

    public void confirmQuest(View view){
        questyes = findViewById(R.id.textView6);
        questno = findViewById(R.id.textView7);
        Questbox = findViewById(R.id.questbox);
        questText = findViewById(R.id.questtext);
        questyes.setVisibility(View.VISIBLE);
        questno.setVisibility(View.VISIBLE);
        Questbox.setVisibility(View.VISIBLE);
        questText.setVisibility(View.VISIBLE);

    }
    public void noQuest(View view){
        questyes.setVisibility(View.GONE);
        questno.setVisibility(View.GONE);
        Questbox.setVisibility(View.GONE);
        questText.setVisibility(View.GONE);
    }

    /**
     * set bet based on button chose by user
     * @param view the button pressed
     */
    public void setBet(View view) {

        if (view == findViewById(R.id.bet30)) {
            bet = 30;
        }
        if (view == findViewById(R.id.bet20)) {
            bet = 20;
        }
        if (view == findViewById(R.id.bet40)){
            bet = 40;
    }
            final DatabaseReference myRef = database.getReference("Player");

            challengertimer.start();
            myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        //Loop 1 to go through all the child nodes of users
                        String challengedName = uniqueKeySnapshot.child("name").getValue().toString();
                        if (challengedName.equals(challengedplayer)) {
                            challengedId = uniqueKeySnapshot.child("id").getValue().toString();
                            myRef.child("User").child(challengedId).child("challenged").setValue(curUser);
                            myRef.child("User").child(challengedId).child("challengedBet").setValue(bet);

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    @Override
    protected void onPause() {
        super.onPause();
        Tune.stop();
        Tune.release();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference aaRef = database.getReference("Player");
        aaRef.child("User").child(auth.getUid()).child("Place").setValue("moving");

    }

    /**
     * starts musicplayback when entering or coming back to the activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        Tune = MediaPlayer.create(getApplicationContext(), R.raw.bartune);
        Tune.start();

        /**Hide Statusbar**/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    public void makeQuest(View view){

        Questobject.newQuest(true);

            Intent intent = new Intent(Barview.this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        }




    /**
     * @param view When pressing return button in a barview it takes user back to MapsActivity.
     */
    public void backToMap(View view) {
        Intent intent = new Intent(Barview.this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }



    /**
     * initializes the armgame to database, and moves the challenged player to the activity
     * @param bet the stake in the game
     * @param curplayer name of current player
     * @param opponent name of the opponent
     */
    public void ChallengedtoArmGame(long bet, String curplayer, String opponent) {
        this.opponent = opponent;
        DatabaseReference GameRef = database.getReference("Game");
        GameRef.child(curplayer).child("Score").setValue(0);
        GameRef.child(curplayer).child("bet").setValue(bet);
        GameRef.child(curplayer).child(curplayer).setValue(1);
        GameRef.child(curplayer).child(opponent).setValue(2);
        int betint = (int)bet*-1;
        transaction.addMoney(betint);


        Intent in = new Intent(this, ArmGameActivity.class);
        startActivity(in);
    }

    /**
     * directs challlenger user to the acticity for the armgame
     */
    public void ChallengertoArmGame(){

        myRef.child("User").child(auth.getUid()).child("challenged").setValue(challengedplayer);
        int betint = (int)bet*-1;
        transaction.addMoney(betint);
        Intent in = new Intent(this, ArmGameActivity.class);
        startActivity(in);
        challengertimer.cancel();
    }

    /**
     * Change the required database values before initializing the armgame
     * @param view the accept button
     */
    public void directarmgame(View view) {
        opponenttimer.cancel();
        final DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    //Loop 1 to go through all the child nodes of users
                    String challengedName = uniqueKeySnapshot.child("name").getValue().toString();
                    if(challengedName.equals(challengeOn)) {
                        challengedId = uniqueKeySnapshot.child("id").getValue().toString();

                        myRef.child("User").child(challengedId).child("challenged").setValue("start");
                        myRef.child("User").child(auth.getUid()).child("challenged").setValue(curUser);
                        myRef.child("User").child(challengedId).child("inarmgame").setValue(true);
                        myRef.child("User").child(auth.getUid()).child("inarmgame").setValue(true);



                        ChallengedtoArmGame(bet,curUser,challengeOn);
                        break;
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
        CountDownTimer challengertimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {

                TimeTextView = findViewById(R.id.time);
                TimeTextView.setText("" + String.format("0%d : %d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));


            }
            public void onFinish() {
                Toast.makeText(getApplicationContext(), R.string.noaccept,
                        Toast.LENGTH_SHORT).show();
                myRef.child("User").child(challengedId).child("challenged").setValue("no");
                myRef.child("User").child(auth.getUid()).child("challenged").setValue("no");
                myRef.child("User").child(challengedId).child("inarmgame").setValue(false);
                myRef.child("User").child(challengedId).child("inarmgame").setValue(false);
                playertext.setVisibility(View.GONE);
                armchal.setVisibility(View.GONE);
                turnChallenge.setVisibility(View.GONE);
                bet20.setVisibility(View.GONE);
                bet30.setVisibility(View.GONE);
                bet40.setVisibility(View.GONE);
                TimeTextView.setVisibility(View.GONE);
            }
        };

        CountDownTimer opponenttimer =  new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                challengerTextView = findViewById(R.id.armchallenger);
                challengerTextView.setVisibility(View.VISIBLE);
                TimeTextView = findViewById(R.id.time);
                TimeTextView.setText("" + String.format("0%d : %d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                TimeTextView.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
                challengerTextView.setVisibility(View.GONE);
                TimeTextView.setVisibility(View.GONE);
            }

        };

        public void hideAll(View view){
            TimeTextView.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
            playertext.setVisibility(View.GONE);
            armchal.setVisibility(View.GONE);
            turnChallenge.setVisibility(View.GONE);
            bet20.setVisibility(View.GONE);
            bet30.setVisibility(View.GONE);
            bet40.setVisibility(View.GONE);

        }
    /**
     * This method sets the accept and decline buttons to the opponent
     * @param opponent
     */
    public void challengedToTurngame(final String opponent){
        //BUTTONS TO TURN BASED GAME
        Log.e("","challengeToTurnGame");
        acceptTurn = findViewById(R.id.acceptTurn);
        declineTurn = findViewById(R.id.declinTurn);
        acceptTurn.setVisibility(View.VISIBLE);
        declineTurn.setVisibility(View.VISIBLE);
        challengerTextView = findViewById(R.id.armchallenger);
        challengerTextView.setText(opponent + getString(R.string.Challenge) + "\n" + getString(R.string.turngame));
        challengerTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method is onclick event for player who is challenged to turn based multiplayer
     * and accepts it
     * @param view
     */
    public void acceptTurnGame(View view){

        Log.e("","acceptTurnGame");

        final DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curUserAvatar = dataSnapshot.child(auth.getUid()).child("Avatar").getValue().toString();
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    //Loop 1 to go through all the child nodes of users
                    String challengedName = uniqueKeySnapshot.child("name").getValue().toString();
                    if(challengedName.equals(TurnchallengeOn)) {
                        challengedId = uniqueKeySnapshot.child("id").getValue().toString();
                        OpponentAvatar = uniqueKeySnapshot.child("Avatar").getValue().toString();

                        myRef.child("User").child(challengedId).child("challengedTurn").setValue("startTurn");
                        myRef.child("User").child(auth.getUid()).child("challengedTurn").setValue(curUser);
                        myRef.child("User").child(challengedId).child("inturngame").setValue(true);
                        myRef.child("User").child(auth.getUid()).child("inturngame").setValue(true);
                        myRef.removeEventListener(mListener);
                        toTurnGame(curUser,TurnchallengeOn);
                        break;
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method is onclick event listener for player who is challenged to turn based multiplayer
     * and declines it
     * @param view
     */
    public void declineTurnGame(View view){
        myRef.child("User").child(challengedId).child("challengedTurn").setValue("no");
        acceptTurn = findViewById(R.id.acceptTurn);
        declineTurn = findViewById(R.id.declinTurn);
        acceptTurn.setVisibility(View.GONE);
        declineTurn.setVisibility(View.GONE);
        challengerTextView = findViewById(R.id.armchallenger);
        challengerTextView.setVisibility(View.GONE);
    }

    /**
     * This method sends user to turn based multiplayer
     */
    private void ChallengerToTurnGame() {
        myRef.child("User").child(auth.getUid()).child("challengedTurn").setValue(challengedplayer);
        Intent turnGame = new Intent(this, TurnBasedActivity.class);
        startActivity(turnGame);
        finish();
    }

    /**
     * This method is onclick event listener for challenge to turn based multiplayer
     * @param view
     */
    public void startTurnGame(View view){

        Log.e("","startToTurngame");
        final DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    //Loop 1 to go through all the child nodes of users
                    String challengedName = uniqueKeySnapshot.child("name").getValue().toString();
                    if (challengedName.equals(challengedplayer)) {
                        challengedId = uniqueKeySnapshot.child("id").getValue().toString();
                        myRef.child("User").child(challengedId).child("challengedTurn").setValue(curUser);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * This method is the entry to turn based game
     * This method sets the multiplayer room and needed data under "Game" in database
     *
     * @param curplayer current user
     * @param Opponent  challenged user
     */
    public void toTurnGame(String curplayer, String opponent) {
        this.opponent = opponent;
        DatabaseReference GameRef = database.getReference("Game");
        GameRef.child("TurnGame").child(curplayer).child(curplayer).child("Status").setValue(1);
        GameRef.child("TurnGame").child(curplayer).child(curplayer).child("Action").setValue("none");
        GameRef.child("TurnGame").child(curplayer).child(curplayer).child("HP").setValue(100);
        GameRef.child("TurnGame").child(curplayer).child(opponent).child("Status").setValue(2);
        GameRef.child("TurnGame").child(curplayer).child(opponent).child("Action").setValue("none");
        GameRef.child("TurnGame").child(curplayer).child(opponent).child("HP").setValue(100);
        GameRef.child("TurnGame").child(curplayer).child(opponent).child("Avatar").setValue(OpponentAvatar);
        GameRef.child("TurnGame").child(curplayer).child(curplayer).child("Avatar").setValue(curUserAvatar);

        myRef.removeEventListener(mListener);
        Intent turnGame = new Intent(this, TurnBasedActivity.class);
        startActivity(turnGame);
        finish();
    }

}

