package com.example.kona.myapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by kona on 7.3.2018.
 */

public class Encounter {


    //databasehommat


    public String enemyName;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private boolean victory = false;
    //Money & hp transfer
    Transaction transact = new Transaction();







    public boolean Randomize() {
        Random rn = new Random();
        int answer = rn.nextInt(20) + 1;

        if(answer < 2) {
            return true;
        }
        return false;

    }


}