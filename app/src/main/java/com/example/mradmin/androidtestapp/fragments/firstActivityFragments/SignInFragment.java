package com.example.mradmin.androidtestapp.fragments.firstActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.InputValidation;
import com.example.mradmin.androidtestapp.activities.NavigationActivity;
import com.example.mradmin.androidtestapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mrAdmin on 08.08.2017.
 */

public class SignInFragment extends Fragment {

    private GoogleApiClient googleApiClient;

    public static final int RC_SIGN_IN = 1;

    FirebaseAuth firebaseAuth;
    DatabaseReference userDB;

    private InputValidation inputValidation;
    //private DBHelper databaseHelper;

    private Button button;
    private ImageButton googleButton;

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

        firebaseAuth = ((FirebaseApplication) getActivity().getApplication()).getFirebaseAuth();
        ((FirebaseApplication) getActivity().getApplication()).checkUserLogin(getActivity());
        userDB = ((FirebaseApplication)getActivity().getApplication()).getFirebaseDatabase();

        //Google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //buttons
        button = (Button) view.findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("click");

                verifyFromSQLite();

            }
        });

        googleButton = (ImageButton) view.findViewById(R.id.button_sign_in_google);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        googleButton.setEnabled(false);  // ------------------ fix proble mwith google sign in

        return view;
    }


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
        ((FirebaseApplication) getActivity().getApplication()).loginUser(getContext(), editTextEmail.getText().toString(), editTextPassword.getText().toString());

        emptyInputEditText();
    }

    private void emptyInputEditText() {
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

    //--------------------GOOGLE SING IN-------------------------
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                System.out.println("wrong");
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (!((FirebaseApplication)getActivity().getApplication()).checkUserExistInDatabase()){

                                System.out.println("------------- not exist in db -------------");
                                ((FirebaseApplication)getActivity().getApplication()).addInfoInDatabase(acct.getDisplayName(), "default", "Hi there, I'm using ...", "default");

                            }

                            System.out.println("good");
                            checkUserExist();
                        } else {
                            System.out.println("bad");
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void checkUserExist() {
        if (firebaseAuth.getCurrentUser() != null) {
            Intent navigationIntent = new Intent(getActivity(), NavigationActivity.class);
            startActivity(navigationIntent);
        }
    }
}
