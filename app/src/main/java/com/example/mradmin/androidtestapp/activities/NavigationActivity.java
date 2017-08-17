package com.example.mradmin.androidtestapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mradmin.androidtestapp.Constants;
import com.example.mradmin.androidtestapp.FirebaseApplication;
import com.example.mradmin.androidtestapp.R;
import com.example.mradmin.androidtestapp.entities.User;
import com.example.mradmin.androidtestapp.fragments.ContactsFragment;
import com.example.mradmin.androidtestapp.fragments.DialoguesFragment;
import com.example.mradmin.androidtestapp.fragments.EditProfileFragment;
import com.example.mradmin.androidtestapp.fragments.FriendsFragment;
import com.example.mradmin.androidtestapp.fragments.SettingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;

    FirebaseAuth mAuth;
    DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = ((FirebaseApplication) getApplication()).getFirebaseAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        userDB = ((FirebaseApplication)getApplication()).getFirebaseDatabase().child(user.getUid());


        //for set dialogues fragment aat startup
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        //for header name text
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.nav_avatar_name);
        if (user != null) {
            nav_user.setText(user.getDisplayName());
        }


        //for header image avatar
        CircleImageView circleImageView = (CircleImageView) hView.findViewById(R.id.imageViewHeaderAvatar);
        StorageReference storageReference = ((FirebaseApplication) getApplication()).getFirebaseStorage();
        StorageReference thumbs = storageReference.child("thumbs");
        StorageReference thumb = thumbs.child(user.getUid() + ".jpg");
        String thumbUrl = thumb.getDownloadUrl().toString();
        setUserImageAvatar(thumbUrl, this, circleImageView);


        //for log out button
        ImageButton logOutButton = (ImageButton) hView.findViewById(R.id.button_log_out);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("------- log out -------");

                ((FirebaseApplication) getApplication()).getFirebaseAuth().signOut();
                startActivity(new Intent(NavigationActivity.this, FirstActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser curUser = mAuth.getCurrentUser();

        if (curUser == null) {

            Intent first = new Intent(NavigationActivity.this, FirstActivity.class);
            startActivity(first);
            finish();

        } else {

            userDB.child("online").setValue("true");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser curUser = mAuth.getCurrentUser();

        if (curUser != null){

            userDB.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    public void setUserImageAvatar(String image, Context context, CircleImageView userImageView) {

        Picasso.with(context).load(image).placeholder(R.mipmap.ic_launcher).into(userImageView);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.moveTaskToBack(true);
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Fragment fragment = null;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            fragment = new SettingsFragment();
        } else if (id == R.id.action_contacts) {
            fragment = new ContactsFragment();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_content, fragment).commit();
        }

        item.setChecked(true);
        setTitle(item.getTitle());
        return true;
        //return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        android.support.v4.app.Fragment fragment = null;

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_friends) {

            fragment = new FriendsFragment();

        } else if (id == R.id.nav_settings) {

            fragment = new SettingsFragment();

        } else if (id == R.id.nav_dialogues) {

            fragment = new DialoguesFragment();

        } else if (id == R.id.nav_edit_profile) {

            fragment = new EditProfileFragment();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_content, fragment).commit();
        }

        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
