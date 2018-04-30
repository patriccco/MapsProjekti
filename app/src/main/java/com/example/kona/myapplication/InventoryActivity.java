package com.example.kona.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final ArrayList<Item> itemList = new ArrayList<>();
    private int currItem;
    private int resId;
    private TextView itemInfo;
    private ImageView imageLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inventory);
        recyclerView = findViewById(R.id.recycler);
        itemInfo = findViewById(R.id.item_info);
        imageLarge = findViewById(R.id.item_large);
        layoutManager = new LinearLayoutManager(this);

        DatabaseReference itemRef = database.getReference("Player");
        itemRef.child("User").child(auth.getUid()).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = new Item();

                    item.name = (String) snapshot.child("name").getValue();
                    item.info = (String) snapshot.child("info").getValue();
                    item.image = (String) snapshot.child("image").getValue();
                    item.type = (String) snapshot.child("type").getValue();

                    //long itemAmount = (long) snapshot.child("Amount").getValue();
                    //item.amount = (int) itemAmount;

                    String img = item.image;
                    resId = getResources().getIdentifier(img,"drawable",getPackageName());

                    itemList.add(item);
                }
                recyclerView.setLayoutManager(layoutManager);
                mAdapter = new ItemAdapter(itemList, resId, itemInfo, imageLarge);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void discardItem(View view) {
        Transaction transaction = new Transaction();
        //returns the current List of items
        transaction.addItem(itemList);

        Toast toast= Toast.makeText(getApplicationContext(),
                "Item discarded", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }



}
