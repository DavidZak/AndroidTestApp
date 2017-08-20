package com.example.mradmin.androidtestapp;

import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

/**
 * Created by mrAdmin on 18.08.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> messageList;
    private FirebaseAuth mAuth;
    private DatabaseReference userDB;

    public MessageAdapter(List<Message> messageList) {

        this.messageList = messageList;

        mAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       // View v = LayoutInflater.from(parent.getContext())
        //        .inflate(R.layout.message_row_layout, parent, false);

        //return new MessageViewHolder(v);

        View v = null;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_row_layout, parent, false);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_row_layout_other, parent, false);
        }

        return new MessageViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {

        String currentUserId = mAuth.getCurrentUser().getUid();

        Message message = messageList.get(position);

        String fromUser = message.getFrom();

        if (fromUser.equals(currentUserId)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        String currentUserId = mAuth.getCurrentUser().getUid();

        Message message = messageList.get(position);

        String fromUser = message.getFrom();

        if (fromUser.equals(currentUserId)){

            userDB.child(currentUserId).child("thumb_image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String image = dataSnapshot.getValue().toString();

                    Picasso.with(holder.itemView.getContext()).load(image).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //holder.messageText.setBackgroundResource(R.drawable.message_bubble_view_other);//Color.parseColor("#83CCCD"));
            //holder.messageText.setTextColor(Color.WHITE);


        } else {

            userDB.child(fromUser).child("thumb_image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String image = dataSnapshot.getValue().toString();

                    Picasso.with(holder.itemView.getContext()).load(image).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //holder.messageText.setBackgroundResource(R.drawable.message_bubble_view);// Color.parseColor("#F8D255"));
            //holder.messageText.setTextColor(Color.WHITE);

        }

        holder.messageText.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView)itemView.findViewById(R.id.messageTextView);
            profileImage = (CircleImageView)itemView.findViewById(R.id.imageViewContactImage);

        }
    }
}
