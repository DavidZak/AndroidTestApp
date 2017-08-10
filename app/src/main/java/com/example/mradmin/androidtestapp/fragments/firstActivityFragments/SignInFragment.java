package com.example.mradmin.androidtestapp.fragments.firstActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mradmin.androidtestapp.activities.MessagingActivity;
import com.example.mradmin.androidtestapp.db.DBHelper;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.db.UserDBHelper;
import com.example.mradmin.androidtestapp.entities.User;

/**
 * Created by mrAdmin on 08.08.2017.
 */

public class SignInFragment extends Fragment {

    UserDBHelper dbHelper;

    boolean mailNotEmpty = false;
    boolean passwordNotEmpty = false;

    private String userName = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        dbHelper = new UserDBHelper(this.getContext());
        dbHelper.insertUser("davo zak","dav","zak");

        final EditText editTextMail = (EditText) view.findViewById(R.id.messageEditText);
        final EditText editTextPassword = (EditText) view.findViewById(R.id.messageEditTextPassword);

        final Button button = (Button) view.findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("click");

                User user = dbHelper.getUserByEmail(editTextMail.getText().toString());
                if (user!=null) {
                    if (user.getPassword().equals(editTextPassword.getText().toString())) {
                        System.out.println("ok");

                        userName = user.getFullName();

                        Intent intent = new Intent(getContext(), NavigationActivity.class);
                        intent.putExtra("username", userName);
                        startActivity(intent);

                        //startActivity(new Intent(getContext(), NavigationActivity.class));
                    } else {
                        System.out.println("password wrong");
                    }
                } else {
                    System.out.println("not ok");
                }

            }
        });

        editTextMail.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    mailNotEmpty = true;
                    if (passwordNotEmpty) {
                        button.setEnabled(true);
                    }
                } else {
                    mailNotEmpty = false;
                    button.setEnabled(false);
                }
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.length() != 0) {
                    passwordNotEmpty = true;
                    if (mailNotEmpty) {
                        button.setEnabled(true);
                    }
                } else {
                    passwordNotEmpty = false;
                    button.setEnabled(false);
                }
            }
        });

        return view;
    }
}
