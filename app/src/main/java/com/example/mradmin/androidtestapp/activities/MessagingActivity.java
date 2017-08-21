package com.example.mradmin.androidtestapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.InputValidation;
import com.example.mradmin.androidtestapp.MessageAdapter;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.TimeSinceAgo;
import com.example.mradmin.androidtestapp.entities.Message;
import com.example.mradmin.androidtestapp.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mrAdmin on 07.08.2017.
 */

public class MessagingActivity extends AppCompatActivity {

    private InputValidation inputValidation;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserId;
    private String userId;

    private DatabaseReference userDB;

    private DatabaseReference notificationDB;

    private RecyclerView messageView;

    TextInputEditText editTextMessage;

    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private Toolbar messagingToolbar;

    private TextView profileName;
    private TextView onlineStatus;
    private CircleImageView imageViewAvatar;
    public String title = "";
    private ImageView onlineImageViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        //String title = getIntent().getStringExtra("title").toString(); // Now, message has Drawer title

        messagingToolbar = (Toolbar) findViewById(R.id.messaging_toolbar);
        setSupportActionBar(messagingToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);

        profileName = (TextView) findViewById(R.id.profile_toolbar_name);
        onlineStatus = (TextView) findViewById(R.id.profile_toolbar_online_status);
        imageViewAvatar = (CircleImageView) findViewById(R.id.toolbar_avatar_image);
        onlineImageViewStatus = (ImageView) findViewById(R.id.imageViewOnlineStatus);
        //profileName.setText(title);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.messaging_toolbar_layout, null);

        actionBar.setCustomView(actionBarView);

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userId = getIntent().getStringExtra("user_id");

        notificationDB = ((FirebaseApplication)getApplication()).getNotificationsDatabase();

        userDB = ((FirebaseApplication)getApplication()).getFirebaseDatabase();
        userDB.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();

                if (online.equals("true")) {

                    onlineImageViewStatus.setVisibility(View.VISIBLE);

                    onlineStatus.setText("Online");

                    //onlineStatus.setVisibility(View.INVISIBLE);

                } else {

                    onlineImageViewStatus.setVisibility(View.INVISIBLE);

                    TimeSinceAgo timeSinceAgo = new TimeSinceAgo();
                    long lastTime = Long.parseLong(online);
                    String lastSeen = timeSinceAgo.getTimeAgo(lastTime);

                    onlineStatus.setVisibility(View.VISIBLE);

                    onlineStatus.setText("Last seen " + lastSeen);
                }

                profileName.setText(displayName);   //-------------------------------------------for toolbar title
                Picasso.with(MessagingActivity.this).load(image).placeholder(R.mipmap.ic_launcher).into(imageViewAvatar);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println(userId + " ---------------------------------------------------------- ");

        inputValidation = new InputValidation(MessagingActivity.this);

        String title = getIntent().getStringExtra("title").toString(); // Now, message has Drawer title
        setTitle(title);

        editTextMessage = (TextInputEditText) findViewById(R.id.messageTextInput);

        final ImageButton buttonSendMessage = (ImageButton) findViewById(R.id.button_send_message);
        final ImageButton buttonAttachFile = (ImageButton) findViewById(R.id.button_attach_file);

        messageAdapter = new MessageAdapter(messageList);

        messageView = (RecyclerView)findViewById(R.id.messagingRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);

        messageView.setHasFixedSize(true);
        messageView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);

        messageView.setAdapter(messageAdapter);
        
        loadMessages();

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String message = editTextMessage.getText().toString();

                String currentUserRef = "Messages/" + currentUserId + "/" + userId;
                String messagingUserRef = "Messages/" + userId + "/" + currentUserId;

                DatabaseReference userMessagePush = rootRef.child("Messages")
                        .child(currentUserId).child(userId).push();

                String pushId = userMessagePush.getKey();

                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", currentUserId);

                Map messageUserMap = new HashMap();
                messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
                messageUserMap.put(messagingUserRef + "/" + pushId, messageMap);

                editTextMessage.setText("");

                rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError != null) {

                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                        }

                    }
                });

                messageView.setVerticalScrollbarPosition(messageList.size() - 1);

                //------------- NEED CHANGE (FOR MESSAGE NOTIFICATIONS)

                HashMap<String, String> notificationData = new HashMap<String, String>();
                notificationData.put("from", currentUserId);
                notificationData.put("type", "request");

                notificationDB.child(userId).push().setValue(notificationData);

            }
        });

        buttonAttachFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                System.out.println("attach file");

            }
        });

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (inputValidation.isInputEditTextFilled(editTextMessage, null, "azaz")){

                    buttonSendMessage.setEnabled(true);

                } else

                    buttonSendMessage.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadMessages() {

        rootRef.child("Messages").keepSynced(true);
        rootRef.child("Messages").child(currentUserId).child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);

                messageList.add(message);
                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
