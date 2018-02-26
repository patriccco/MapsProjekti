package com.example.kona.myapplication;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kona on 16.2.2018.
 */

public class ShopView extends AppCompatActivity{

    ArrayList<String> PlaceNames = new ArrayList<>();
    private final static String TAG = "TÄÄ";
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ListView mainListView ;
    int money,price;

    ArrayList <String> products = new ArrayList<>();

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop);
        final ArrayAdapter <String> adapter  = new ArrayAdapter<String>(this,R.layout.shoprow,products);

        try {
            InputStream is = (InputStream) new URL("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAP0SURBVEhL3ZbtT1tVGMBJ1EyN0Q8m+g/4wUQ/aDTGaCLJjFDaW4zKjC+FAqPdYF2Co7SgGCZzDmN8wfnW3vcRGWmUiUZKb2/b6RR0golZN657wY7pxkbLpLe9bSnt47nd2YWFJcIoS/SXnKQ5fc7z6znnuc9tyf+Gaqr14QbW2V9PO6a292zfgKfXn2q38+l6xplt3t8hm0m7Dk9fHyxc23jHwBtgYdu/xVPXhxrSUdf+xc5EA9emoM+P4On1p57a8eC23leVvtEPwco64/VUy/34q/XFwdi8bw52wa8RHvpGe8DKtC7YSdvj+Ov1oZOxPFZLOWDX4C7wHXHB+B88vOfrhkamJdvtst6Bw4rPV2zlbvEbW1o4/AGg4wZ7fwc497cC9+XW+X53VRcOKz4hvnJs5tjbALO9MHnKDdRQC8xNmGBO2gIh3ngShxWXoR7dBpHWp7Pn6YJYHfJxK8SROD5hBj9VkcGhxUWgyg0jnhfmLkvzMQ7iUk1BLEu1INAV8zi0uAQ544HTh1/PXxZnz72Pd4vG7xYIssRfOLR4DLvK7g0wRCp7ntGOORVp08TRI00Q5I0DOLw4eDybbgiyxl8iP72m7TYfpZHQrImPCqak4C5/ES8pDgHG2PPzgEnOx/Zpu81MdWpSGR1zgDXER96tugUvWTsipX/r+8+fS86fozRpbuZTJLxUVOo4dWhzxk/rd+MlayP0UeltqFgGfujflMicJTUpzO6D5EmbJp2TtoJIE7NqPF567fjcumcCjGE67GtK5aLcEqlaUO2aVJbqYMTzbHLYXUbgpavnaxdxq580WAMsMXmor0qOSe9cIVRH+nSHJo1PVEN4+OWUnzV8hlOsDt8nT90lUhV7REYfHxusjV9NmI9SoEw2L0pR0zhxsDYd4IyiWvE41coQyY13BziCFWmDEha2pRJTHy8XxlhIo+qVcXdSh/pZCpjT6CrEEFt6M063MgSGaBAZgywdbE7PTy9W61Jh5sxO9JjULe6ysFMLjB14XgkyRN+Y66GbcLp/R71HdIcedIcJObL3KkKm8HwuE6IRCxc6U1KkdZtxupUT5CqF37xblIWZKytV7ULpM+qRLheqPTjsNSnoBx/3ufUP4FQrx+suvw81eSUX5TVh7oILPx6LrW+pMPJjQwbdpSyQBluos/RGnGp19HZuHJZCryyowoULe1GV2pfJZMkMF482wTF/tYKEf6NK7/LzT96JU1wbe2oenaYcZTn5xI5L71D1WFGxXJxohLPjjfmw96VEkNen0KmEfKTOpL708dK1QbaV3uOyPzGqtjeB1OX8jF4O8pV/or8r3/nJim40px/q1d2Ow/+rlJT8A1alZtq5aWcPAAAAAElFTkSuQmCC").getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
        mainListView = findViewById( R.id.shoplist);
    }
    @Override
    protected void onPause() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").child(auth.getUid()).child("Place").setValue("moving");
        super.onPause();
    }

    public void checkmoney(){

        DatabaseReference myRef = database.getReference("Player");
        myRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setMoney((Integer) dataSnapshot.child(auth.getUid()).child("money").getValue());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if (Transaction()){

        }

    }



            public boolean Transaction(){

        if (price <= getMoney()){
            return true;



        }
        else{
            return false;
        }


    }

}
