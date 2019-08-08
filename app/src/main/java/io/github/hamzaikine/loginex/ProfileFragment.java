package io.github.hamzaikine.loginex;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private EditText editText_update_name, editText_update_email, editText_update_password;
    private Button btn_update_name, btn_update_email, btn_update_password, btn_delete;
    SendUpdate callback;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public void setOnProfileUpdate(Activity activity) {
        callback = (SendUpdate) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //initialize buttons
        btn_update_name = view.findViewById(R.id.btn_update_name);
        btn_update_email = view.findViewById(R.id.btn_update_email);
        btn_update_password = view.findViewById(R.id.btn_update_password);
        btn_delete = view.findViewById(R.id.btn_delete);

        //initialize editText
        editText_update_name = view.findViewById(R.id.editText_update_name);
        editText_update_email = view.findViewById(R.id.editText_update_email);
        editText_update_password = view.findViewById(R.id.editText_update_password);



        btn_update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (!isEmpty(editText_update_name)) {
                    final String user_name = editText_update_name.getText().toString();

                    //create a profile update with the new name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(user_name)
                            .build();

                    //update the user in Firebase
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //send data to activity to reflect changes
                                callback.sendUpdate(user_name, null);
                                Toast.makeText(getView().getContext(), "user name updated.", Toast.LENGTH_LONG).show();

                            }else
                                Toast.makeText(getView().getContext(), "user name not updated.", Toast.LENGTH_LONG).show();
                        }
                    });

                }


            }
        });

        btn_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!isEmpty(editText_update_email)) {
                    final String user_email = editText_update_email.getText().toString();

                    //update the user in Firebase
                    user.updateEmail(user_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Main", "User email address updated.");
                                //send data to activity to reflect changes
                                callback.sendUpdate(null, user_email);
                                Toast.makeText(getView().getContext(), "user email" +
                                        " updated.", Toast.LENGTH_LONG).show();
                            }else
                                Toast.makeText(getView().getContext(), "user email not updated.", Toast.LENGTH_LONG).show();


                        }
                    });


                }
            }
        });

        btn_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!isEmpty(editText_update_password)) {
                    String user_password = editText_update_password.getText().toString();
                    //update the user in Firebase
                    user.updatePassword(user_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getView().getContext(), "user password updated.", Toast.LENGTH_LONG).show();
                            }else
                                Toast.makeText(getView().getContext(), "user password not updated.", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
               final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getActivity().startActivity(intent);
                                        Toast.makeText(view.getContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });


        return view;
    }


    public interface SendUpdate {
        public void sendUpdate(String name, String email);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


}
