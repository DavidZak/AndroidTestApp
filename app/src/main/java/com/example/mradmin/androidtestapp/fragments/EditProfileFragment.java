package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment {

    DatabaseReference userDB;

    ImageView imageView;
    TextView textViewName;
    EditText editTextStatus;
    Button saveButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        imageView = (ImageView)view.findViewById(R.id.edit_profile_image);
        textViewName = (TextView) view.findViewById(R.id.text_view_name_profile);
        editTextStatus = (EditText) view.findViewById(R.id.edit_profile_status);
        saveButton = (Button)view.findViewById(R.id.save_profile_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("saving profile");

                String curUserUID = ((FirebaseApplication)getActivity().getApplication()).getFirebaseAuth().getCurrentUser().getUid();

                userDB.child(curUserUID).child("status").setValue(editTextStatus.getText().toString());
            }
        });

        FirebaseUser curUser = ((FirebaseApplication)getActivity().getApplication()).getFirebaseAuth().getCurrentUser();

        String curuserUID = curUser.getUid();

        userDB = ((FirebaseApplication)getActivity().getApplication()).getFirebaseDatabase();
        DatabaseReference currentUser = userDB.child(curuserUID);

        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                textViewName.setText(name);
                editTextStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
