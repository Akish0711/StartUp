package com.example.ashish.startup.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ashish.startup.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;

public class ForgotPassword extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Forgot Password?");
        }

        Button reset = findViewById(R.id.reset);
        EditText email = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.INVISIBLE);

        reset.setOnClickListener(v -> {
            String user_email = email.getText().toString();
            if (user_email.isEmpty()) {
                email.setError("Field is empty");
                email.requestFocus();
            }else if (isEmailValid(user_email)){
                progressBar.setVisibility(View.VISIBLE);
                sendEmailToUser(user_email);
            }else if (!isEmailValid(user_email)){
                progressBar.setVisibility(View.VISIBLE);
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                rootRef.collection("Users").document(user_email.toUpperCase()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String Email = documentSnapshot.getString("Email");
                            sendEmailToUser(Email);
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            KToast.errorToast(ForgotPassword.this,"No matching Username found",Gravity.BOTTOM,KToast.LENGTH_AUTO);
                        }
                    }
                }).addOnFailureListener(this::onFailure);
            }
        });

    }

    private void sendEmailToUser(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        KToast.successToast(ForgotPassword.this, "Email Sent to your Email ID", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                        finish();
                        startActivity(new Intent(ForgotPassword.this, Login.class));
                    }
                }).addOnFailureListener(this::onFailure);
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

    public void onFailure(@NonNull Exception e) {
        if (!isInternetAvailable()){
            notifyUser("No internet connection.");
        }
        else if (e instanceof FirebaseAuthInvalidUserException) {

            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

            //if (errorCode.equals("ERROR_USER_NOT_FOUND"))
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

    private void notifyUser(String error) {
        progressBar.setVisibility(View.GONE);
        KToast.errorToast(ForgotPassword.this,error,Gravity.BOTTOM,KToast.LENGTH_AUTO);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
