package com.example.kona.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;


/**
 * Created by kona on 1.2.2018.
 */

public class Login extends AppCompatActivity {


    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String dBUID;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (auth.getCurrentUser() != null) {

            // already signed in
            DatabaseReference myRef = database.getReference("Player");
            startActivity(new Intent(Login.this, MapsActivity.class));
            finish();
        } else {
            // not signed in
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                DatabaseReference MyRef = database.getReference("Player");
                Log.d("ASDF", " " + auth.getCurrentUser().getUid());
                MyRef.child("User").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("id")){
                            //IT EXISTS
                        }
                        else{
                            String email = auth.getCurrentUser().getEmail().toString();
                            String name = auth.getCurrentUser().getDisplayName().toString();

                            User user = new User();
                            user.writeNewUser(auth.getUid(), name, email);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Player");
                            myRef.child("User").child(auth.getUid()).child("Place").setValue("moving");
                            myRef.child("User").child(auth.getUid()).child("HP").setValue(100);
                            myRef.child("User").child(auth.getUid()).child("Money").setValue(10);
                            myRef.child("User").child(auth.getUid()).child("Quest").child("latitude").setValue(0.01);
                            myRef.child("User").child(auth.getUid()).child("Quest").child("longitude").setValue(0.01);

                            myRef.child("User").child(auth.getUid()).child("latitude").setValue(0.01);
                            myRef.child("User").child(auth.getUid()).child("longitude").setValue(0.01);

                            myRef.child("User").child(auth.getUid()).child("Quest").child("newQuest").setValue(false);
                            myRef.child("User").child(auth.getUid()).child("Quest").child("isQuest").setValue(false);
                            myRef.child("User").child(auth.getUid()).child("Quest").child("Questname").setValue("noquest");
                            myRef.child("User").child(auth.getUid()).child("Quest").child("Questvicinity").setValue("noquest");


                            //IT DOESNT EXISTS
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                // Write a message to the database





                startActivity(new Intent(Login.this,MapsActivity.class));
                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login","Login canceled by User");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login","No Internet Connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login","Unknown Error");
                    return;
                }
            }
            Log.e("Login","Unknown sign in response");
        }
    }


}