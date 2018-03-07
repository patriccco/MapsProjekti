package com.example.kona.myapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by kona on 28.2.2018.
 */

public class Transaction {

    private final static String TAG = "TÄÄ";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    private int money,price,itemhp,userHP;
    private int newUserHP,newUserMoney;

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

    public void checkHP(){
        DatabaseReference HPRef = database.getReference("Player");
        HPRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long userHP = (long) dataSnapshot.child(auth.getUid()).child("HP").getValue();
                setUserHP((int)userHP);
                Log.d(TAG, "userHP: " + getUserHP());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void addMoney(final int money){

        final DatabaseReference MoneyRef = database.getReference("Player");
        MoneyRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dbMoney = (long) dataSnapshot.child(auth.getUid()).child("Money").getValue();
                setMoney((int)dbMoney);

                final int totalmoney = getMoney() + money;
                MoneyRef.child("User").child(auth.getUid()).child("Money").setValue(totalmoney);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    public void addHP(final int hp) {

        final DatabaseReference MoneyRef = database.getReference("Player");
        MoneyRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dbMoney = (long) dataSnapshot.child(auth.getUid()).child("HP").getValue();
                setMoney((int) dbMoney);


                final int totalHP = getMoney() + hp;
                MoneyRef.child("User").child(auth.getUid()).child("Money").setValue(totalHP);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Check the current amount of money
    public void checkmoney() {
        DatabaseReference MoneyRef = database.getReference("Player");
        MoneyRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dbMoney = (long) dataSnapshot.child(auth.getUid()).child("Money").getValue();
                setMoney((int)dbMoney);
                Log.d(TAG, "money: " + money);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

            // check the price of selected item
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


                        if (valid()){
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
    //Validate that the player has enough money for the product.
    public boolean valid(){
        if (price <= money){
            return true;
        }
        else{
            return false;
        }


    }

}

