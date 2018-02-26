package com.example.kona.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;


/**
 * Created by kona on 25.2.2018.
 */

@IgnoreExtraProperties
public class User {
    public String name;
    public String email;
    public String id;

    private DatabaseReference mDatabase;

    User(){

    }

    public User(String id ,String name, String email, String place) {
        this.name = name;
        this.email = email;
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void writeNewUser(String userId, String name, String email) {
        User user = new User(userId, name, email,"moving");

        mDatabase = FirebaseDatabase.getInstance().getReference("Player");

        mDatabase.child("User").child(userId).setValue(user);
    }


}


