package com.example.kona.myapplication;

/**
 * Created by Rahis on 3.5.2018.
 */

public class Player {
    long Key;
    String Name;

    public Player(long key, String name){
        this.Key = key;
        this.Name = name;

    }
    public long getKey() {
        return Key;
    }

    public String getName() {
        return Name;
    }
}
