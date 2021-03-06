package com.example.mradmin.androidtestapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
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
import com.example.mradmin.androidtestapp.fragments.BlogFragment;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;

    FirebaseAuth mAuth;
    DatabaseReference userDB;

    SwipeRefreshLayout swipeRefreshLayout;

    private static int selectedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = ((FirebaseApplication) getApplication()).getFirebaseAuth();
        final FirebaseUser user = mAuth.getCurrentUser();
        userDB = ((FirebaseApplication)getApplication()).getFirebaseDatabase().child(user.getUid());


        //for set dialogues fragment aat startup
        navigationView.getMenu().getItem(selectedItem).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(selectedItem));

        //for header name text
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.nav_avatar_name);


        // ---------------------------------------------------------------------------------- FOR SWIPE REFRESH
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshNav);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorBackgroundLight, R.color.colorButtonEnabled, R.color.colorIcon);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);

                if (selectedItem < 4) {

                    Intent navIntent = new Intent(NavigationActivity.this, NavigationActivity.class);
                    startActivity(navIntent);
                    navigationView.getMenu().getItem(selectedItem).setChecked(true);
                    onNavigationItemSelected(navigationView.getMenu().getItem(selectedItem));

                }

            }
        });




        final CircleImageView nav_user_image = (CircleImageView)hView.findViewById(R.id.imageViewHeaderAvatar);

        if (user != null) {
            nav_user.setText(user.getDisplayName());

            userDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String image = dataSnapshot.child("image").getValue().toString();
                    String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                    if (!image.equals("default")) {

                        Picasso.with(NavigationActivity.this).load(thumb).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.mipmap.ic_launcher).into(nav_user_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(NavigationActivity.this).load(image).placeholder(R.mipmap.ic_launcher).into(nav_user_image);

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Create new post", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //navigationView.getMenu().getItem(0).setChecked(true);
                //onNavigationItemSelected(navigationView.getMenu().getItem(0));

                startActivity(new Intent(NavigationActivity.this, EditPostActivity.class));

            }
        });


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

                userDB.child("online").setValue(ServerValue.TIMESTAMP);

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

            fab.setVisibility(View.INVISIBLE);

            fragment = new SettingsFragment();

            selectedItem = 5;

        } else if (id == R.id.action_contacts) {

            fab.setVisibility(View.INVISIBLE);

            fragment = new ContactsFragment();

            selectedItem = 3;

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

        if (id == R.id.nav_blog) {

            fab.setVisibility(View.INVISIBLE);

            fragment = new BlogFragment();

            selectedItem = 0;

        } else if (id == R.id.nav_dialogues) {

            fab.setVisibility(View.INVISIBLE);

            fragment = new DialoguesFragment();

            selectedItem = 1;

        } else if (id == R.id.nav_friends) {

            fab.setVisibility(View.VISIBLE);

            fragment = new FriendsFragment();

            selectedItem = 2;

        } else if (id == R.id.nav_contacts) {

            fab.setVisibility(View.INVISIBLE);

            fragment = new ContactsFragment();

            selectedItem = 3;

        } else if (id == R.id.nav_edit_profile) {

            fab.setVisibility(View.INVISIBLE);

            fragment = new EditProfileFragment();

            selectedItem = 4;

        } else if (id == R.id.nav_settings) {

            fab.setVisibility(View.INVISIBLE);

            fragment = new SettingsFragment();

            selectedItem = 5;

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
