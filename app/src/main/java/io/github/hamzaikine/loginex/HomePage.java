package io.github.hamzaikine.loginex;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomSheetFragment.BottomSheetListener, ProfileFragment.SendUpdate, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;             // auth, firestore, sotrageReference, uploadtaskFirebase instances
    private UploadTask uploadTask;
    private FirebaseUser user;
    private RecyclerView rv;
    private RVAdapter adapter;                         //adapter for the recyclerView
    private LinearLayoutManager llm;
    private TextView email, name;
    FrameLayout frameLayoutHomePage;
    private RequestQueue mQueue;                       // queue used with Volley http library
    final private int MY_SOCKET_TIMEOUT_MS = 10000;    //increase the timeout of Volley
    ProgressDialog p;
    private ArrayList<Person> persons;
    private static final String TAG = "HomePage";
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 13;
    private final int CAMERA_REQUEST_CODE = 2;
    private final int GALLERY_REQUEST_CODE = 1;
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buildRecyclerView();
        loadData();


        // instantiation for layout, recycle view adapter, firebase auth, volley queue
        frameLayoutHomePage = findViewById(R.id.home_page);
        adapter = new RVAdapter(this, persons);
        rv.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mQueue = Volley.newRequestQueue(this);

        Log.d("Main", user.getEmail());


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialogFragment();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        email = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        name = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        email.setText(user.getEmail());
        name.setText(user.getDisplayName());
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            rv.setVisibility(View.VISIBLE);
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
        if (id == R.id.nav_manage) {
            ProfileFragment profileFragment = new ProfileFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag("tag");
            // Replace whatever is in the fragment_container view with this fragment,

            // If fragment doesn't exist yet, create one
            if(fragment == null) {
                fragmentTransaction.replace(R.id.home_page, profileFragment,"tag");
                fragmentTransaction.addToBackStack(null);
                rv.setVisibility(View.INVISIBLE);
                // Commit the transaction
                fragmentTransaction.commit();

            }else{  // re-use the old fragment
                rv.setVisibility(View.INVISIBLE);
                fragmentTransaction.replace(R.id.home_page, fragment, "tag");
            }

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
                            Snackbar.make(frameLayoutHomePage, "User profile updated.", Snackbar.LENGTH_LONG)
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
                    Snackbar.make(frameLayoutHomePage, "Please check your inbox to verify your account.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }
            }
        });
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ProfileFragment) {
            ProfileFragment profileFragment = (ProfileFragment) fragment;
            profileFragment.setOnProfileUpdate(this);
        }

    }

    @Override
    public void sendUpdate(String pname, String pemail) {
        if (pname != null) {
            name.setText(pname);
        }

        if (pemail != null) {
            email.setText(pemail);
        }

    }

    public void uploadFileToCloud() {

        //initialize the progress dialog and show it
        p = new ProgressDialog(this);
        p.setMessage("Analyzing in progress....");
        p.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(mCurrentPhotoPath));

        Log.d(TAG, "File URI: " + file.toString());
        // Uri file = Uri.fromFile(new File(fileUri.getPath()));
        final StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, downloadUri.toString());
                    //Send downloadable link to backend server for image processing
                    fetchServerPost(downloadUri.toString());
                    p.dismiss();

                } else {
                    // Handle failures
                    // ...
                    Snackbar.make(frameLayoutHomePage, "Failed to upload.", Snackbar.LENGTH_LONG).show();
                    p.dismiss();
                }
            }
        });


    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "io.github.hamzaikine.loginex.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd_mmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, mCurrentPhotoPath);
        return image;
    }


    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        // Launching the Intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            saveData();
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }

    private void captureFromCamera() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Kindly Reminder");
        alertDialogBuilder.setMessage("Please take your picture in landscape mode. So it shows correctly in your card.");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));

                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                dialogInterface.dismiss();
            }
        }).show();

    }


    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(persons); //persons is an ArrayList instance variable
        prefsEditor.putString("currentList", json);
        prefsEditor.apply();
    }


    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            mCurrentPhotoPath = picturePath;
                            Log.d("CurrentPath", mCurrentPhotoPath);
                            uploadFileToCloud();
                            Snackbar.make(frameLayoutHomePage, "Image uploaded successfully.", Snackbar.LENGTH_LONG).show();
                            //fetchServerPost(mCurrentPhotoPath);
                            cursor.close();


                        }

                    }
                    break;

                case CAMERA_REQUEST_CODE:
                    //imageView.setImageURI(Uri.parse(mCurrentPhotoPath));
                    if (mCurrentPhotoPath != null) {

                        uploadFileToCloud();
                        Snackbar.make(frameLayoutHomePage, "Image uploaded successfully.", Snackbar.LENGTH_LONG).show();
                        // fetchServerPost(mCurrentPhotoPath);


                    }
                    break;

            }

        }
    }


    @Override
    public void onButtonClicked(String text) {
        if (text.equals("gallery")) {
            pickFromGallery();
        }

        if (text.equals("camera"))
            captureFromCamera();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentPath", mCurrentPhotoPath);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPhotoPath = savedInstanceState.getString("currentPath");


    }


    private void buildRecyclerView() {
        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new RVAdapter(this, persons);

        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);

    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RVAdapter.PersonViewHolder) {
            // get the removed item name to display it in snack bar
//            String emotion = persons.get(viewHolder.getAdapterPosition()).emotion;

            // backup of removed item for undo purpose
            final Person deletedItem = persons.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(frameLayoutHomePage, "This card is removed from List!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
        // save changes
        saveData();
    }


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("currentList", "");
        persons = gson.fromJson(json, new TypeToken<ArrayList<Person>>() {
        }.getType());
        if (persons == null) {
            persons = new ArrayList<>();
        }
    }


    private void fetchServerPost(final String pic_url) {


        String server_url = "http://3.224.85.93";
        StringRequest postRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        //getting the whole json object from the response
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray emo = obj.getJSONArray("Emotions");

                            String top_emotion = "Calm";
                            double conf_lower_bound = 50.0;
                            //access dictionaries within JSONArray
                            for (int i = 0; i < emo.length(); i++) {
                                JSONObject jo = emo.getJSONObject(i);
                                Double conf = jo.getDouble("Confidence");
                                String type = jo.getString("Type");
                                if (conf > conf_lower_bound) {
                                    top_emotion = type;
                                }
                            }

                            String gender = obj.getString("Gender");
                            String age = obj.getString("Age");

                            Date now = new Date();
                            persons.add(new Person(now.toString(), top_emotion, age,
                                    gender, mCurrentPhotoPath));

                            rv.getAdapter().notifyDataSetChanged();
                            // save new data
                            saveData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Snackbar.make(frameLayoutHomePage, "Error: Server is down.", Snackbar.LENGTH_LONG).show();
                        p.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("url", pic_url);

                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mQueue.add(postRequest);


    }


}