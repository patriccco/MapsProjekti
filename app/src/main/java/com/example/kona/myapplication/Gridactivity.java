package com.example.kona.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Gridactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridactivity);

        PixelGridView pixelGrid = new PixelGridView(this);
        pixelGrid.setNumColumns(8);
        pixelGrid.setNumRows(14);

        setContentView(pixelGrid);
    }
}
