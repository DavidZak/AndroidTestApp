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

import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.R;

/**
 * Created by mrAdmin on 08.08.2017.
 */

public class SignUpFragment extends Fragment {

    boolean nameNotEmpty = false;
    boolean surnameNotEmpty = false;
    boolean mailNotEmpty = false;
    boolean passwordNotEmpty = false;
    boolean passwordCopyNotEmpty = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        final EditText editTextName = (EditText) view.findViewById(R.id.messageEditTextNameSignUp);
        final EditText editTextSurname = (EditText) view.findViewById(R.id.messageEditTextSurNameSignUp);
        final EditText editTextMail = (EditText) view.findViewById(R.id.messageEditTextMailSignUp);
        final EditText editTextPassword = (EditText) view.findViewById(R.id.messageEditTextPasswordSignUp);
        final EditText editTextPasswordCopy = (EditText) view.findViewById(R.id.messageEditTextPasswordCopySignUp);

        final Button button = (Button) view.findViewById(R.id.loginButtonSignUp);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("click");
                startActivity(new Intent(getContext(), NavigationActivity.class));
            }
        });

        editTextName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    nameNotEmpty = true;
                    if (passwordNotEmpty && passwordCopyNotEmpty && surnameNotEmpty && mailNotEmpty) {
                        button.setEnabled(true);
                    }
                } else {
                    nameNotEmpty = false;
                    button.setEnabled(false);
                }
            }
        });

        editTextSurname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    surnameNotEmpty = true;
                    if (passwordNotEmpty && passwordCopyNotEmpty && nameNotEmpty && mailNotEmpty) {
                        button.setEnabled(true);
                    }
                } else {
                    surnameNotEmpty = false;
                    button.setEnabled(false);
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
                    if (passwordNotEmpty && passwordCopyNotEmpty && nameNotEmpty && surnameNotEmpty) {
                        button.setEnabled(true);
                    }
                } else {
                    mailNotEmpty = false;
                    button.setEnabled(false);
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
                    if (passwordNotEmpty && passwordCopyNotEmpty && nameNotEmpty && surnameNotEmpty) {
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
                    if (mailNotEmpty && passwordCopyNotEmpty && nameNotEmpty && surnameNotEmpty) {
                        button.setEnabled(true);
                    }
                } else {
                    passwordNotEmpty = false;
                    button.setEnabled(false);
                }
            }
        });

        editTextPasswordCopy.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.length() != 0) {
                    passwordCopyNotEmpty = true;
                    if (mailNotEmpty && passwordNotEmpty) {
                        button.setEnabled(true);
                    }
                } else {
                    passwordCopyNotEmpty = false;
                    button.setEnabled(false);
                }
            }
        });

        return view;
    }
}
