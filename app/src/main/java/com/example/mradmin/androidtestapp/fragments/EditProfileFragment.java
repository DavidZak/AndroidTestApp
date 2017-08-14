package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    private static final int GALLERY_PICK = 1;
    DatabaseReference userDB;
    StorageReference imageStorage;

    FirebaseUser curUser;
    DatabaseReference currentDBUser;

    TextView textViewImageName;
    ImageButton imageButtonSelectImage;

    CircleImageView imageView;
    TextView textViewName;
    EditText editTextStatus;
    Button saveButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        textViewImageName = (TextView) view.findViewById(R.id.image_name_text_view);
        imageButtonSelectImage = (ImageButton) view.findViewById(R.id.button_choose_image);
        imageButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity());*/
            }
        });

        imageView = (CircleImageView) view.findViewById(R.id.edit_profile_image);
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

                    }
                });
            }
        });

        curUser = ((FirebaseApplication) getActivity().getApplication()).getFirebaseAuth().getCurrentUser();

        String curuserUID = curUser.getUid();

        userDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();
        currentDBUser = userDB.child(curuserUID);

        imageStorage = ((FirebaseApplication)getActivity().getApplication()).getFirebaseStorage();

        currentDBUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                textViewImageName.setText(image);
                textViewName.setText(name);
                editTextStatus.setText(status);
                Picasso.with(getActivity()).load(image).into(imageView);
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

            //CropImage.activity(imageUri)
            //  .setAspectRatio(1, 1)
            //  .start(this.getActivity());
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);

            //Toast.makeText(getContext(),imageUri,Toast.LENGTH_LONG).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                final String currentUserId = curUser.getUid();

                StorageReference filepath = imageStorage.child(currentUserId + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            String downloadUrl = task.getResult().getDownloadUrl().toString();
                            currentDBUser.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getActivity(), "uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            Toast.makeText(getActivity(), "working", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(getActivity(), "not working", Toast.LENGTH_SHORT).show();

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
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
