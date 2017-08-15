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
import com.example.mradmin.androidtestapp.entities.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
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

        listView = (RecyclerView) rootView.findViewById(R.id.contactsListView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //ContactAdapter contactAdapter = new ContactAdapter(getActivity(),
        //        R.layout.contact_row_layout);

        //listView.setAdapter(contactAdapter);

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

            Picasso.with(context).load(image).placeholder(R.mipmap.ic_launcher).into(userImageView);

        }
    }

    /*class ContactAdapter extends ArrayAdapter<String> {

        private Context mContext;

        @Override
        public int getCount() {
            return 5;
        }

        public ContactAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_row_layout, parent, false);
            }

            CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.imageViewContactImage);
            TextView textViewName = (TextView) convertView.findViewById(R.id.textViewContactName);
            TextView textViewMessageStatus = (TextView) convertView.findViewById(R.id.textViewContactMessageStatus);

            title = textViewName.getText().toString();

            return convertView;
        }
    }*/
}
