package com.example.mradmin.androidtestapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.fragments.ContactsFragment;
import com.example.mradmin.androidtestapp.fragments.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;

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

    private DatabaseReference notificationsDatabase;

    private FirebaseUser currentUser;

    private String userId;

    private String currentState;

    private Toolbar profileToolbar;

    private TextView profileName;
    private ImageButton buttonSendMessage;

    public String title = "";

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String title = getIntent().getStringExtra("user_id").toString(); // Now, message has Drawer title
        //setTitle(title);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshProfile);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorBackgroundLight, R.color.colorButtonEnabled, R.color.colorIcon);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);

                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", title);
                startActivity(intent);
            }
        });



        profileToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(profileToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);

        profileName = (TextView) findViewById(R.id.profile_toolbar_name);
        //profileName.setText(title);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.profile_toolbar_layout, null);

        actionBar.setCustomView(actionBarView);

        userId = getIntent().getExtras().getString("user_id");
        userDB = ((FirebaseApplication)getApplication()).getFirebaseDatabase().child(userId);
        friendRequestDB = ((FirebaseApplication)getApplication()).getFirebaseFriendRequestDatabase();
        currentUser = ((FirebaseApplication)getApplication()).getFirebaseAuth().getCurrentUser();
        friendsDatabase = ((FirebaseApplication)getApplication()).getFirebaseFriendsDatabase();
        notificationsDatabase = ((FirebaseApplication)getApplication()).getNotificationsDatabase();

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

                final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
                pd.setTitle("Declining");
                pd.setMessage("Please wait");
                pd.show();

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

                                pd.dismiss();
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

                profileName.setText(displayName);   //-------------------------------------------for toolbar title

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

                final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
                pd.setTitle("Operation in progress");
                pd.setMessage("Please wait");
                pd.show();

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

                                        HashMap<String, String> notificationData = new HashMap<String, String>();
                                        notificationData.put("from", currentUser.getUid());
                                        notificationData.put("type", "request");

                                        notificationsDatabase.child(userId).push().setValue(notificationData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        currentState = "req_sent";
                                                        buttonFriendRequest.setText("Cancel friend request");

                                                    }
                                                });


                                        Toast.makeText(ProfileActivity.this,"REQUEST SENT",Toast.LENGTH_LONG).show();

                                    }
                                });

                            } else {

                                Toast.makeText(ProfileActivity.this,"FAILED SENDING REQUEST",Toast.LENGTH_LONG).show();
                            }

                            buttonFriendRequest.setEnabled(true);
                            pd.dismiss();

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

                                            Toast.makeText(ProfileActivity.this,"REQUEST CANCELED",Toast.LENGTH_LONG).show();

                                        }
                                    });
                            pd.dismiss();
                        }
                    });

                }

                // --------------- REQ RECEIVED STATE

                if (currentState.equals("req_received")){

                    buttonDeclineFriendRequest.setVisibility(View.INVISIBLE);

                    final String currentDate = ServerValue.TIMESTAMP.toString();       //need min sdk version 24

                    friendsDatabase.child(currentUser.getUid()).child(userId).child("date").setValue(currentDate)//(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendsDatabase.child(userId).child(currentUser.getUid()).child("date").setValue(currentDate)//(currentDate)
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
                                                                                    buttonDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                                                    Toast.makeText(ProfileActivity.this,"REQUEST ACCEPTED",Toast.LENGTH_LONG).show();

                                                                                }
                                                                            });

                                                                }
                                                            });


                                                }
                                            });
                                    pd.dismiss();

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
                                    pd.dismiss();
                                }
                            });


                }

            }
        });

        buttonSendMessage = (ImageButton)profileToolbar.findViewById(R.id.toolbar_button_send_message);
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("--------------------------------------------------------click");

                Intent messagingIntent = new Intent(ProfileActivity.this, MessagingActivity.class);
                messagingIntent.putExtra("title", profileName.getText());
                messagingIntent.putExtra("user_id", userId);
                startActivity(messagingIntent);

            }
        });

    }
}
