package com.example.mradmin.androidtestapp;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.mradmin.androidtestapp.activities.FirstActivity;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by mrAdmin on 11.08.2017.
 */

public class FirebaseApplication extends Application {

    private static final String TAG = FirebaseApplication.class.getSimpleName();

    public FirebaseAuth mAuth;

    private DatabaseReference userDB;
    private DatabaseReference friendRequestDB;
    private DatabaseReference friendsDB;
    private DatabaseReference notificationsDB;
    private DatabaseReference dialoguesDB;
    private DatabaseReference blogDB;

    private FirebaseStorage mStorage;
    private FirebaseStorage blogStorage;

    public FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuth getFirebaseAuth() {
        return mAuth = FirebaseAuth.getInstance();
    }

    public DatabaseReference getFirebaseDatabase() {
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public DatabaseReference getFirebaseFriendRequestDatabase(){
        return FirebaseDatabase.getInstance().getReference().child("Friend_req");
    }

    public DatabaseReference getFirebaseFriendsDatabase(){
        return FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    public DatabaseReference getNotificationsDatabase(){
        return FirebaseDatabase.getInstance().getReference().child("Notifications");
    }

    public DatabaseReference getDialoguesDatabase() {
        return FirebaseDatabase.getInstance().getReference().child("Dialogues");
    }

    public DatabaseReference getFirebaseBlogDatabase() {
        return FirebaseDatabase.getInstance().getReference().child("Blog");
    }

    public StorageReference getFirebaseStorage() {
        return FirebaseStorage.getInstance().getReference().child("profile_images");
    }

    public StorageReference getBlogStorage() {
        return FirebaseStorage.getInstance().getReference().child("blog_images");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Picasso

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth = getFirebaseAuth();
        userDB = getFirebaseDatabase();

        FirebaseUser curUser = mAuth.getCurrentUser();

        if (curUser != null) {

            final DatabaseReference user = userDB.child(mAuth.getCurrentUser().getUid());

            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        user.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public void createNewUser(Context context, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password);
    }

    public void loginAUser(final Context context, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password);
    }

    public String getFirebaseUserAuthenticateId() {
        String userId = null;
        if (mAuth.getCurrentUser() != null) {
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

    public void checkUserLogin(final Context context) {
        if (mAuth.getCurrentUser() != null) {
            Intent profileIntent = new Intent(context, NavigationActivity.class);
            context.startActivity(profileIntent);
        }
    }

    public void isUserCurrentlyLogin(final Context context) {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (null != user) {
                    Intent profileIntent = new Intent(context, NavigationActivity.class);
                    context.startActivity(profileIntent);
                } else {
                    Intent loginIntent = new Intent(context, FirstActivity.class);
                    context.startActivity(loginIntent);
                }
            }
        };
    }

    public void addInfoInDatabase(final String name, String image, String status, String thumb) {

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference curUser = userDB.child(userId);

        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        HashMap<String, String> values = new HashMap<>();
        values.put("device_token", deviceToken);
        values.put("name", name);
        values.put("image", image);
        values.put("status", status);
        values.put("thumb_image", thumb);

        curUser.setValue(values);

    }

    public void addNewUser(final Context context, String email, String password, final String name) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Signing up");
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {

                            userDB = getFirebaseDatabase();

                            addInfoInDatabase(name, "default", "Hi there, I'm using ...", "default");

                            userProfile(name);

                            Snackbar.make(((Activity) context).findViewById(R.id.scroll_view_sign_up), getString(R.string.success_message), Snackbar.LENGTH_LONG).show();

                        } else {

                            Snackbar.make(((Activity) context).findViewById(R.id.scroll_view_sign_up), getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();

                        }

                        pd.dismiss();

                    }
                });
    }

    public void loginUser(final Context context, String email, String password) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Signing in");
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            //errorMessage.setText("Failed to login");
                            //Snackbar.make(((Activity) context).findViewById(R.id.nested_scroll_view_sign_in), getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).show();
                            Snackbar.make(((Activity) context).findViewById(R.id.nested_scroll_view_sign_in), getString(R.string.sign_in_network_failure), Snackbar.LENGTH_LONG).show();
                        } else {
                            //checkUserExist(((Activity) context).getParent());

                            String curUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            getFirebaseDatabase().child(curUserId).child("device_token").setValue(deviceToken)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Snackbar.make(((Activity) context).findViewById(R.id.nested_scroll_view_sign_in), getString(R.string.success_message), Snackbar.LENGTH_LONG).show();

                                            Intent profileIntent = new Intent(context, NavigationActivity.class);
                                            context.startActivity(profileIntent);


                                        }
                                    });

                        }
                        pd.dismiss();
                    }
                });
    }

    public void userProfile(String string) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(string.trim())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TEST", "user profile updated");
                            }
                        }
                    });
        }
    }
}
