package com.example.kona.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.Constraints.TAG;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_FINE_LOCATION = 0;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference nameref = database.getReference("Player");
    ArrayList<String> names = new ArrayList<>();
    Locale myLocale;


    /**
     *  This list contains banned words and symbols
     */
    public static ArrayList<String> naughtylist = new ArrayList<>();

    static {
        naughtylist.add("!");   naughtylist.add("/");   naughtylist.add("?");   naughtylist.add("'");
        naughtylist.add("@");   naughtylist.add("{");   naughtylist.add("+");   naughtylist.add("-");
        naughtylist.add("#");   naughtylist.add("}");   naughtylist.add("´");   naughtylist.add("_");
        naughtylist.add("£");   naughtylist.add("(");   naughtylist.add("`");   naughtylist.add(".");
        naughtylist.add("¤");   naughtylist.add(")");   naughtylist.add("^");   naughtylist.add(":");
        naughtylist.add("$");   naughtylist.add("[");   naughtylist.add("¨");   naughtylist.add(",");
        naughtylist.add("%");   naughtylist.add("]");   naughtylist.add("~");   naughtylist.add(";");
        naughtylist.add("&");   naughtylist.add("=");   naughtylist.add("*");   naughtylist.add("<");
        naughtylist.add("½");   naughtylist.add("§");   naughtylist.add("|");   naughtylist.add(">");
        naughtylist.add(" ");

        naughtylist.add("vittu");       naughtylist.add("jumalauta");   naughtylist.add("hintti");
        naughtylist.add("saatana");     naughtylist.add("neekeri");     naughtylist.add("kulli");
        naughtylist.add("perkele");     naughtylist.add("nekru");       naughtylist.add("kyrpä");
        naughtylist.add("helvetti");    naughtylist.add("homo");        naughtylist.add("pillu");

        naughtylist.add("fuck");        naughtylist.add("nigga");       naughtylist.add("piss");
        naughtylist.add("shit");        naughtylist.add("nigger");      naughtylist.add("cum");
        naughtylist.add("bitch");       naughtylist.add("damn");        naughtylist.add("pussy");
        naughtylist.add("ass");         naughtylist.add("hitler");      naughtylist.add("kike");

        naughtylist.add("poo");         naughtylist.add("cyka");
        naughtylist.add("pee");         naughtylist.add("blyat");
        naughtylist.add("sperm");       naughtylist.add("gook");
        naughtylist.add("smegma");      naughtylist.add("perv");
    }



    /**
     * This activity is for the user to modify profile settings
     */

    FirebaseAuth auth = FirebaseAuth.getInstance();
    final String username = auth.getCurrentUser().getDisplayName();
    EditText newName;
    TextView namePlease,deleteyes,deleteno,areyousure;
    Button changeName,japan,en;
    RelativeLayout RL;

    ImageButton avatar;
    ImageButton choice1,choice2,choice3,choice4,choice5;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        newName = findViewById(R.id.newName);
        changeName = findViewById(R.id.changeName);
        namePlease = findViewById(R.id.changeplease);
        avatar = findViewById(R.id.avatar);
        nameref.child("User").child(auth.getUid()).child("Place").setValue("moving");
        RL = findViewById(R.id.relativeavatar);
        choice1 = findViewById(R.id.imageButton2);
        choice2 = findViewById(R.id.imageButton3);
        choice3 = findViewById(R.id.imageButton4);
        choice4 = findViewById(R.id.imageButton5);
        choice5 = findViewById(R.id.imageButton6);
        areyousure = findViewById(R.id.areyousure);
        deleteno = findViewById(R.id.deleteno);
        deleteyes = findViewById(R.id.deleteyes);
        en = findViewById(R.id.ukbtn);
        japan = findViewById(R.id.japanbtn);
        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");

            }
        });

        japan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ja");

            }
        });




        getDatabaseName();
        getDatabaseAvatar();

        ActivityCompat.requestPermissions(ProfileActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);

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
     * Called when the user taps the Delete profile button
     * Deleting authed profile in response to button
     */
    public void DeleteProfile(View view) {

        setContentView(R.layout.activity_log_out);
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = GoogleAuthProvider.getCredential("email@email.com", "password123");

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Player");
                        myRef.child("User").child(user.getUid()).setValue(null);
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent logout = new Intent(ProfileActivity.this, Login.class);
                                            //Closing all activities
                                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finishAffinity();
                                            startActivity(logout);

                                        }
                                    }
                                });

                    }
                });
    }

    /**
     * Called when the user taps the Log Out button
     * Log out authed user in response to button
     */
    public void logOut(View view) {

        //LOG OUT
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {

                        // Ask user to log in
                        Intent intent = new Intent(ProfileActivity.this, Login.class);
                        //Closing all activities
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finishAffinity();
                        startActivity(intent);

                    }
                });

        finish();
    }

    /**
     *  Method for checking if a String contains a string form naughtylist
     * @return
     */

    public boolean checkNaughty(String word) {

        String naughty;
        int i;
        for (i = 0; i < naughtylist.size(); i++) {
            naughty = naughtylist.get(i);
            if (word.toLowerCase().contains(naughty.toLowerCase())){
                return false;
            }
        }
        return true;
    }

    /**
     * Method for changing display name
     * validates the name to be unique
     */


    public void nameChange(View view) {

        final String name = newName.getText().toString();
        if (name.equals(username)) {
            namePlease.setVisibility(View.VISIBLE);

            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }

        nameref.child("User").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    String databaseName = uniqueKeySnapshot.child("name").getValue().toString();
                    names.add(databaseName);
                }

                if (names.contains(name)) {
                    newName.setText(R.string.namenotavailph);

                    Toast.makeText(getApplicationContext(), R.string.namenotavail,
                            Toast.LENGTH_SHORT).show();

                }

                if (checkNaughty(name) == false) {
                    newName.setText(R.string.badnameph);

                    Toast.makeText(getApplicationContext(), R.string.badname,
                            Toast.LENGTH_SHORT).show();
                }

                if (name.length() > 15) {
                    newName.setText(R.string.nametoolongph);

                    Toast.makeText(getApplicationContext(), R.string.nametoolong,
                            Toast.LENGTH_SHORT).show();

                }

                if (name.length() < 3) {
                    newName.setText(R.string.tooshortnameph);

                    Toast.makeText(getApplicationContext(), R.string.tooshortname,
                            Toast.LENGTH_SHORT).show();

                }

                if (name.length() < 15 && name.length() > 3 && !names.contains(name) && checkNaughty(name)) {
                    nameref.child("User").child(auth.getUid()).child("name").setValue(name);

                    namePlease.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), R.string.namechanged,
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                    startActivity(intent);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


    }

    /**
     * Method for changing the users avatar from few default ones
     * @param view
     */

    public void showAvatars(View view){

        if (choice1.getVisibility() == View.GONE) {

            RL.setVisibility(View.VISIBLE);
            choice1.setVisibility(View.VISIBLE);
            choice2.setVisibility(View.VISIBLE);
            choice3.setVisibility(View.VISIBLE);
            choice4.setVisibility(View.VISIBLE);
            choice5.setVisibility(View.VISIBLE);
        } else {

            RL.setVisibility(View.GONE);
            choice1.setVisibility(View.GONE);
            choice2.setVisibility(View.GONE);
            choice3.setVisibility(View.GONE);
            choice4.setVisibility(View.GONE);
            choice5.setVisibility(View.GONE);
        }
    }

    public void changeAvatar(final View view){

        choice1.setVisibility(View.GONE);
        choice2.setVisibility(View.GONE);
        choice3.setVisibility(View.GONE);

        nameref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int chosen;
                String avatarname = null;

                if (view.equals(choice1)) {
                    chosen = 1;
                } else if (view.equals(choice2)) {
                    chosen = 2;
                } else if (view.equals(choice3)){
                    chosen = 3;
                } else if (view.equals(choice4)){
                    chosen = 4;
                } else if (view.equals(choice5)){
                    chosen = 5;
                } else {
                    chosen = 0;
                }

                RL.setVisibility(View.GONE);

                switch (chosen) {
                    case 0:
                        avatarname = "avatar_s2";
                        break;
                    case 1:
                        avatarname = "animepic1";
                        break;
                    case 2:
                        avatarname = "animepic2";
                        break;
                    case 3:
                        avatarname = "animepic3";
                        break;

                    case 4:
                        avatarname = "animepic4";
                        break;

                    case 5:
                        avatarname = "animepic5";
                        break;

                }

                nameref.child("User").child(auth.getUid()).child("Avatar").setValue(avatarname);
                getDatabaseAvatar();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void getDatabaseAvatar(){

        nameref.child("User").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dbavatar = dataSnapshot.child(auth.getUid()).child("Avatar").getValue().toString();

                Drawable d = null;

                switch (dbavatar) {
                    case "animepic1":
                        d = getResources().getDrawable(R.drawable.pallopic1);
                        break;
                    case "animepic2":
                        d = getResources().getDrawable(R.drawable.pallopic2);
                        break;
                    case "animepic3":
                        d = getResources().getDrawable(R.drawable.pallopic3);
                        break;

                    case "animepic4":
                        d = getResources().getDrawable(R.drawable.pallopic4);
                        break;

                    case "animepic5":
                        d = getResources().getDrawable(R.drawable.pallopic5);
                        break;
                }

                avatar.setBackground(d);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }


    /**
     * Called when the user taps the return button
     *
     * @param view
     */

    public void backToMenu(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * Get the display name from database
     */
    public void getDatabaseName() {
        nameref.child("User").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String databasename = dataSnapshot.child(auth.getUid()).child("name").getValue().toString();
                newName.setText(databasename);
                if (databasename.equals("newPlayer")) {
                    namePlease.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


    }

    public void showAreYouSure(View view){
        areyousure.setVisibility(View.VISIBLE);
        deleteyes.setVisibility(View.VISIBLE);
        deleteno.setVisibility(View.VISIBLE);



    }


    public void setLocale(String lang) {

        myLocale = new Locale(lang);

        Resources res = getResources();

        DisplayMetrics dm = res.getDisplayMetrics();

        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;

        res.updateConfiguration(conf, dm);

        Intent refresh = new Intent(this, ProfileActivity.class);

        startActivity(refresh);

    }


    public void hideAreYouSure(View view){

        areyousure.setVisibility(View.GONE);
        deleteyes.setVisibility(View.GONE);
        deleteno.setVisibility(View.GONE);
    }



}

