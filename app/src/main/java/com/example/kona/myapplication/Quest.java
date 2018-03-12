package com.example.kona.myapplication;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kona on 28.2.2018.
 */

public class Quest {

    private String qName,qVicinity;
    private Boolean isquest = false;
    private LatLng qlatlng, questLatLng;
    private double qlat,qlong;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userinfo = auth.getCurrentUser().getUid();
    FirebaseDatabase  mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference QuestRef = mDatabase.getReference("Player");
    public Quest() {

    }

    /**
     *Asetetaan tietokantaan tieto uudesta tehtävästä
     * @param newQ
     */
    public void newQuest(Boolean newQ){
        QuestRef.child("User").child(userinfo).child("Quest").child("newQuest").setValue(newQ);

    }

    /**
     * Asetetaan tehtävälle muuttujat tietokantaan
     * @param latitude
     * @param longitude
     * @param name
     * @param vicinity
     * @param isquest
     */
    public void setQuest(double latitude, double longitude, String name, String vicinity, Boolean isquest) {

            this.questLatLng = new LatLng(latitude,longitude);
            DatabaseReference MyRef = mDatabase.getReference("Player");
            Log.d("aa", questLatLng.toString());
            MyRef.child("User").child(auth.getUid()).child("Quest").setValue(questLatLng);
            MyRef.child("User").child(auth.getUid()).child("Quest").child("isQuest").setValue(isquest);
            MyRef.child("User").child(auth.getUid()).child("Quest").child("Questname").setValue(name);
            MyRef.child("User").child(auth.getUid()).child("Quest").child("Questvicinity").setValue(vicinity);

        QuestRef.child("User").child(userinfo).child("Quest").child("newQuest").setValue(false);

}

    /**
     * Tehtävän sijainti
     * @return
     */
    public LatLng getQuestLatLng() {
            QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Test customer marker
                    qlat = (double)dataSnapshot.child("User").child(userinfo).child("Quest").child("latitude").getValue();
                    qlong = (double)dataSnapshot.child("User").child(userinfo).child("Quest").child("longitude").getValue();
                    qlatlng = new LatLng(qlat, qlong);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });



        return qlatlng;
    }

    /**
     * Tehtävän paikan nimi
     * @return
     */
    public String getQuestName() {
        QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test customer marker
                qName = dataSnapshot.child("User").child(userinfo).child("Quest").child("Questname").getValue().toString();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return qName;
    }

    /**
     * Tehtävän paikan osoite
     * @return
     */
    public String getQuestVicinity() {
        QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test customer marker
                qVicinity = dataSnapshot.child("User").child(userinfo).child("Quest").child("Questvicinity").getValue().toString();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return qVicinity;
    }

    /**
     * Palauttaa true jos tehtävä päällä
     *
     * @return
     */
    public boolean getisQuest() {
        QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test customer marker
                isquest = (Boolean) dataSnapshot.child("User").child(userinfo).child("Quest").child("isQuest").getValue();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return isquest;
    }


}


