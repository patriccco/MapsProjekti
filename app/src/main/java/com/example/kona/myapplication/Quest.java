package com.example.kona.myapplication;

import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kona on 28.2.2018.
 */

public class Quest {

    private String name,vicinity;
    private Boolean isquest = false;
    private LatLng qlatlng, questLatLng;
    private double qlat,qlong;
    long Qtime, QendTime;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userinfo = auth.getCurrentUser().getUid();
    FirebaseDatabase  mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference QuestRef = mDatabase.getReference("Player");
    public Quest() {

    }

    /**
     *Push the data of quest taken to the database
     * @param newQ true if new quest was taken
     */
    public void newQuest(Boolean newQ){
        QuestRef.child("User").child(userinfo).child("Quest").child("newQuest").setValue(newQ);

    }

    /**
     * Set all the required variables for a quest to the database
     * @param latitude
     * @param longitude
     * @param name
     * @param vicinity
     * @param isquest
     */
    public void setQuest(double latitude, double longitude, String name, String vicinity, long Qtime, Boolean isquest) {

        this.questLatLng = new LatLng(latitude, longitude);

            try {
                DatabaseReference MyRef = mDatabase.getReference("Player");
                MyRef.child("User").child(auth.getUid()).child("Quest").setValue(questLatLng);
                MyRef.child("User").child(auth.getUid()).child("Quest").child("isQuest").setValue(isquest);
                MyRef.child("User").child(auth.getUid()).child("Quest").child("Questname").setValue(name);

                MyRef.child("User").child(auth.getUid()).child("Quest").child("QuestTimeEnd").setValue(SystemClock.uptimeMillis() + Qtime);
                MyRef.child("User").child(auth.getUid()).child("Quest").child("QuestTimeStart").setValue(SystemClock.uptimeMillis());
                this.Qtime = SystemClock.uptimeMillis();
                this.QendTime = SystemClock.uptimeMillis() + Qtime;

                MyRef.child("User").child(auth.getUid()).child("Quest").child("Questvicinity").setValue(vicinity);

                MyRef.child("User").child(userinfo).child("Quest").child("newQuest").setValue(false);


            } catch (Exception e){}

}

    /**
     * Getter for the quest location
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
     * getter for the name of the quest
     * @return
     */
    public String getQuestName() {
        QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test customer marker
                name = dataSnapshot.child("User").child(userinfo).child("Quest").child("Questname").getValue().toString();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return name;
    }

    /**
     * Getter for the vicinity of the Quest
     * @return
     */
    public String getQuestVicinity() {
        QuestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test customer marker
                vicinity = dataSnapshot.child("User").child(userinfo).child("Quest").child("Questvicinity").getValue().toString();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return vicinity;
    }

    /**
     * Returns true if quest is on
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

    public long getQuestTime() {
        Qtime = SystemClock.uptimeMillis();
        return Qtime;
    }
    public long getQuestEndtTime() {
        return QendTime;
    }




}


