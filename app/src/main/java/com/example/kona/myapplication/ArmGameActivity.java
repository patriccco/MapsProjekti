package com.example.kona.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

/**
 * Created by kona on 25.3.2018.
 */

public class ArmGameActivity extends AppCompatActivity{
    /**
     * This activity triggers when the random encounter is accepted.
     * This is the battlescreen.
     */
    private TextView text;
    MediaPlayer fightTune;
    long points = 0;
    long hand= 0;
    long playerstatus;
    int bet;
    ImageView red,green,blue,yellow;
    TextView mTextField;
    String player,curUser;
    Transaction transaction = new Transaction();

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    DatabaseReference MyRef = mDatabase.getReference("Game");
    DatabaseReference nameRef = mDatabase.getReference("Player");

    FirebaseAuth auth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armgame);


        mTextField = findViewById(R.id.armtime);
        red = findViewById(R.id.redbutton);
        yellow = findViewById(R.id.yellowbutton);
        green = findViewById(R.id.greenbutton);
        blue = findViewById(R.id.bluebutton);
        yellow.setVisibility(GONE);
        blue.setVisibility(GONE);
        red.setVisibility(GONE);
        green.setVisibility(GONE);
        getchallengedName();
        timer(4000);


    }


    public void getchallengedName(){


        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curUser = dataSnapshot.child("User").child(auth.getUid()).child("name").getValue().toString();
                player = dataSnapshot.child("User").child(auth.getUid()).child("challenged").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void gameOn(){

        MyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {


                mTextField.setText("START!");
                long betDB = (long) dataSnapshot.child(player).child("bet").getValue();
                bet = (int) betDB;
                playerstatus = (long) dataSnapshot.child(player).child(curUser).getValue();
                points = (long) dataSnapshot.child(player).child("Score").getValue();



                    if (hand > -20 && hand < 20) {
                        if (points >= 19 || points <= -19){
                            HandleVictory(points);
                    }

                        Random rng = new Random();
                        int rngRes = rng.nextInt(4) + 1;

                        switch (rngRes) {
                            case 1:
                                mTextField.setVisibility(GONE);
                                red.setVisibility(View.VISIBLE);

                                blue.setVisibility(View.INVISIBLE);
                                yellow.setVisibility(View.INVISIBLE);
                                green.setVisibility(View.INVISIBLE);
                                red.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        points = (long) dataSnapshot.child(player).child("Score").getValue();

                                        if (playerstatus == 1) {
                                            points = points - 1;
                                            hand = points;
                                        } else {
                                            points = points + 1;
                                            hand = points * (-1);
                                        }

                                        MyRef.child(player).child("Score").setValue(points);
                                        red.setVisibility(View.INVISIBLE);
                                        if (hand > 0) {
                                            RotateRight();
                                        } else {
                                            Rotateleft();

                                        }


                                    }
                                });
                                break;
                            case 2:

                                mTextField.setVisibility(GONE);
                                blue.setVisibility(View.VISIBLE);

                                yellow.setVisibility(View.INVISIBLE);
                                red.setVisibility(View.INVISIBLE);
                                green.setVisibility(View.INVISIBLE);
                                blue.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        points = (long) dataSnapshot.child(player).child("Score").getValue();

                                        if (playerstatus == 1) {
                                            points = points - 1;
                                            hand = points;
                                        } else {
                                            points = points + 1;
                                            hand = points * (-1);
                                        }

                                        MyRef.child(player).child("Score").setValue(points);
                                        blue.setVisibility(View.INVISIBLE);
                                        if (hand > 0) {
                                            RotateRight();
                                        } else {
                                            Rotateleft();

                                        }

                                    }
                                });
                                break;
                            case 3:

                                mTextField.setVisibility(GONE);
                                green.setVisibility(View.VISIBLE);
                                blue.setVisibility(View.INVISIBLE);
                                red.setVisibility(View.INVISIBLE);
                                yellow.setVisibility(View.INVISIBLE);
                                green.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Log.e("", "" + hand);

                                        points = (long) dataSnapshot.child(player).child("Score").getValue();

                                        if (playerstatus == 1) {
                                            points = points - 1;
                                            hand = points;

                                        } else {
                                            points = points + 1;
                                            hand = points * (-1);
                                        }

                                        MyRef.child(player).child("Score").setValue(points);
                                        green.setVisibility(View.INVISIBLE);
                                        if (hand > 0) {
                                            RotateRight();
                                        } else {
                                            Rotateleft();

                                        }
                                    }
                                });
                                break;

                            case 4:
                                mTextField.setVisibility(GONE);
                                yellow.setVisibility(View.VISIBLE);
                                blue.setVisibility(View.INVISIBLE);
                                red.setVisibility(View.INVISIBLE);
                                green.setVisibility(View.INVISIBLE);
                                yellow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Log.e("", "" + hand);

                                        points = (long) dataSnapshot.child(player).child("Score").getValue();

                                        if (playerstatus == 1) {
                                            points = points - 1;
                                            hand = points;

                                        } else {
                                            points = points + 1;
                                            hand = points * (-1);
                                        }
                                        MyRef.child(player).child("Score").setValue(points);
                                        yellow.setVisibility(View.INVISIBLE);
                                        if (hand > 0) {
                                            RotateRight();
                                        } else {
                                            Rotateleft();

                                        }
                                    }
                                });
                                break;

                        }
                    }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });









    }

    public void timer(long time) {


        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                mTextField.setText(""+TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
            }


            public void onFinish() {
                gameOn();


            }
        }.start();
    }



    public void Rotateleft(){

        ImageView imageView = findViewById(R.id.käsiview);
        imageView.setRotation(hand*5);
        imageView.getRight();

        Log.e("", "" + hand);
        imageView.setTranslationX(hand*18);
        imageView.setTranslationY(hand* - 10);
    }
    public void RotateRight(){

        ImageView imageView = findViewById(R.id.käsiview);
        imageView.setRotation(hand*5);
        imageView.getRight();
        Log.e("", "" + hand);
        imageView.setTranslationX(hand* 18);
        imageView.setTranslationY(hand*  10);
    }

    public void HandleVictory(long score){

        if(score <= -19 && playerstatus == 1){
            transaction.addMoney(bet*2);
            Toast.makeText(getApplicationContext(), "You Won!" + "You got " + bet*2 + "Money!",
                    Toast.LENGTH_SHORT).show();

        }
        else if(score >= 19 && playerstatus == 2){
            transaction.addMoney(bet*2);

        }
        else {
            Toast.makeText(getApplicationContext(), "You Lost!",
                    Toast.LENGTH_SHORT).show();

        }




        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();

    }






}


