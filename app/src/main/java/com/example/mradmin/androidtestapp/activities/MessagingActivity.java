package com.example.mradmin.androidtestapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mradmin.androidtestapp.R;

/**
 * Created by mrAdmin on 07.08.2017.
 */

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        String title = getIntent().getStringExtra("title").toString(); // Now, message has Drawer title
        setTitle(title);
    }
}
