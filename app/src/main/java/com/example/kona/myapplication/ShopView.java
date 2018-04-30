package com.example.kona.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kona on 16.2.2018.
 */

public class ShopView extends AppCompatActivity{

    ArrayList<String> PlaceNames = new ArrayList<>();
    private final static String TAG = "TÄÄ";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    MediaPlayer Tune;

    ArrayList <String> products = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop);
        Transaction test = new Transaction();
    }

    @Override
    protected void onResume(){
        super.onResume();

        Tune = MediaPlayer.create(getApplicationContext(), R.raw.kauppatune);
        Tune.start();

        /**piilottaa status barin**/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
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
     * Buy from the shop
     * @param view
     */

    public void buy(View view) {
        Intent intent = new Intent(this, BuyActivity.class);
        startActivity(intent);
    }

    /*public void sell(View view) {
        Intent intent = new Intent(this, SellActivity.class);
        startActivity(intent);
    }*/

    /**
     * Switch the activity by view
     * @param view
     */
    public void backToMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void toArmGame(View view){
        Intent in = new Intent(this, ArmGameActivity.class);
        startActivity(in);

    }

}
