package com.example.mradmin.androidtestapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.activities.FirstActivity;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by mrAdmin on 11.08.2017.
 */

public class FirebaseApplication extends Application {

    private static final String TAG = FirebaseApplication.class.getSimpleName();

    public FirebaseAuth mAuth;

    private DatabaseReference userDB;

    public FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuth getFirebaseAuth(){
        return mAuth = FirebaseAuth.getInstance();
    }

    public DatabaseReference getFirebaseDatabase() {
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void createNewUser(Context context, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password);
    }

    public void loginAUser(final Context context, String email, String password){
        mAuth.signInWithEmailAndPassword(email, password);
    }

    public String getFirebaseUserAuthenticateId() {
        String userId = null;
        if(mAuth.getCurrentUser() != null){
            userId = mAuth.getCurrentUser().getUid();
        }
        return userId;
    }

    public boolean checkUserExistInDatabase() {
        userDB = getFirebaseDatabase();

        String userId = mAuth.getCurrentUser().getUid();
        if (userId != null) {
            DatabaseReference curUser = userDB.child(userId);
            if (curUser != null)
                return true;
            else
                return false;
        }

        return false;
    }

    public void checkUserLogin(final Context context){
        if(mAuth.getCurrentUser() != null){
            Intent profileIntent = new Intent(context, NavigationActivity.class);
            context.startActivity(profileIntent);
        }
    }

    public void isUserCurrentlyLogin(final Context context){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(null != user){
                    Intent profileIntent = new Intent(context, NavigationActivity.class);
                    context.startActivity(profileIntent);
                }else{
                    Intent loginIntent = new Intent(context, FirstActivity.class);
                    context.startActivity(loginIntent);
                }
            }
        };
    }

    public void addInfoInDatabase(final String name, String image, String status) {
        userDB = getFirebaseDatabase();

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference curUser = userDB.child(userId);

        HashMap<String, String> values = new HashMap<>();
        values.put("name", name);
        values.put("image", image);
        values.put("status", status);

        curUser.setValue(values);

    }

    public void addNewUser(final Context context, String email, String password, final String name){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {

                            addInfoInDatabase(name, "default", "Hi there, I'm using ...");

                            userProfile(name);

                            Snackbar.make(((Activity) context).findViewById(R.id.scroll_view_sign_up), getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(((Activity) context).findViewById(R.id.scroll_view_sign_up), getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void loginUser(final Context context, String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            //errorMessage.setText("Failed to login");
                            Snackbar.make(((Activity) context).findViewById(R.id.nested_scroll_view_sign_in), getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).show();
                        } else {
                            //checkUserExist(((Activity) context).getParent());
                            Intent profileIntent = new Intent(context, NavigationActivity.class);
                            context.startActivity(profileIntent);
                        }
                    }
                });
    }

    public void userProfile(String string){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(string.trim())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("TEST", "user profile updated");
                            }
                        }
                    });
        }
    }
}
