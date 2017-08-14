package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    private static final int GALLERY_PICK = 1;
    DatabaseReference userDB;
    StorageReference imageStorage;

    FirebaseUser curUser;
    DatabaseReference currentDBUser;

    CircleImageView imageView;
    TextView textViewName;
    EditText editTextStatus;
    Button saveButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        imageView = (CircleImageView) view.findViewById(R.id.edit_profile_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
        textViewName = (TextView) view.findViewById(R.id.text_view_name_profile);
        editTextStatus = (EditText) view.findViewById(R.id.edit_profile_status);
        saveButton = (Button) view.findViewById(R.id.save_profile_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("saving profile");

                String curUserUID = ((FirebaseApplication) getActivity().getApplication()).getFirebaseAuth().getCurrentUser().getUid();

                userDB.child(curUserUID).child("status").setValue(editTextStatus.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "changes saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        curUser = ((FirebaseApplication) getActivity().getApplication()).getFirebaseAuth().getCurrentUser();

        String curuserUID = curUser.getUid();

        userDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();
        currentDBUser = userDB.child(curuserUID);

        imageStorage = ((FirebaseApplication) getActivity().getApplication()).getFirebaseStorage();

        currentDBUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                textViewName.setText(name);
                editTextStatus.setText(status);

                if (!image.equals("default")) {

                    Picasso.with(getActivity()).load(image).placeholder(R.mipmap.ic_launcher).into(imageView);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                final String currentUserId = curUser.getUid();

                Bitmap thumb_bitmap = new Compressor(getContext())
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = imageStorage.child(currentUserId + ".jpg");
                final StorageReference thumb_filepath = imageStorage.child("thumbs").child(currentUserId + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    String thumb_downloadUrl = task.getResult().getDownloadUrl().toString();

                                    if (task.isSuccessful()) {

                                        Map updateHashMap = new HashMap();
                                        updateHashMap.put("image", downloadUrl);
                                        updateHashMap.put("thumb_image", thumb_downloadUrl);

                                        currentDBUser.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(getActivity(), "uploaded", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                    } else {

                                        Toast.makeText(getActivity(), "uploading failed", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        } else {

                            Toast.makeText(getActivity(), "something wrong", Toast.LENGTH_SHORT).show();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
