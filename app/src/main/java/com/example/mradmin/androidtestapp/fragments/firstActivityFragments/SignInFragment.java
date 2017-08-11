package com.example.mradmin.androidtestapp.fragments.firstActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.InputValidation;
import com.example.mradmin.androidtestapp.activities.FirstActivity;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.db.DBHelper;
import com.example.mradmin.androidtestapp.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

/**
 * Created by mrAdmin on 08.08.2017.
 */

public class SignInFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    private InputValidation inputValidation;
    //private DBHelper databaseHelper;

    private Button button;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nested_scroll_view_sign_in);

        textInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.emailTextView);
        textInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.passwordTextView);

        editTextEmail = (TextInputEditText) view.findViewById(R.id.messageEditText);
        editTextPassword = (TextInputEditText) view.findViewById(R.id.messageEditTextPassword);

        inputValidation = new InputValidation(getContext());
        //databaseHelper = new DBHelper(getContext());

        firebaseAuth = ((FirebaseApplication)getActivity().getApplication()).getFirebaseAuth();
        ((FirebaseApplication)getActivity().getApplication()).checkUserLogin(getActivity());

        button = (Button) view.findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("click");

                verifyFromSQLite();

            }
        });

        return view;
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {
        if (!inputValidation.isInputEditTextFilled(editTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(editTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(editTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }

        //if (databaseHelper.checkUser(editTextEmail.getText().toString().trim()
        //        , editTextPassword.getText().toString().trim())) {

        //
            ((FirebaseApplication) getActivity().getApplication()).loginUser(getContext(), editTextEmail.getText().toString(), editTextPassword.getText().toString());


            //Intent accountsIntent = new Intent(getActivity(), NavigationActivity.class);
            //accountsIntent.putExtra("username", editTextEmail.getText().toString().trim());
            emptyInputEditText();
            //startActivity(accountsIntent);
        //} else {
            // Snack Bar to show success message that record is wrong
          //  Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        //}
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }
}
