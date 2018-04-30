package com.example.kona.myapplication;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class Transaction {

    private final static String TAG = "TÄÄ";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    private int money, price, itemhp, userHP, currItem;
    private int newUserHP, newUserMoney;
    private ArrayList<Item> playerItems = new ArrayList<>();

    public int getUserHP() {
        return userHP;
    }

    public void setUserHP(int userHP) {
        this.userHP = userHP;
    }

    public int getItemhp() {
        return itemhp;
    }

    public void setItemhp(int hp) {
        this.itemhp = hp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setCurrItem(int currItem) {
        this.currItem = currItem;
    }


    /**
     * Request users HP from database.
     */
    public void checkHP() {
        DatabaseReference HPRef = database.getReference("Player");
        HPRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long userHP = (long) dataSnapshot.child(auth.getUid()).child("HP").getValue();
                setUserHP((int) userHP);
                Log.d(TAG, "userHP: " + getUserHP());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * update users money to database
     *
     * @param money
     */
    public void addMoney(final int money) {

        final DatabaseReference MoneyRef = database.getReference("Player");
        MoneyRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dbMoney = (long) dataSnapshot.child(auth.getUid()).child("Money").getValue();
                setMoney((int) dbMoney);

                final int totalMoney = getMoney() + money;
                MoneyRef.child("User").child(auth.getUid()).child("Money").setValue(totalMoney);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**Decreases the amount of money the player has.
     * The decreased amount is equal to the price of
     * the purchased item.
     * @param money
     */
    public void decreaseMoney(final int money) {
        final DatabaseReference MoneyRef = database.getReference("Player");
        MoneyRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int moneyAfterPurchase = getMoney() - money;
                MoneyRef.child("User").child(auth.getUid()).child("Money").setValue(moneyAfterPurchase);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Adds the currently selected item to player's inventory upon purchase.
     * The variable currItem is assigned by the ItemAdapter when an item is clicked
     * and represents the item the player is currently viewing.
     */
    public void addItem(final ArrayList<Item> storeItems) {
        final Item item = storeItems.get(currItem);
        getPlayerMoney();
        if (validateMoney(money)) {
            if (checkForExistingItem(item)) {
                updateItemAmount(item);
            } else {
                addNewItem(item);
            }
        } else {
            //inform player that more money is needed
        }
    }

    public void updateItemAmount(final Item item) {
        final DatabaseReference playerRef = database.getReference("Player");
        final Query query = playerRef.child("User").child("Items").orderByChild("name").equalTo(item.name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ++item.amount;
                decreaseMoney(item.price);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addNewItem(final Item item) {
        final DatabaseReference playerRef = database.getReference("Player");
        playerRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ++item.amount;
                decreaseMoney(item.price);
                DatabaseReference itemRef = playerRef.child("User").child(auth.getUid()).child("Items");
                itemRef.child(item.name).setValue(item);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**Compares the item to be added to the player's current items.
     * This is used to determine whether to update the Amount of an Item
     * or whether an entirely new child should be pushed.
     * @param item
     */
    public boolean checkForExistingItem(final Item item) {
        getPlayerItems();
        boolean match = false;
        for (Item i : playerItems) {
            match = i.equals(item);
            if (match) {
                break;
            }
        }
        return match;
    }

    /**Retrieves a list of names of the items the player is currently carrying.
     *
     */
    public void getPlayerItems() {
        DatabaseReference itemRef = database.getReference("Player");
        itemRef.child("User").child(auth.getUid()).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = new Item();
                    item.name = (String) snapshot.child("name").getValue();
                    playerItems.add(item);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void removeItem(final ArrayList<Item> itemList) {
        final DatabaseReference playerRef = database.getReference("Player");
        playerRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item item = itemList.get(currItem);
                --item.amount;
                DatabaseReference itemRef = playerRef.child("User").child(auth.getUid()).child("Items");
                //itemRef.child(item).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
/*
    public void removeItem(final ArrayList<Item> itemList) {

        final DatabaseReference playerRef = database.getReference("Player");
        playerRef.child("User").addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item item = itemList.get(currItem);
                DatabaseReference itemRef = playerRef.child("User").child(auth.getUid()).child("Items");
                itemRef.child(item).remove();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));
    }*/

    /**
     * Return users Money amount from database.
     */
    public void getPlayerMoney() {
        DatabaseReference MoneyRef = database.getReference("Player");
        MoneyRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dbMoney = (long) dataSnapshot.child(auth.getUid()).child("Money").getValue();
                setMoney((int) dbMoney);
                Log.d(TAG, "money: " + money);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**Checks if player has enough money to
     * make the purchase by comparing it to
     * the item price
     * @param money
     * @return
     */
    public boolean validateMoney(int money) {
        if (price <= money) {
            return true;
        } else {
            return false;
        }
    }

    // check the price of selected item

    /**
     * Check price of a item from database, and perform the transaction.
     * On success, move the items attributes to user, and decrease users money.
     * @param item
     */
    /*
    public void CheckPrice(String item){
                final String dbItem = item;

                DatabaseReference ItemRef = database.getReference("Items");
                ItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long dbPrice = (long) dataSnapshot.child(dbItem).child("Price").getValue();
                        long dbHP = (long) dataSnapshot.child(dbItem).child("HP").getValue();

                        setPrice((int)dbPrice);
                        setItemhp((int)dbHP);
                        Log.d(TAG, "Price: " + price);
                        Log.d(TAG, "itemhp: " + itemhp);
                        checkMoney();


                        if (price <= money){
                            checkHP();
                            final DatabaseReference PlayerRef = database.getReference("Player");
                            PlayerRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long userMoney = (long) dataSnapshot.child(auth.getUid()).child("Money").getValue();
                                    Log.d(TAG, "Usermoney: " + userMoney);
                                    newUserHP = (getItemhp() + getUserHP());
                                    newUserMoney = ((int)userMoney - getPrice());
                                    Log.d(TAG, "newUserHP: " + newUserHP);
                                    PlayerRef.child("User").child(auth.getUid()).child("Money").setValue(newUserMoney);

                                    // Cap the hp to value 100
                                    if(newUserHP < 100) {
                                        PlayerRef.child("User").child(auth.getUid()).child("HP").setValue(newUserHP);
                                    }
                                    else{
                                        PlayerRef.child("User").child(auth.getUid()).child("HP").setValue(100);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
*/

    /**
     * Update Users HP to database.
     *
     * @param hp
     */

    public void addHP(final int hp) {

        final DatabaseReference hpRef = database.getReference("Player");
        hpRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dbHP = (long) dataSnapshot.child(auth.getUid()).child("HP").getValue();
                setUserHP((int) dbHP);


                final int totalHP = getUserHP() + hp;
                hpRef.child("User").child(auth.getUid()).child("HP").setValue(totalHP);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
