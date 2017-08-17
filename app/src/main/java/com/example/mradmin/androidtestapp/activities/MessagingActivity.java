package com.example.mradmin.androidtestapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.InputValidation;
import com.example.mradmin.androidtestapp.MessageAdapter;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.entities.Message;
import com.example.mradmin.androidtestapp.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrAdmin on 07.08.2017.
 */

public class MessagingActivity extends AppCompatActivity {

    private InputValidation inputValidation;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserId;
    private String userId;

    private RecyclerView messageView;

    TextInputEditText editTextMessage;

    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userId = getIntent().getStringExtra("user_id");

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
