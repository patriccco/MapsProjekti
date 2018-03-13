package com.example.kona.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    /**
     * Called when the user taps the Log Out button
     * @param view
     */
    public void profileView(View view) {
        Intent logout = new Intent(this, LogOutActivity.class);

        startActivity(logout);
    }

    /**
     * return to map view
     * @param view
     */
    public void backToMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
