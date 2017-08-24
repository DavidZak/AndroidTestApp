package com.example.mradmin.androidtestapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.activities.MessagingActivity;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mrAdmin on 10.08.2017.
 */

public class ContactsFragment extends Fragment {

    RecyclerView listView;

    private DatabaseReference userDB;

    private String title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        userDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();
        userDB.keepSynced(true);

        listView = (RecyclerView) rootView.findViewById(R.id.contactsListView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.contact_row_layout,
                UserViewHolder.class,
                userDB
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
                viewHolder.setUserImage(model.getImage(), getContext()); // model.getThumbImage()

                System.out.println(model.getImage() +"--------------------------"+ model.getThumbImage());

                //to profile activity
                final String userId = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                        profileIntent.putExtra("user_id", userId);
                        title = userDB.child(userId).child("name").toString();
                        profileIntent.putExtra("title", title);
                        startActivity(profileIntent);

                    }
                });
            }
        };

        listView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }


        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.textViewContactName);
            userNameView.setText(name);
        }

        public void setUserStatus(String status) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.textViewContactMessageStatus);
            userStatusView.setText(status);

        }

        public void setUserImage(String image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.imageViewContactImage);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.ic_launcher).into(userImageView);

        }
    }
}
