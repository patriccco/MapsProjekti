package com.example.kona.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * This abstract class is the base for turn based multiplayer game
 */
public abstract class Game {

    abstract void initializeGame();

    abstract void makePlay(String player);

    abstract boolean endOfGame(boolean end);

    abstract void printWinner(String player);
    //Connect to firebase
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Game");
    private Context context;

    /**
     * This method creates gameroom and starts the game
     * The turnbasedActivity is started from here
     * @param player1 this is the users name
     * @param player2 this is the opponents name
     */
    public void CreateGame(String player1, String player2) {

        this.context.startActivity(new Intent(this.context, TurnBasedActivity.class));

        DatabaseReference GameRef = database.getReference("Game");
        GameRef.push();

        GameRef.child("1").setValue(player1);
        GameRef.child("2").setValue(player2);
        playOneGame(player1, player2);
    }

    /**
     * basic turn based template method
     */
    public final void playOneGame(String player1, String player2) {

        ArrayList<String> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        long tStop;
        double start, finaltime;
        boolean end = false;
        initializeGame();
        int j = 0;

        while (!endOfGame(end)) {
            start = (double) System.currentTimeMillis();
            makePlay(players.get(j));
            j = (j + 1) % players.size();
            tStop = System.currentTimeMillis();
            finaltime = (((double) tStop - start) / 1000);
            if (finaltime > 4.0) {
                end = true;
            }

        }
        printWinner(players.get(j));
        this.context.startActivity(new Intent(this.context, TurnBasedActivity.class));
    }
}
