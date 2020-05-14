package com.example.kona.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
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
    TextView mTextField,invisbtn;
    String player,curUser;
    Transaction transaction = new Transaction();

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    DatabaseReference MyRef = mDatabase.getReference("Game");
    DatabaseReference nameRef = mDatabase.getReference("Player");
    private ValueEventListener mListener;

    FirebaseAuth auth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armgame);

        invisbtn = findViewById(R.id.invisbtn);
        invisbtn.setVisibility(View.VISIBLE);
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

        mListener = MyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {


                mTextField.setText(R.string.startarm);
                long betDB = (long) dataSnapshot.child(player).child("bet").getValue();
                bet = (int) betDB;
                playerstatus = (long) dataSnapshot.child(player).child(curUser).getValue();
                points = (long) dataSnapshot.child(player).child("Score").getValue();



                    if (hand > -20 && hand < 20) {
                        if (points >= 20 || points <= -20){
                            points = (long) dataSnapshot.child(player).child("Score").getValue();
                            HandleVictory(points);
                    }

                        Random rng = new Random();
                        int rngRes = rng.nextInt(4) + 1;



                        points = (long) dataSnapshot.child(player).child("Score").getValue();
                        switch (rngRes) {
                            case 1:
                                if (hand > 0) {
                                    RotateRight();
                                } else {
                                    Rotateleft();
                                }
                                mTextField.setVisibility(GONE);
                                red.setVisibility(View.VISIBLE);
                                blue.setVisibility(View.GONE);
                                yellow.setVisibility(View.GONE);
                                green.setVisibility(View.GONE);
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
                                        red.setVisibility(View.GONE);
                                        if (hand > 0) {

                                                RotateRight();

                                        } else {

                                                Rotateleft();


                                        }


                                    }
                                });
                                break;
                            case 2:

                                if (hand > 0) {
                                    RotateRight();
                                } else {
                                    Rotateleft();
                                }
                                mTextField.setVisibility(GONE);
                                blue.setVisibility(View.VISIBLE);

                                yellow.setVisibility(View.GONE);
                                red.setVisibility(View.GONE);
                                green.setVisibility(View.GONE);
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
                                        blue.setVisibility(View.GONE);
                                        if (hand > 0) {

                                                RotateRight();

                                        } else {

                                                Rotateleft();


                                        }

                                    }
                                });
                                break;
                            case 3:

                                if (hand > 0) {
                                    RotateRight();
                                } else {
                                    Rotateleft();
                                }
                                mTextField.setVisibility(GONE);
                                green.setVisibility(View.VISIBLE);
                                blue.setVisibility(View.GONE);
                                red.setVisibility(View.GONE);
                                yellow.setVisibility(View.GONE);
                                green.setOnClickListener(new View.OnClickListener() {
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
                                        green.setVisibility(View.GONE);
                                        if (hand > 0) {

                                                RotateRight();

                                        } else {

                                                Rotateleft();



                                        }
                                    }
                                });
                                break;

                            case 4:

                                if (hand > 0) {
                                    RotateRight();
                                } else {
                                    Rotateleft();
                                }
                                mTextField.setVisibility(GONE);
                                yellow.setVisibility(View.VISIBLE);
                                blue.setVisibility(View.GONE);
                                red.setVisibility(View.GONE);
                                green.setVisibility(View.GONE);
                                yellow.setOnClickListener(new View.OnClickListener() {
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
                                        yellow.setVisibility(View.GONE);
                                        if (hand > 0) {
                                            RotateRight();}
                                         else {
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
    public void endtimer(long time) {


        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {


                Intent intent = new Intent(ArmGameActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();

                nameRef.child("User").child(auth.getUid()).child("challenged").setValue("no");
                nameRef.child("User").child(auth.getUid()).child("inarmgame").setValue(false);

                nameRef.child("User").child(auth.getUid()).child("challengedBet").setValue(0);
                MyRef.removeEventListener(mListener);

                MyRef.child(player).removeValue();


            }
        }.start();
    }

    public void Missclick(View view){
        if(playerstatus == 1){
            points = points - 1;
            MyRef.child(player).child("Score").setValue(points);
        }
        if (playerstatus == 2){
            points = points +1;
            MyRef.child(player).child("Score").setValue(points);
        }

    }



    public void Rotateleft(){

        ImageView imageView = findViewById(R.id.käsiview);
        imageView.setRotation(hand*5);
        imageView.getRight();

        imageView.setTranslationX(hand*18);
        imageView.setTranslationY(hand* - 10);
    }
    public void RotateRight(){
            ImageView imageView = findViewById(R.id.käsiview);
            imageView.setRotation(hand * 5);
            imageView.getRight();
            imageView.setTranslationX(hand * 18);
            imageView.setTranslationY(hand * 10);

    }

    public void HandleVictory(long score){
        red.setVisibility(View.GONE);
        blue.setVisibility(View.GONE);
        yellow.setVisibility(View.GONE);
        green.setVisibility(View.GONE);

        if(score > 18 && playerstatus == 2){

            transaction.addMoney(bet*2);
            Toast.makeText(getApplicationContext(), getString(R.string.armwin) + bet*2 + getString(R.string.money),
                    Toast.LENGTH_SHORT).show();
        }
        else if (score < -18 && playerstatus == 1){
            transaction.addMoney(bet*2);
            Toast.makeText(getApplicationContext(), getString(R.string.armwin) + bet*2 + getString(R.string.money),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.lost,
                    Toast.LENGTH_SHORT).show();

        }


        endtimer(3000);




    }






}


