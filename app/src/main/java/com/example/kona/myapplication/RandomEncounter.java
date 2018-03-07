package com.example.kona.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by ottoj_000 on 15.2.2018.
 */

public class RandomEncounter extends AppCompatActivity {

    //databasehommat
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //boolean arvo joka kertoo voittiko pelaaja taistelun vai eikö
    private boolean victory;
    //random luku jolla määritetään tuleeko kohtausta
    public double rnd = Math.random();
    public double rndchance = 0.5;
    //arvotaan mikä vihu
    public String enemName;
    public int raja = (1 - 4) + 1;
    public int enemgen = (int) (Math.random() * raja) + 1;
    public String enemID = String.valueOf(enemgen);
    //rahasiirto
    Transaction transact = new Transaction();

    public RandomEncounter() {}

    //Jotain viestijuttuja ja nappuloita
    /**
    private TextView createNewTextView(String msg) {
        final LinearLayout.LayoutParams lparams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(this.msg);
        return textView;
    }


    //Nappuloita
    /**
     //Viestit
     final TextView message = findViewById(R.id.intro);
     message.setVisibility(View.VISIBLE);


     //Taistelu ja pako -nappulat
     final Button ok = findViewById(R.id.button_ok);
     final Button flee = findViewById(R.id.button_flee);
     flee.setVisibility(View.VISIBLE);
     final Button fight = findViewById(R.id.button_fight);
     fight.setVisibility(View.VISIBLE);

     fight.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    if (pHP > eAP) {
    flee.setVisibility(View.GONE);
    fight.setVisibility(View.GONE);
    msg = "YOU FOUGHT WELL AND WON!";
    message.setText(msg);
    ok.setVisibility(View.VISIBLE);

    message.setVisibility(View.GONE);
    RandomEncounter.this.finish();
    } else {
    flee.setVisibility(View.GONE);
    fight.setVisibility(View.GONE);
    msg = "YOU LOST!";
    message.setText(msg);
    ok.setVisibility(View.VISIBLE);

    message.setVisibility(View.GONE);
    RandomEncounter.this.finish();
    }
    }
    });

     flee.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    flee.setVisibility(View.GONE);
    fight.setVisibility(View.GONE);
    msg = "YOU FLED LIKE A COWARD!";
    message.setText(msg);
    ok.setVisibility(View.VISIBLE);

    message.setVisibility(View.GONE);
    RandomEncounter.this.finish();
    }
    });

     ok.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    ok.setVisibility(View.GONE);

    message.setVisibility(View.GONE);
    RandomEncounter.this.finish();
    }
    });

     **/
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //jos random on suurempi kuin todnak, tulee kohtaus
        if (rnd < rndchance) RandomEncounter.this.finish();

        DatabaseReference myRef = database.getReference("Enemies");
        myRef.child("Type").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Haetaan vihun tiedot, ensin nimi
                enemName = (String) dataSnapshot.child(auth.getUid()).child(enemID).child("Name").getValue();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        //
        //
        //    PELI TÄHÄN
        //
        //
        //LOPPUTULEMAT, PALKINNOT JA RANGAISTUKSET

        if (victory == true) {
            //Haetaan nyk rahamäärä, ja lisätään vammainen summa tilille
            transact.addMoney(10);
        } else {
            transact.addHP(-10);

            //Tsäänssi menettää HP


        }

    }
}


