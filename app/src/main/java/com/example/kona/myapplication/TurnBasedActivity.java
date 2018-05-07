package com.example.kona.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.kona.myapplication.MapsActivity.GetImage;

public class TurnBasedActivity extends AppCompatActivity {
    //Connect to firebase
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference gameRef = database.getReference("Game").child("TurnGame");
    DatabaseReference nameRef = database.getReference("Player").child("User");
    private ValueEventListener mListener;
    Avatar curUserAvatar = new Avatar();
    Avatar opponentAvatar = new Avatar();
    TextView EventText, OpponentName;
    ImageButton attack, dodge, heal;
    ImageView OpponentImage, curUserImage;
    String curUser, opponent, OpponentState, realOpponent;
    Boolean onclick = false;
    Player player1, player2;
    long status1, status2, curUserHP, OpponentHP;
    Transaction transaction = new Transaction();
    ArrayList<Player> players = new ArrayList<>();
    int j = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_based);

        OpponentImage = findViewById(R.id.avatar);
        curUserImage = findViewById(R.id.curAvatar);
        EventText = findViewById(R.id.EventText);
        OpponentName = findViewById(R.id.opponentname);
        attack = findViewById(R.id.item1);
        dodge = findViewById(R.id.item2);
        heal = findViewById(R.id.item3);
        attack.setVisibility(View.GONE);
        dodge.setVisibility(View.GONE);
        heal.setVisibility(View.GONE);
        curUserAvatar.getDatabaseAvatar();
        getUsers();

        timer(4000);


    }

    /**
     * This method sets timer to the start of the game
     * This method also starts the game
     *
     * @param time
     */
    public void timer(long time) {
        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                CreateGame();
            }
        }.start();
    }

    /**
     * This is a timer for texts
     *
     * @param time
     */
    public void TextTimer(long time) {
        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

            }
        }.start();
    }

    /**
     * This method sets timer to the end of the game and resets the database for both players
     * This method also changes the Activity to MapsActivity
     *
     * @param time
     */
    public void endtimer(long time) {
        new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(TurnBasedActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();

                gameRef.child("TurnGame").child(opponent).removeValue();
                nameRef.child(auth.getUid()).child("challengedTurn").setValue("no");
                nameRef.child(auth.getUid()).child("inturngame").setValue(false);

                gameRef.removeEventListener(mListener);
            }
        }.start();
    }
    private void getAvatar(){

        curUserImage.setBackground(GetImage(TurnBasedActivity.this, curUserAvatar.toBallAvatar()));
    }

    /**
     * This method gets both players names
     * and sets the avatar for current user.
     * It sets the opponents name over the opponents avatar.
     */
    public void getUsers() {
        Log.e("", " getUsers");
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                curUser = dataSnapshot.child(auth.getUid()).child("name").getValue().toString();
                opponent = dataSnapshot.child(auth.getUid()).child("challengedTurn").getValue().toString();
                gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                            //Loop 1 to go through all the child nodes of users
                            String challengedName = uniqueKeySnapshot.getKey();
                            if (!challengedName.equals(curUser)) {
                                realOpponent = challengedName;
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                OpponentName.setText(opponent);
                getAvatar();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method creates gameroom and starts the game
     * The turnbasedActivity is started from here
     * It sets the avatar for opponent
     *
     * @param curUser  this is the users name
     * @param opponent this is the opponents name
     */
    public void CreateGame() {
        Log.e("", " CreateGame");
        //EventText: Lets kick some ass!

        EventText.setText(R.string.EventText);
        TextTimer(1000);
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curUserHP = (long) dataSnapshot.child(opponent).child(curUser).child("HP").getValue();
                OpponentHP = (long) dataSnapshot.child(opponent).child(opponent).child("HP").getValue();
                opponentAvatar.setAvatarname(dataSnapshot.child(opponent).child(opponent).child("Avatar").getValue().toString());getAvatar();
                OpponentImage.setBackground(GetImage(TurnBasedActivity.this, opponentAvatar.toBallAvatar()));
                Log.e("", " gameref" + curUserHP + OpponentHP);
                status1 = (long) dataSnapshot.child(opponent).child(curUser).child("Status").getValue();
                status2 = (long) dataSnapshot.child(opponent).child(opponent).child("Status").getValue();
                Log.e("", " gameref" + status1 + status2);
                player1 = new Player(status1, curUser);
                player2 = new Player(status2, opponent);
                players.add(player1);
                players.add(player2);

                playOneGame();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    /**
     * basic turn based template method
     */
    private void playOneGame() {
        Log.e("", " playOneGame");

        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curUserHP = (long) dataSnapshot.child(opponent).child(curUser).child("HP").getValue();
                OpponentHP = (long) dataSnapshot.child(opponent).child(opponent).child("HP").getValue();
                status1 = (long) dataSnapshot.child(opponent).child(curUser).child("Status").getValue();

                String jo = dataSnapshot.child(opponent).child(curUser).child("Action").getValue().toString();

                long tStop;
                double start, finaltime;
                if (status1 == 2) {
                    Log.e("", " gameref" + curUserHP + OpponentHP);
                    if (OpponentHP >= 0 || curUserHP >= 0) {
                        Log.e("", " playonegamewhile" + curUserHP + OpponentHP);
                        start = (double) System.currentTimeMillis();
                        makePlay(j);
                        j = (j + 1) % 2;
                        tStop = System.currentTimeMillis();
                        finaltime = (((double) tStop - start) / 1000);
                        if (finaltime >= 10000) {
                            printWinner(j);
                        }

                    } else {
                        printWinner(j);
                    }
                }else{
                    TextTimer(1000);
                    playOneGame();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    /**
     * This method is one turn inside the game
     *
     * @param player this tells whom turn it is
     */

    private void makePlay(final int player) {
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onclick = false;
                Log.e("", " makePlay");
                //Info about whos turn it is

                    final TextView EventText = findViewById(R.id.EventText);

                    //EventText: Player "player" turn!
                    EventText.setText(R.string.Player + players.get(player).getName() + R.string.turn +" "+ status1);
                    //wait until the item1 is pressed
                    attack.setVisibility(View.VISIBLE);
                    dodge.setVisibility(View.VISIBLE);
                    heal.setVisibility(View.VISIBLE);
                    TextTimer(1000);
                    EventText.setText(R.string.UseItem);



                //attack onclick handler
                attack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("", " AttackOnClick");
                        //EventText: I hope "player" slips on this banana!
                        EventText.setText(R.string.banana);
                        gameRef.child(opponent).child(players.get(player).getName()).child("Action").setValue("Attack");
                        setState("Attack", players.get(player));
                        attack.setVisibility(View.GONE);
                        dodge.setVisibility(View.GONE);
                        heal.setVisibility(View.GONE);
                        EventText.setText(R.string.Player + opponent + R.string.turn +" " + status2);
                        gameRef.child(opponent).child(player1.getName()).child("Status").setValue(1);
                        gameRef.child(opponent).child(realOpponent).child("Status").setValue(2);
                        playOneGame();
                    }
                });

                //dodge onclick handler
                dodge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("", " dodgeOnClick");
                        EventText.setText(R.string.fisthit);
                        gameRef.child(opponent).child(players.get(player).getName()).child("Action").setValue("Dodge");
                        setState("Dodge", players.get(player));
                        attack.setVisibility(View.GONE);
                        dodge.setVisibility(View.GONE);
                        heal.setVisibility(View.GONE);
                        EventText.setText(R.string.Player + opponent + R.string.turn +" " + status2);
                        gameRef.child(opponent).child(players.get(player).getName()).child("Status").setValue(1);
                        gameRef.child(opponent).child(realOpponent).child("Status").setValue(2);
                        playOneGame();
                    }
                });

                //Heal onclick handler
                heal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("", " healOnClick");
                        gameRef.child(opponent).child(players.get(player).getName()).child("Action").setValue("Heal");
                        setState("Heal", players.get(player));
                        attack.setVisibility(View.GONE);
                        dodge.setVisibility(View.GONE);
                        heal.setVisibility(View.GONE);
                        EventText.setText(R.string.Player + opponent + R.string.turn +" " + status2);
                        gameRef.child(opponent).child(players.get(player).getName()).child("Status").setValue(1);
                        gameRef.child(opponent).child(realOpponent).child("Status").setValue(2);
                        playOneGame();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * This method sets turnactions to database and handles them
     *
     * @param state
     * @param player
     */
    public void setState(final String state, final Player player) {
        Log.e("", " setState");

        //Get health points and opponents state from database
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curUserHP = (long) dataSnapshot.child(opponent).child(player.getName()).child("HP").getValue();
                OpponentHP = (long) dataSnapshot.child(opponent).child(opponent).child("HP").getValue();
                OpponentState = (String) dataSnapshot.child(opponent).child(opponent).child("Action").getValue();
                //Set the action for your turn
                switch (state) {
                    case "Attack":
                        Log.e("", " AttackState");
                        if (OpponentState.equals("Dodge")) {
                            int rndm = (int) Math.ceil(Math.random() * 10);
                            if (rndm < 3) {
                                EventText.setText(R.string.dodged);
                            } else {
                                EventText.setText(R.string.hit);
                                OpponentHP = OpponentHP - 10;
                                gameRef.child(opponent).child(opponent).child("HP").setValue(OpponentHP);
                            }
                        } else {
                            EventText.setText(R.string.hit);
                            OpponentHP = OpponentHP - 10;
                            gameRef.child(opponent).child(opponent).child("HP").setValue(OpponentHP);

                        }
                        break;
                    case "Dodge":
                        Log.e("", " DodgeState");
                        gameRef.child(opponent).child(player.getName()).child("Action").setValue("Dodge");
                        EventText.setText(R.string.matrixdodge);
                        break;
                    case "Heal":
                        Log.e("", " HealState");
                        if (curUserHP <= 100) {
                            curUserHP = curUserHP + 10;
                            gameRef.child(opponent).child(player.getName()).child("HP").setValue(curUserHP);
                        } else {
                            EventText.setText(R.string.fullHP);
                        }
                        break;
                    default:

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    /**
     * This method points the winner to EventText
     *
     * @param player contains the winners name
     */

    private void printWinner(final int playerstatus) {

        //Add 30 money to winners account
        if (players.get(playerstatus).getKey() == 1) {
            transaction.addMoney(30);
            Toast.makeText(getApplicationContext(), "You Won! You got 30 Money!",
                    Toast.LENGTH_SHORT).show();

        } else if (players.get(playerstatus).getKey() == 2) {
            transaction.addMoney(30);

        } else {
            Toast.makeText(getApplicationContext(), "You Lost!",
                    Toast.LENGTH_SHORT).show();

        }
        endtimer(3000);

    }


}
