package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.TimeSinceAgo;
import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.Friend;
import com.example.mradmin.androidtestapp.entities.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    private RecyclerView listView;

    private FirebaseAuth mAuth;
    private DatabaseReference friendsDB;
    private DatabaseReference usersDB;

    private String currentUserId;

    private String title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        listView = (RecyclerView) rootView.findViewById(R.id.friendsListView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mAuth = ((FirebaseApplication) getActivity().getApplication()).getFirebaseAuth();
        currentUserId = mAuth.getCurrentUser().getUid();
        friendsDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseFriendsDatabase().child(currentUserId);
        friendsDB.keepSynced(true);
        usersDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();
        usersDB.keepSynced(true);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friend, FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(
                Friend.class,
                R.layout.friend_row_layout,
                FriendViewHolder.class,
                friendsDB
        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, Friend model, int position) {

                viewHolder.setDate(model.getDate());

                final String listUserId = getRef(position).getKey();

                usersDB.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")){

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);

                        }

                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userThumb, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", listUserId);
                                profileIntent.putExtra("title", title);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        listView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {

            TextView dateTextView = (TextView) mView.findViewById(R.id.textViewFriendLastTime);
            dateTextView.setText("Last seen " + date);

        }

        public void setName(String name) {

            TextView nameTextView = (TextView) mView.findViewById(R.id.textViewFriendName);
            nameTextView.setText(name);

        }

        public void setUserImage(String image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.imageViewFriendImage);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.ic_launcher).into(userImageView);

        }

        public void setUserOnline(String onlineStatus) {

            ImageView onlineImageView = (ImageView) mView.findViewById(R.id.imageViewOnlineImage);

            TextView textViewLastSeen = (TextView) mView.findViewById(R.id.textViewFriendLastTime);

            if (onlineStatus.equals("true")) {

                onlineImageView.setVisibility(View.VISIBLE);

                textViewLastSeen.setText("Online");

                //textViewLastSeen.setVisibility(View.INVISIBLE);

            } else {

                onlineImageView.setVisibility(View.INVISIBLE);

                TimeSinceAgo timeSinceAgo = new TimeSinceAgo();
                long lastTime = Long.parseLong(onlineStatus);
                String lastSeen = timeSinceAgo.getTimeAgo(lastTime);

                textViewLastSeen.setVisibility(View.VISIBLE);

                textViewLastSeen.setText("Last seen " + lastSeen);
            }
        }


    }

}
