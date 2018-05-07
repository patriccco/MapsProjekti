package com.example.kona.myapplication;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This activity triggers when the random encounter is accepted.
 * This is the battlescreen.
 */

public class Gridactivity extends AppCompatActivity {
    private TextView text;
    MediaPlayer fightTune;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridactivity);
        text = findViewById(R.id.GridText);
        text.setText(R.string.startgridgame);
        PixelGridView pixelGrid = findViewById(R.id.pixelGridView);
        pixelGrid.setNumColumns(7);
        pixelGrid.setNumRows(10);

    }
    @Override
    protected void onResume() {
        super.onResume();
        fightTune = MediaPlayer.create(getApplicationContext(), R.raw.crimson05);
        fightTune.start();
    }

    protected void onStop() {
        finish();
        super.onStop();
        fightTune.stop();
        fightTune.release();
    }

}
