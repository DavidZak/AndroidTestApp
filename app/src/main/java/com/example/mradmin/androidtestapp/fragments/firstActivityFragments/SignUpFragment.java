package com.example.mradmin.androidtestapp.fragments.firstActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.InputValidation;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.db.DBHelper;
import com.example.mradmin.androidtestapp.entities.User;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by mrAdmin on 08.08.2017.
 */

public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;

    private Button buttonSignUp;

    private InputValidation inputValidation;
    //private DBHelper databaseHelper;

    private User user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        inputValidation = new InputValidation(getActivity());
        //databaseHelper = new DBHelper(getActivity());
        user = new User();

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view_sign_up);

        textInputLayoutName = (TextInputLayout) view.findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) view.findViewById(R.id.textInputLayoutConfirmPassword);

        textInputEditTextName = (TextInputEditText) view.findViewById(R.id.textInputEditTextName);
        textInputEditTextEmail = (TextInputEditText) view.findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) view.findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = (TextInputEditText) view.findViewById(R.id.textInputEditTextConfirmPassword);

        firebaseAuth = ((FirebaseApplication)getActivity().getApplication()).getFirebaseAuth();

        buttonSignUp = (Button) view.findViewById(R.id.loginButtonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("click");

                postDataToSQLite();
            }
        });

        return view;
    }

    private void postDataToSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }

        //if (!databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim())) {
        //if (((FirebaseApplication)getActivity().getApplication()).checkUserLogin(getContext())) {

            ((FirebaseApplication) getActivity().getApplication()).addNewUser(getContext(), textInputEditTextEmail.getText().toString(), textInputEditTextPassword.getText().toString());

        //    user.setName(textInputEditTextName.getText().toString().trim());
        //    user.setEmail(textInputEditTextEmail.getText().toString().trim());
        //    user.setPassword(textInputEditTextPassword.getText().toString().trim());

            //databaseHelper.addUser(user);

            // Snack Bar to show success message that record saved successfully
        //    Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();
        //} else {
            // Snack Bar to show error message that record already exists
        //    Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        //}
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextConfirmPassword.setText(null);
    }
}
