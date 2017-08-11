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

import com.example.mradmin.androidtestapp.activities.FirstActivity;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by mrAdmin on 11.08.2017.
 */

public class FirebaseApplication extends Application {

    private static final String TAG = FirebaseApplication.class.getSimpleName();

    public FirebaseAuth mAuth;

    public FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuth getFirebaseAuth(){
        return mAuth = FirebaseAuth.getInstance();
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

    public void checkUserLogin(final Context context){
        if(mAuth.getCurrentUser() != null){
            Intent profileIntent = new Intent(context, NavigationActivity.class);
            context.startActivity(profileIntent);
        } else {
            System.out.println("sasai");
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

    public void addNewUser(final Context context, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        Snackbar.make(((Activity) context).findViewById(R.id.scroll_view_sign_up), getString(R.string.success_message), Snackbar.LENGTH_LONG).show();

                        if (!task.isSuccessful()) {
                            System.out.println("sasi blya");
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
                        }else {
                            Intent profileIntent = new Intent(context, NavigationActivity.class);
                            context.startActivity(profileIntent);
                        }
                    }
                });
    }
}
