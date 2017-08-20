package com.example.mradmin.androidtestapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    TextView imageViewPostImage;
    TextInputEditText textInputTitle;
    TextInputEditText textInputDescription;
    Button buttonPublishPost;

    DatabaseReference rootRef;
    DatabaseReference blogDB;
    DatabaseReference userDB;
    StorageReference imageStorage;

    FirebaseUser curUser;
    DatabaseReference currentDBUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        setTitle("New Post");

        imageViewPostImage = (TextView) findViewById(R.id.textViewSelectPostImage);
        textInputTitle = (TextInputEditText) findViewById(R.id.textViewPostName);
        textInputDescription = (TextInputEditText) findViewById(R.id.blogTextViewPostDescription);
        buttonPublishPost = (Button) findViewById(R.id.buttonSavePost);

        imageViewPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

        rootRef = FirebaseDatabase.getInstance().getReference();

        curUser = ((FirebaseApplication) getApplication()).getFirebaseAuth().getCurrentUser();

        String curuserUID = curUser.getUid();

        blogDB = ((FirebaseApplication) getApplication()).getFirebaseBlogDatabase();
        userDB = ((FirebaseApplication) getApplication()).getFirebaseDatabase();
        userDB.keepSynced(true);

        currentDBUser = userDB.child(curuserUID);

        imageStorage = ((FirebaseApplication) getApplication()).getFirebaseStorage();

        buttonPublishPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("publishing post");

                String curUserUID = ((FirebaseApplication) getApplication()).getFirebaseAuth().getCurrentUser().getUid();

                String currentUserRef = "Blog";

                DatabaseReference postPush = rootRef.child("Blog").push();

                String pushId = postPush.getKey();

                Map valueMap = new HashMap();
                valueMap.put("title", textInputTitle.getText().toString());
                valueMap.put("description", textInputDescription.getText().toString());
                valueMap.put("time", ServerValue.TIMESTAMP);
                valueMap.put("user_id", curUserUID);
                valueMap.put("likes_count", 0);

                Map postMap = new HashMap();
                postMap.put(currentUserRef + "/" + pushId, valueMap);

                rootRef.updateChildren(postMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError != null) {

                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                        }

                    }
                });
            }
        });

    }

}
