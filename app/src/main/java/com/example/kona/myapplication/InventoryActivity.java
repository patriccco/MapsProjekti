package com.example.kona.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import androidx.core.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        // Register to receive messages.
        // Registers an observer (mMessageReceiver) to receive Intents
        // with actions named "pass-item"
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("pass-item"));

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

                    long itemPrice = (long) snapshot.child("price").getValue();
                    item.price = (int) itemPrice;
                    long itemValue = (long) snapshot.child("value").getValue();
                    item.value = (int) itemValue;
                    long itemPower = (long) snapshot.child("power").getValue();
                    item.power = (int) itemPower;
                    long itemAmount = (long) snapshot.child("amount").getValue();
                    item.amount = (int) itemAmount;

                    String img = item.image;
                    item.resId = getResources().getIdentifier(img,"drawable",getPackageName());

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

    /**
     * Receives the message passed by intent.
     */
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            currItem = (int) intent.getLongExtra("item",0);
        }
    };

    /**
     * Called when the Use button is pressed in the Inventory view.
     * Checks whether the chosen item is a weapon or a curative
     * and calls the required methods for the Use action.
     * @param view
     */
    public void useItem(View view) {
        Transaction transaction = new Transaction();
        Item toUse = itemList.get(currItem);
        if (toUse.type.equals("Weapon")) {
            transaction.equipWeapon(toUse);
            Toast toast= Toast.makeText(getApplicationContext(),
                    "Weapon equipped!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            transaction.useCurative(toUse);
            Toast toast= Toast.makeText(getApplicationContext(),
                    "Item used!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }


    public void discardItem(View view) {
        Transaction transaction = new Transaction();
        Item toDiscard = itemList.get(currItem);
        transaction.removeItem(toDiscard);

        Toast toast= Toast.makeText(getApplicationContext(),
                "Item discarded", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }



}
