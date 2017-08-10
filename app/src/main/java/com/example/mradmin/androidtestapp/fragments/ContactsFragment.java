package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.activities.MessagingActivity;

/**
 * Created by mrAdmin on 10.08.2017.
 */

public class ContactsFragment extends Fragment {


    private String title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.contactsListView);

        ContactAdapter contactAdapter = new ContactAdapter(getActivity(),
                R.layout.contact_row_layout);

        listView.setAdapter(contactAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MessagingActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        return rootView;
    }

    class ContactAdapter extends ArrayAdapter<String> {

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

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewContactImage);
            TextView textViewName = (TextView) convertView.findViewById(R.id.textViewContactName);
            TextView textViewMessageTime = (TextView) convertView.findViewById(R.id.textViewContactMessageTime);

            title = textViewName.getText().toString();

            return convertView;
        }
    }
}
