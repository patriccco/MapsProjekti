package com.example.kona.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


/**
 * This activity is for shopping in shopview.
 */
public class BuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
    }

    /**
     *
     * @param view
     * This takes user back to MainMenuActivity after pressing button purchase items.
     */

    public void confirmBuy(View view) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }



    /**
     *
     * @param view
     * This takes user back to MainMenuActivity when pressing button return.
     */
    public void backToStore(View view) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}
