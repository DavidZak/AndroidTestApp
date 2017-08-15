package com.example.mradmin.androidtestapp.activities;

import android.icu.text.DateFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageViewProfileImage;
    private TextView textViewProfileName;
    private TextView textViewProfileStatus;
    private TextView textViewProfileFriendsCount;
    private Button buttonFriendRequest;
    private Button buttonDeclineFriendRequest;

    private DatabaseReference userDB;

    private DatabaseReference friendRequestDB;

    private DatabaseReference friendsDatabase;

    private FirebaseUser currentUser;

    private String currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String title = getIntent().getStringExtra("title").toString(); // Now, message has Drawer title
        setTitle(title);

        final String userId = getIntent().getExtras().getString("user_id");
        userDB = ((FirebaseApplication)getApplication()).getFirebaseDatabase().child(userId);
        friendRequestDB = ((FirebaseApplication)getApplication()).getFirebaseFriendRequestDatabase();
        currentUser = ((FirebaseApplication)getApplication()).getFirebaseAuth().getCurrentUser();
        friendsDatabase = ((FirebaseApplication)getApplication()).getFirebaseFriendsDatabase();

        imageViewProfileImage=(ImageView)findViewById(R.id.imageViewContactImage);
        textViewProfileName = (TextView)findViewById(R.id.textViewContactName);
        textViewProfileStatus = (TextView)findViewById(R.id.textViewContactStatus);
        textViewProfileFriendsCount = (TextView)findViewById(R.id.textViewContactFriensCount);

        buttonFriendRequest = (Button)findViewById(R.id.inviteFriendButton);
        buttonDeclineFriendRequest = (Button)findViewById(R.id.declineFriendButton);
        buttonDeclineFriendRequest.setVisibility(View.INVISIBLE);
        buttonDeclineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonDeclineFriendRequest.setEnabled(false);

                friendRequestDB.child(currentUser.getUid()).child(userId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                friendRequestDB.child(userId).child(currentUser.getUid()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                currentState = "not_friends";
                                                buttonFriendRequest.setText("Send friend request");
                                                buttonDeclineFriendRequest.setEnabled(true);
                                                buttonDeclineFriendRequest.setVisibility(View.INVISIBLE);

                                                Toast.makeText(ProfileActivity.this,"REQUEST SENT",Toast.LENGTH_LONG).show();

                                            }
                                        });

                            }
                        });
            }
        });

        currentState = "not_friends";

        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                textViewProfileName.setText(displayName);
                textViewProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.mipmap.ic_launcher_1).into(imageViewProfileImage);

                // --------- FRIEND LIST / REQUEST FEATURE

                friendRequestDB.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(userId)){

                            String requestType = dataSnapshot.child(userId).child("request_type").getValue().toString();

                            if (requestType.equals("received")){

                                currentState = "req_received";
                                buttonFriendRequest.setText("Accept friend request");
                                buttonDeclineFriendRequest.setVisibility(View.VISIBLE);

                            } else if (requestType.equals("sent")){

                                currentState = "req_sent";
                                buttonFriendRequest.setText("Cancel friend request");
                                buttonDeclineFriendRequest.setVisibility(View.INVISIBLE);

                            }
                        } else {
                            friendsDatabase.child(currentUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.hasChild(userId)){

                                                currentState = "friends";
                                                buttonFriendRequest.setText("Unfriend this person");

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonFriendRequest.setEnabled(false);

                //------------- NOT FRIENDS STATE
                if (currentState.equals("not_friends")){

                    friendRequestDB.child(currentUser.getUid()).child(userId)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                friendRequestDB.child(userId).child(currentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        currentState = "req_sent";
                                        buttonFriendRequest.setText("Cancel friend request");

                                        Toast.makeText(ProfileActivity.this,"REQUEST SENT",Toast.LENGTH_LONG).show();

                                    }
                                });

                            } else {

                                Toast.makeText(ProfileActivity.this,"FAILED SENDING REQUEST",Toast.LENGTH_LONG).show();
                            }

                            buttonFriendRequest.setEnabled(true);

                        }
                    });

                }

                //----------------- CANCEL REQUEST
                if (currentState.equals("req_sent")){

                    friendRequestDB.child(currentUser.getUid()).child(userId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendRequestDB.child(userId).child(currentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            buttonFriendRequest.setEnabled(true);
                                            currentState = "not_friends";
                                            buttonFriendRequest.setText("Send friend request");

                                            Toast.makeText(ProfileActivity.this,"REQUEST SENT",Toast.LENGTH_LONG).show();

                                        }
                                    });

                        }
                    });

                }

                // --------------- REQ RECEIVED STATE

                if (currentState.equals("req_received")){

                    buttonDeclineFriendRequest.setVisibility(View.INVISIBLE);

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    friendsDatabase.child(currentUser.getUid()).child(userId).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendsDatabase.child(userId).child(currentUser.getUid()).setValue(currentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    friendRequestDB.child(currentUser.getUid()).child(userId).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    friendRequestDB.child(userId).child(currentUser.getUid()).removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    buttonFriendRequest.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    buttonFriendRequest.setText("Unfriend this person");

                                                                                    Toast.makeText(ProfileActivity.this,"REQUEST ACCEPTED",Toast.LENGTH_LONG).show();

                                                                                }
                                                                            });

                                                                }
                                                            });


                                                }
                                            });

                                }
                            });

                }


                //--------------------- FRIENDS STATE

                if (currentState.equals("friends")){

                    friendsDatabase.child(currentUser.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendsDatabase.child(userId).child(currentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    buttonFriendRequest.setEnabled(true);
                                                    currentState = "not_friends";
                                                    buttonFriendRequest.setText("Send friend request");

                                                    Toast.makeText(ProfileActivity.this,"REQUEST ACCEPTED",Toast.LENGTH_LONG).show();

                                                }
                                            });

                                }
                            });


                }

            }
        });
    }
}
