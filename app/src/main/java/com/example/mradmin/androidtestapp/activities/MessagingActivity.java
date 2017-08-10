package com.example.mradmin.androidtestapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.entities.User;

/**
 * Created by mrAdmin on 07.08.2017.
 */

public class MessagingActivity extends AppCompatActivity {

    boolean messageFieldNotEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        String title = getIntent().getStringExtra("title").toString(); // Now, message has Drawer title
        setTitle(title);

        final EditText editTextMessage = (EditText) findViewById(R.id.messageTextInput);

        final ImageButton buttonSendMessage = (ImageButton) findViewById(R.id.button_send_message);
        final ImageButton buttonAttachFile = (ImageButton) findViewById(R.id.button_attach_file);

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("send message");
            }
        });

        buttonAttachFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("attach file");

            }
        });

        editTextMessage.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    messageFieldNotEmpty = true;
                    buttonSendMessage.setEnabled(true);
                    buttonAttachFile.setEnabled(false);

                } else {
                    messageFieldNotEmpty = false;
                    buttonSendMessage.setEnabled(false);
                    buttonAttachFile.setEnabled(true);
                }
            }
        });
    }
}
