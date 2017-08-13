package com.example.mradmin.androidtestapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

    private String title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialogues, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.dialoguesListView);

        DialogueAdapter dialogueAdapter = new DialogueAdapter(getActivity(),
                R.layout.dialogue_row_layout);

        listView.setAdapter(dialogueAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MessagingActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        return rootView;
    }

    class DialogueAdapter extends ArrayAdapter<String> {

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

            TextView imageView = (TextView) convertView.findViewById(R.id.imageViewDialogueImage);
            TextView textViewName = (TextView) convertView.findViewById(R.id.textViewDialogueName);
            TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
            TextView textViewMessageTime = (TextView) convertView.findViewById(R.id.textViewMessageTime);

            title = textViewName.getText().toString();

            return convertView;
        }
    }
}