package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.TimeSinceAgo;
import com.example.mradmin.androidtestapp.activities.EditPostActivity;
import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.Friend;
import com.example.mradmin.androidtestapp.entities.Message;
import com.example.mradmin.androidtestapp.entities.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private DatabaseReference rootRef;

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

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = ((FirebaseApplication) getActivity().getApplication()).getFirebaseAuth();
        currentUserId = mAuth.getCurrentUser().getUid();
        blogDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseBlogDatabase();
        blogDB.keepSynced(true);
        usersDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();
        usersDB.keepSynced(true);

        buttonCreatePost = (ImageButton) rootView.findViewById(R.id.blogButtonNewPost);
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), EditPostActivity.class));

            }
        });

        Spinner spinner = (Spinner) rootView.findViewById(R.id.blogSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blog_variants, R.layout.custom_spinner_layout);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, int position) {

                viewHolder.setName(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setTime(model.getTime());                     //------------------ Need to work

                viewHolder.setUserName(model.getUserName());
                viewHolder.setUserImage(model.getUserImage(), getContext());
                /*viewHolder.setLike(model.getLikesCount());

                ImageButton buttonLike = (ImageButton) viewHolder.mView.findViewById(R.id.button_like_post);
                buttonLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int likesCount = model.getLikesCount() + 1;

                        viewHolder.setLike(likesCount);

                    }
                });*/

                /*blogDB.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                        String userID = dataSnapshot.child("user_id").getValue().toString();

                        usersDB.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String name = dataSnapshot.child("name").getValue().toString();
                                viewHolder.setUserName(name);

                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                viewHolder.setUserImage(userThumb, getContext());

                        *//*viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", listUserId);
                                //profileIntent.putExtra("title", title);
                                startActivity(profileIntent);

                            }
                        });*//*


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
                });*/

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

        public void setUserName(String name) {

            TextView userName = (TextView) mView.findViewById(R.id.textViewUserName);

            userName.setText(name);

        }

        public void setTime(long time) {

            TextView timeView = (TextView) mView.findViewById(R.id.textViewPostTime);

            TimeSinceAgo timeSinceAgo = new TimeSinceAgo();
            //long lastTime = Long.parseLong(time);
            String lastSeen = timeSinceAgo.getTimeAgo(time);

            timeView.setText(lastSeen);
        }

        /*public void setLike(int like){

            TextView likeText = (TextView) mView.findViewById(R.id.text_view_post_likes_count);

            likeText.setText(String.valueOf(like));

        }*/

    }

}
