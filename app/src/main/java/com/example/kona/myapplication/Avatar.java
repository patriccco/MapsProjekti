package com.example.kona.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kona on 2.5.2018.
 */

public class Avatar {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("Player");
    public String avatarname;

    public void setAvatarname(String avatarname) {
        this.avatarname = avatarname;
    }

    public void getDatabaseAvatar() {


        myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                setAvatarname(dataSnapshot.child(auth.getUid()).child("Avatar").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public String getAvatarname() {
        return avatarname;
    }


    public String toBallAvatar() {
        avatarname = getAvatarname();
        switch (avatarname) {

            case "animepic1":
                avatarname = "pallopic1";
                break;
            case "animepic2":
                avatarname = "pallopic2";
                break;
            case "animepic3":
                avatarname = "pallopic3";
                break;
            case "animepic4":
                avatarname = "pallopic4";
                break;
            case "animepic5":
                avatarname = "pallopic5";
                break;
        }

        return avatarname;
    }
}
