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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.TimeSinceAgo;
import com.example.mradmin.androidtestapp.activities.EditPostActivity;
import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.Friend;
import com.example.mradmin.androidtestapp.entities.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogFragment extends Fragment {

    private RecyclerView listView;

    private FirebaseAuth mAuth;
    private DatabaseReference blogDB;
    private DatabaseReference usersDB;

    private ImageButton buttonCreatePost;

    private String currentUserId;

    private String title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blog, container, false);


        listView = (RecyclerView) rootView.findViewById(R.id.blogListView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mAuth = ((FirebaseApplication) getActivity().getApplication()).getFirebaseAuth();
        currentUserId = mAuth.getCurrentUser().getUid();
        blogDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseBlogDatabase().child(currentUserId);
        blogDB.keepSynced(true);
        usersDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();
        usersDB.keepSynced(true);

        buttonCreatePost = (ImageButton) rootView.findViewById(R.id.blogButtonNewPost);
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map map = new HashMap();
                map.put("title", "title");
                map.put("description", "desc");

                blogDB.child(currentUserId).setValue(map);
                //startActivity(new Intent(getActivity(), EditPostActivity.class));

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.blog_row_layout,
                PostViewHolder.class,
                blogDB
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position) {

                //viewHolder.setDate(model.getDate());
                viewHolder.setName(model.getTitle());
                viewHolder.setDescription(model.getDescription());

                //final String listUserId = getRef(position).getKey();

                usersDB.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setUserImage(userThumb, getContext());

                        /*viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", listUserId);
                                //profileIntent.putExtra("title", title);
                                startActivity(profileIntent);

                            }
                        });*/

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        listView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        /*public void setDate(String date) {

            TextView dateTextView = (TextView) mView.findViewById(R.id.textViewFriendLastTime);
            dateTextView.setText("Last seen " + date);

        }*/

        public void setName(String name) {

            TextView nameTextView = (TextView) mView.findViewById(R.id.textViewPostName);

            nameTextView.setText(name);

        }

        public void setDescription(String description) {

            TextView descriptionView = (TextView) mView.findViewById(R.id.blogTextViewDescription);

            descriptionView.setText(description);

        }

        public void setUserImage(String image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.blogImageViewContactImage);

            Picasso.with(context).load(image).placeholder(R.mipmap.ic_launcher).into(userImageView);

        }

    }

}
