package com.example.mradmin.androidtestapp;

import android.app.Application;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.entities.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mrAdmin on 18.08.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Message> messageList) {

        this.messageList = messageList;

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row_layout, parent, false);

        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        String currentUserId = mAuth.getCurrentUser().getUid();

        Message message = messageList.get(position);

        String fromUser = message.getFrom();

        if (fromUser.equals(currentUserId)){

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);

        } else {

            holder.messageText.setBackgroundColor(Color.BLACK);
            holder.messageText.setTextColor(Color.WHITE);

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
