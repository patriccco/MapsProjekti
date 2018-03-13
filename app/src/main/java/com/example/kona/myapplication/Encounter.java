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

/**
 * This class counts the random encounter.
 */

public class Encounter {
    public String enemyName;
    private boolean victory;
    /**
     *
     * @return
     * This gets the result from the fight.
     * boolean: true or false.
     */

    public boolean isVictory() {
        return victory;
    }

    /**
     *
     * @param victory
     * Sets the result from the fight.
     */
    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    /**
     *
     * @return
     * This counts the random encounter.
     */
    public boolean Randomize() {
        Random rn = new Random();
        int answer = rn.nextInt(20) + 1;

        if(answer < 2) {
            return true;
        }
        return false;

    }


}