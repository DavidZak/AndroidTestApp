package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.example.mradmin.androidtestapp.TimeSinceAgo;
import com.example.mradmin.androidtestapp.activities.MessagingActivity;
import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.Dialogue;
import com.example.mradmin.androidtestapp.entities.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/*public class DialoguesFragment extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dialogues);

        ContactAdapter adapter = new ContactAdapter();

        // получаем экземпляр элемента ListView
        ListView listView = (ListView)findViewById(R.id.contactsListView);

        listView.setAdapter(adapter);
    }

    class ContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.dialogue_row_layout, null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewContactImage);
            TextView textViewName = (TextView) convertView.findViewById(R.id.textViewContactName);
            TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
            TextView textViewMessageTime = (TextView) convertView.findViewById(R.id.textViewMessageTime);

            return convertView;
        }
    }
}*/

public class DialoguesFragment extends Fragment {

    RecyclerView listView;

    private FirebaseAuth mAuth;
    private DatabaseReference messagesDB;
    private DatabaseReference usersDB;
    private String curUserId;

    private String title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialogues, container, false);

        mAuth = FirebaseAuth.getInstance();
        curUserId = mAuth.getCurrentUser().getUid();
        messagesDB = FirebaseDatabase.getInstance().getReference().child("Messages");
        messagesDB.keepSynced(true);
        usersDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();
        usersDB.keepSynced(true);

        listView = (RecyclerView) rootView.findViewById(R.id.dialoguesListView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Dialogue, DialoguesFragment.DialogueViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Dialogue, DialoguesFragment.DialogueViewHolder>(
                Dialogue.class,
                R.layout.dialogue_row_layout,
                DialoguesFragment.DialogueViewHolder.class,
                usersDB
        ) {
            @Override
            protected void populateViewHolder(final DialoguesFragment.DialogueViewHolder viewHolder, final Dialogue model, int position) {

                viewHolder.setName(model.getName());
                //viewHolder.setUserStatus(model.getStatus());
                viewHolder.setUserImage(model.getImage(), getContext()); // model.getThumbImage()

                System.out.println(model.getImage() + "--------------------------" + model.getThumbImage());

                //to profile activity
                final String userId = getRef(position).getKey();

                System.out.println("------------------------------------------------------------------------" + userId);

                messagesDB.child(curUserId).child(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String time = dataSnapshot.child("time").getValue().toString();
                        String lastMessage = dataSnapshot.child("message").getValue().toString();

                        viewHolder.setDialogueMessageAndTime(lastMessage, time);

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
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent messagingIntent = new Intent(getActivity(), MessagingActivity.class);
                        messagingIntent.putExtra("user_id", userId);
                        title = model.getName();
                        messagingIntent.putExtra("title", title);
                        startActivity(messagingIntent);

                    }
                });

            }
        };

        listView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class DialogueViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public DialogueViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }


        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.textViewDialogueName);
            userNameView.setText(name);
        }

        public void setDialogueMessageAndTime(String message, String time) {

            TextView userMessageView = (TextView) mView.findViewById(R.id.textViewMessage);

            TextView messageTime = (TextView) mView.findViewById(R.id.textViewMessageTime);

            userMessageView.setText(message);

            TimeSinceAgo timeSinceAgo = new TimeSinceAgo();
            long lastTime = Long.parseLong(time);
            String lastSeen = timeSinceAgo.getTimeAgo(lastTime);

            messageTime.setText(lastSeen);

        }

        public void setUserImage(String image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.imageViewDialogueImage);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.ic_launcher).into(userImageView);

        }
    }
}