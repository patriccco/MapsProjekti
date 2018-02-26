package com.example.kona.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class LogOutActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

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
                                            Log.d(TAG, "User account deleted.");
                                            Intent logout = new Intent(LogOutActivity.this, Login.class);
                                            //Closing all activities
                                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            //Add new Flag to start new Activity
                                            logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(logout);

                                        }
                                    }
                                });
                        finish();

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
                        Intent intent = new Intent(LogOutActivity.this, Login.class);
                        //Closing all activities
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //Add new Flag to start new Activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                    }
                });

        finish();
    }
}

