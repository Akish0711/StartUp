package com.example.ashish.startup.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ashish.startup.others.CircleTransform;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;

public class DeleteAccount extends AppCompatActivity {
    ProgressBar progressBar;
    String Institute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        if (getIntent().hasExtra("uid")) {
            String uid = getIntent().getStringExtra("uid");

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Delete Account");
            }

            Button delete = findViewById(R.id.delete);
            EditText email = findViewById(R.id.email);
            progressBar = findViewById(R.id.progressBar3);
            progressBar.setVisibility(View.INVISIBLE);

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

            rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Institute = documentSnapshot.getString("Institute");
                }
            });

            delete.setOnClickListener(v -> {
                String user_email = email.getText().toString();
                if (user_email.isEmpty()) {
                    email.setError("Field is empty");
                    email.requestFocus();
                }else if (isEmailValid(user_email)){
                    progressBar.setVisibility(View.VISIBLE);
                    rootRef.collection("Users").whereEqualTo("Institute_Admin",Institute+"_No").whereEqualTo("Email",user_email).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            for (final DocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    String Name = documentSnapshot.getString("Name");
                                    String Username = documentSnapshot.getString("Username");
                                    String Email = documentSnapshot.getString("Email");
                                    String Image = documentSnapshot.getString("Image");
                                    progressBar.setVisibility(View.GONE);
                                    dialogOpener(Name, Username, Email, Image);
                                }
                            }
                        }else if (isInternetAvailable()){
                            progressBar.setVisibility(View.GONE);
                            KToast.errorToast(DeleteAccount.this,"No matching Email found",Gravity.BOTTOM,KToast.LENGTH_AUTO);
                        }
                        else{
                            notifyUser("No internet connection.");
                        }
                    }).addOnFailureListener(this::onFailure);
                }else if (!isEmailValid(user_email)){
                    progressBar.setVisibility(View.VISIBLE);
                    rootRef.collection("Users").whereEqualTo("Institute_Admin",Institute+"_No").whereEqualTo("Username",user_email.toUpperCase()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            for (final DocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    String Name = documentSnapshot.getString("Name");
                                    String Username = documentSnapshot.getString("Username");
                                    String Email = documentSnapshot.getString("Email");
                                    String Image = documentSnapshot.getString("Image");
                                    progressBar.setVisibility(View.GONE);
                                    dialogOpener(Name, Username, Email, Image);
                                }
                            }
                        }else if (isInternetAvailable()){
                            progressBar.setVisibility(View.GONE);
                            KToast.errorToast(DeleteAccount.this,"No matching Username found",Gravity.BOTTOM,KToast.LENGTH_AUTO);
                        }
                        else{
                            notifyUser("No internet connection.");
                        }
                    }).addOnFailureListener(this::onFailure);
                }
            });
        }
    }

    public boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = process.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dialogOpener(String name, String username, String email, String image) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete this Account?");
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View attnView = layoutInflater.inflate(R.layout.activity_delete_account_dialog,null);
        final TextView name_detail =attnView.findViewById(R.id.name_detail);
        final TextView username_detail =attnView.findViewById(R.id.username_detail);
        final TextView email_detail =attnView.findViewById(R.id.email_detail);
        final ImageView image_detail = attnView.findViewById(R.id.image_detail);
        final ProgressBar progressbar = attnView.findViewById(R.id.progressBar);

        name_detail.setText(name);
        username_detail.setText(username);
        email_detail.setText(email);

        Glide.with(this)
                .load(image)
                .error(R.drawable.default_profile)
                .crossFade()
                .bitmapTransform(new CircleTransform(this))
                .into(image_detail);

        builder.setMessage("WARNING: You cannot undo this.")
                .setPositiveButton("DELETE", (dialog, id) -> {
                    progressbar.setVisibility(View.VISIBLE);
                    if (isInternetAvailable()) {
                        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                        rootRef.collection("Users").document(username).delete().addOnCompleteListener(task -> {
                            rootRef.collection("Important").document("Deleted").update(username, email);
                            progressbar.setVisibility(View.GONE);
                            finish();
                            Intent intent = new Intent(DeleteAccount.this, DeleteAccount.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        });
                    }else{
                        notifyUser("No internet connection.");
                    }
                });
        builder.setView(attnView);
        builder.setNegativeButton("Cancel",null);

        // builder.setIcon(R.drawable.record);
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void notifyUser(String error) {
        progressBar.setVisibility(View.GONE);
        KToast.errorToast(DeleteAccount.this,error,Gravity.BOTTOM,KToast.LENGTH_AUTO);
    }

    public void onFailure(@NonNull Exception e) {
        if (!isInternetAvailable()){
            notifyUser("No internet connection.");
        }
        else if (e instanceof FirebaseAuthInvalidUserException) {

            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

            //   if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
            if ("ERROR_USER_NOT_FOUND".equals(errorCode)) {
                notifyUser("No matching Email found");
            } else if ("ERROR_USER_DISABLED".equals(errorCode)) {
                notifyUser("User account has been disabled");
            } else {
                notifyUser(e.getLocalizedMessage());
            }
        }
        else{
            notifyUser("Error.Please close the app and try again.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
