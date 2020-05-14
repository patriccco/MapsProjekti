package com.example.kona.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.ContactsContract;
import androidx.core.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.LinearLayoutManager;
import androidx.appcompat.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BenderBattleActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final ArrayList<Item> itemList = new ArrayList<>();
    private int currItem;
    private int resId;
    final PlayerStats stats = new PlayerStats();
    final EnemyStats eStats = new EnemyStats();
    BenderBattle battle = new BenderBattle();
    ProgressBar pHealthBar;
    ProgressBar eHealthBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bender_battle);
        recyclerView = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(this);
        battle.player = stats;
        battle.enemy = eStats;

        // Register to receive messages.
        // Registers an observer (mMessageReceiver) to receive Intents
        // with actions named "pass-item"
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("pass-item"));

        // Retrieves player's items for the inventory
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
                mAdapter = new BattleAdapter(itemList, resId);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Get and set player info for the UI
        final DatabaseReference playerRef = database.getReference("Player");
        playerRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String screenName = dataSnapshot.child(auth.getUid()).child("name").getValue().toString();
                TextView playerName = findViewById(R.id.player_name);
                playerName.setText(screenName);

                long currHealth = (long) dataSnapshot.child(auth.getUid()).child("HP").getValue();
                int showHealth = (int) currHealth;

                ProgressBar playerHealthBar = findViewById(R.id.player_healthbar);
                playerHealthBar.setMax(100);
                playerHealthBar.setProgress(showHealth);
                pHealthBar = playerHealthBar;

                long dbPower = (long) dataSnapshot.child(auth.getUid()).child("Power").getValue();
                stats.power = (int) dbPower;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Retrieves enemy data and inserts it into GUI
        final DatabaseReference enemyRef = database.getReference("Enemy");
        enemyRef.child("Machinegirl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String screenName = dataSnapshot.child("Name").getValue().toString();
                TextView enemyName = findViewById(R.id.enemy_name);
                enemyName.setText(screenName);

                String enemyImage = dataSnapshot.child("Image").getValue().toString();
                ImageView enemyImg = findViewById(R.id.enemy_img);
                resId = getResources().getIdentifier(enemyImage,"drawable",getPackageName());
                enemyImg.setImageResource(resId);

                long dbHealth = (long) dataSnapshot.child("Health").getValue();
                int enemyHealth = (int) dbHealth;

                ProgressBar enemyHealthBar = findViewById(R.id.enemy_healthbar);
                enemyHealthBar.setMax(100);
                enemyHealthBar.setProgress(enemyHealth);
                eHealthBar = enemyHealthBar;

                long dbPower = (long) dataSnapshot.child("Health").getValue();
                eStats.power = (int) dbPower;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**Hide statusbar**/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
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
     * Called when the Attack button is pressed in the battle view.
     * Calls the required methods for one turn in the game.
     * @param view
     */
    public void attack(View view) {
        battle.attack(stats.power);
        battle.enemyAttack(eStats.power);
        Toast toast= Toast.makeText(getApplicationContext(),
                "You hit the enemy!\nThe enemy hits back!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        int newPlayerHealth = stats.health;
        int newEnemyHealth = eStats.health;
        pHealthBar.setProgress(newPlayerHealth);
        eHealthBar.setProgress(newEnemyHealth);
    }

    /**
     * Called when the Heal button is pressed in the battle view.
     * Calls the required methods for the healing action.
     * @param view
     */
    public void useItem(View view) {
        Transaction transaction = new Transaction();
        Item toUse = itemList.get(currItem);
        transaction.useCurative(toUse);
        Toast toast= Toast.makeText(getApplicationContext(),
                "You healed!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

    }
}
