package com.example.kona.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * This class extends the abstract Game class.
 *
 */
public class TurnBasedGame extends Game {
private Context context;
private Boolean click;

    /**
     * This method sets the welcome text to TurnBaseActivity EventText
     */
    @Override
    void initializeGame() {
        //TÄHÄN PELIN TERVETULOTEKSTIT JA KÄYNNISTYSJUTTUJA
        TextView EventText = (TextView) ((Activity) context).findViewById(R.id.EventText);
        //EventText: Lets kick some ass!
        EventText.setText(R.string.EventText);

    }

    /**
     * This method is one turn in game
     * @param player this is tells whos turn it is
     */
    @Override
    void makePlay(final String player) {
        //Info about whos turn it is
        final TextView EventText = (TextView) ((Activity) context).findViewById(R.id.EventText);
        //EventText: Player "player" turn!
        EventText.setText(R.string.Player + player + R.string.turn);

        //Item1 onclick handler
        ImageButton item1 = (ImageButton)((Activity) context).findViewById(R.id.Item1);
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EventText: I hope "player" slips on this banana!
                EventText.setText(R.string.hope + player + R.string.banana);

            }
        });
//wait till the item1 is pressed
        while (item1.isPressed()) {
            EventText.setText(R.string.UseItem);
        }

    }

    /**
     * This method delivers message to end game
     * @param end
     * @return true/false
     */
    @Override
    boolean endOfGame(boolean end) {
        return end;
    }

    /**
     * This method points the winner to EventText
     * @param player contains the winners name
     */
    @Override
    void printWinner(String player) {
        //Set the winners text to TextView
        TextView EventText = (TextView) ((Activity) context).findViewById(R.id.EventText);
        //EventText: And the winner is: "player"
        EventText.setText(R.string.printWinner +  player + "/n" + R.string.points);
        //Add 10 points to winners account
        myRef.child("Score").setValue(0);

    }
}
