package io.github.hamzaikine.loginex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;


import static io.github.hamzaikine.loginex.Login.FIREBASE_AUTH;
import static java.lang.Thread.sleep;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventFragment.OnSelectedDate, ProfileFragment.SendUpdate {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView email,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.d("Main",user.getEmail());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String displayName = mAuth.getCurrentUser().getDisplayName();
                Snackbar.make(view, "Welcome " + displayName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        email = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        name = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        email.setText(user.getEmail());
        name.setText(user.getDisplayName());
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            mAuth.signOut();

            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        } else
         if (id == R.id.nav_calendar) {
            EventFragment eventFragment = new EventFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            transaction.replace(R.id.home_page, eventFragment);
            // Commit the transaction
            transaction.commit();
        }else if (id == R.id.nav_manage){
             ProfileFragment profileFragment = new ProfileFragment();
             FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
             // Replace whatever is in the fragment_container view with this fragment,
             transaction.replace(R.id.home_page, profileFragment);
             // Commit the transaction
             transaction.commit();

         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateUserName(String name) {
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Main", "User profile updated.");
                            Snackbar.make(getCurrentFocus(), "User profile updated.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }
                });
    }

    private void verifyProfile() {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Main", "Email sent.");
                    Snackbar.make(getCurrentFocus(), "Please check your inbox to verify your account.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }
            }
        });
    }


    @Override
    public void sendDate(LocalDate date) {
        Snackbar.make(getCurrentFocus(),
                String.valueOf(date.getDayOfWeek()) + ", "
                        + String.valueOf(date.getDayOfMonth()) + "/"
                        + String.valueOf(date.getMonth()) + "/"
                        + String.valueOf(date.getYear())
                , Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof EventFragment) {
            EventFragment eventFragment = (EventFragment) fragment;
            eventFragment.setOnSelectedDateListener(this);
        }
        if (fragment instanceof ProfileFragment) {
            ProfileFragment profileFragment = (ProfileFragment) fragment;
            profileFragment.setOnProfileUpdate(this);
        }

    }

    @Override
    public void sendUpdate(String pname, String pemail) {
          if(pname != null){
              name.setText(pname);
          }

          if(pemail != null){
              email.setText(pemail);
          }

    }
}
