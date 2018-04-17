package com.example.kona.myapplication;

import android.*;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static android.support.constraint.Constraints.TAG;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_FINE_LOCATION = 0;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference nameref = database.getReference("Player");
    ArrayList<String> names = new ArrayList<>();
    /**
     * This activity is for the user modify profile settings
     */

    FirebaseAuth auth = FirebaseAuth.getInstance();
    final String username = auth.getCurrentUser().getDisplayName();
    EditText newName;
    TextView namePlease;
    Button changeName;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);
        newName = findViewById(R.id.newName);
        changeName = findViewById(R.id.changeName);
        namePlease = findViewById(R.id.changeplease);
        nameref.child("User").child(auth.getUid()).child("Place").setValue("moving");

        getDatabaseName();


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
                    newName.setText("Name not available!");

                    Toast.makeText(getApplicationContext(), "Name not available, try again",
                            Toast.LENGTH_SHORT).show();

                }

                if (name.length() < 15 && !names.contains(name)) {
                    nameref.child("User").child(auth.getUid()).child("name").setValue(name);

                    namePlease.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Name changed succesfully",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                    startActivity(intent);

                }


                if (name.length() > 15) {
                    newName.setText("Name is too long!");

                    Toast.makeText(getApplicationContext(), "Name is Too long! (15)",
                            Toast.LENGTH_SHORT).show();

                }

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
                if (databasename.equals(auth.getCurrentUser().getDisplayName())) {
                    namePlease.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


    }


}

