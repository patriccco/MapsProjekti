package com.example.kona.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    ArrayList <String> products = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop);
        Transaction test = new Transaction();
        test.checkmoney();
        test.CheckPrice("Banana");
    }
    @Override
    protected void onPause() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").child(auth.getUid()).child("Place").setValue("moving");
        super.onPause();
        }

    /**buttons**/

    public void buy(View view) {
        Transaction transaction = new Transaction();
        transaction.CheckPrice("Banana");
    }

    /*public void sell(View view) {
        Intent intent = new Intent(this, SellActivity.class);
        startActivity(intent);
    }*/

    public void backToMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
