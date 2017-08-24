package com.example.mradmin.androidtestapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class EditPostActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 2;

    TextView imageViewPostImage;
    ImageView postImage;
    TextInputEditText textInputTitle;
    TextInputEditText textInputDescription;
    Button buttonPublishPost;

    DatabaseReference rootRef;
    DatabaseReference blogDB;
    DatabaseReference userDB;
    StorageReference imageStorage;

    FirebaseUser curUser;
    DatabaseReference currentDBUser;

    String downloadUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        setTitle("New Post");

        imageViewPostImage = (TextView) findViewById(R.id.textViewSelectPostImage);
        postImage = (ImageView) findViewById(R.id.imageViewPostImage);
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

        imageStorage = ((FirebaseApplication) getApplication()).getBlogStorage();

        buttonPublishPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("publishing post");


                final String curUserUID = ((FirebaseApplication) getApplication()).getFirebaseAuth().getCurrentUser().getUid();

                final String currentUserRef = "Blog";

                DatabaseReference postPush = rootRef.child("Blog").push();

                final String pushId = postPush.getKey();

                userDB.child(curUserUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map valueMap = new HashMap();
                        valueMap.put("title", textInputTitle.getText().toString());
                        valueMap.put("description", textInputDescription.getText().toString());
                        valueMap.put("time", ServerValue.TIMESTAMP);
                        valueMap.put("user_id", curUserUID);
                        valueMap.put("likes_count", 0);
                        valueMap.put("post_image", downloadUrl);

                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("thumb_image").getValue().toString();

                        valueMap.put("user_name", name);
                        valueMap.put("user_image", image);

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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                postImage.setImageURI(resultUri);

                StorageReference filepath = imageStorage.child(resultUri.getLastPathSegment());

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            downloadUrl = task.getResult().getDownloadUrl().toString();
                        }
                    }
                });

            }
        }
    }
}
