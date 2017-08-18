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
import com.example.mradmin.androidtestapp.activities.MessagingActivity;
import com.example.mradmin.androidtestapp.activities.ProfileActivity;
import com.example.mradmin.androidtestapp.entities.Dialogue;
import com.example.mradmin.androidtestapp.entities.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
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

    private DatabaseReference userDB;

    private String title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialogues, container, false);

        userDB = ((FirebaseApplication) getActivity().getApplication()).getFirebaseDatabase();

        listView = (RecyclerView) rootView.findViewById(R.id.dialoguesListView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //DialogueAdapter dialogueAdapter = new DialogueAdapter(getActivity(),
        //        R.layout.dialogue_row_layout);

        //listView.setAdapter(dialogueAdapter);

        //listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        Intent intent = new Intent(getContext(), MessagingActivity.class);
        //        intent.putExtra("title", title);
        //        startActivity(intent);
        //    }
        //});

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Dialogue, DialoguesFragment.DialogueViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Dialogue, DialoguesFragment.DialogueViewHolder>(
                Dialogue.class,
                R.layout.dialogue_row_layout,
                DialoguesFragment.DialogueViewHolder.class,
                userDB
        ) {
            @Override
            protected void populateViewHolder(DialoguesFragment.DialogueViewHolder viewHolder, final Dialogue model, int position) {

                viewHolder.setName(model.getName());
                //viewHolder.setUserStatus(model.getStatus());
                viewHolder.setUserImage(model.getImage(), getContext()); // model.getThumbImage()

                System.out.println(model.getImage() +"--------------------------"+ model.getThumbImage());

                //to profile activity
                final String userId = getRef(position).getKey();

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

        //public void setDialogueMessageAndTime(String message, long time) {

        //    TextView userStatusView = (TextView) mView.findViewById(R.id.textViewContactMessageStatus);
        //    userStatusView.setText(message);

        //}

        public void setUserImage(String image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.imageViewDialogueImage);

            Picasso.with(context).load(image).placeholder(R.mipmap.ic_launcher).into(userImageView);

        }
    }



    /*class DialogueAdapter extends ArrayAdapter<String> {

        private Context mContext;

        @Override
        public int getCount() {
            return 10;
        }

        public DialogueAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dialogue_row_layout, parent, false);
            }

            CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.imageViewDialogueImage);
            TextView textViewName = (TextView) convertView.findViewById(R.id.textViewDialogueName);
            TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
            TextView textViewMessageTime = (TextView) convertView.findViewById(R.id.textViewMessageTime);

            title = textViewName.getText().toString();

            return convertView;
        }
    }*/
}