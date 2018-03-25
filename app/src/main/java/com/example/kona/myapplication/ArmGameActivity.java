package com.example.kona.myapplication;

import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

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
        int points = 0;
        ImageView red,green,blue,yellow;
        TextView mTextField;



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
            timer(4000);


        }



        public void gameOn(){

            mTextField.setText("START!");


                if(points > -20) {
                    Random rng = new Random();
                    int rngRes = rng.nextInt(4) + 1;

                    switch (rngRes) {
                        case 1:
                            mTextField.setVisibility(GONE);
                            red.setVisibility(View.VISIBLE);
                            red.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    points--;
                                    red.setVisibility(GONE);
                                    Rotate();
                                    gameOn();

                                }
                            });
                            break;
                        case 2:

                            mTextField.setVisibility(GONE);
                            blue.setVisibility(View.VISIBLE);
                            blue.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    points--;
                                    blue.setVisibility(GONE);
                                    Rotate();
                                    gameOn();
                                }
                            });
                            break;
                        case 3:

                            mTextField.setVisibility(GONE);
                            green.setVisibility(View.VISIBLE);
                            green.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    points--;
                                    green.setVisibility(GONE);
                                    Rotate();
                                    gameOn();
                                }
                            });
                            break;

                        case 4:
                            mTextField.setVisibility(GONE);
                            yellow.setVisibility(View.VISIBLE);
                            yellow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    points--;
                                    yellow.setVisibility(GONE);
                                    Rotate();
                                    gameOn();
                                }
                            });
                            break;

                    }
                }
                else{

                }


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



        public void Rotate(){

            ImageView imageView = findViewById(R.id.käsiview);
            imageView.setRotation(points*4);
            imageView.getLeft();
            imageView.getRight();
            imageView.setLeft(imageView.getLeft() + 100);
            imageView.setTranslationX(points*18);
            imageView.setTranslationY(points* - 10);
        }






    }

