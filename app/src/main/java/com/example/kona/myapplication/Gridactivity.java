package com.example.kona.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Gridactivity extends AppCompatActivity {
    private TextView text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridactivity);
        text = findViewById(R.id.GridText);
        text.setText("PRESS ANYWHERE TO START");
        PixelGridView pixelGrid = findViewById(R.id.pixelGridView);
        pixelGrid.setNumColumns(7);
        pixelGrid.setNumRows(10);

    }

}
