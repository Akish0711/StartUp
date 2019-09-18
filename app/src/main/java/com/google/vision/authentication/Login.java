package com.google.vision.authentication;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.vision.activities.MainActivity;
import com.google.vision.activities.MainActivityStudent;
import com.google.vision.activities.MainActivityTeacher;
import com.google.vision.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;

public class Login extends AppCompatActivity {
    private EditText mEmailField;
    private EditText mPasswordField;
    ProgressBar mProgressBar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            rootRef.collection("Users").document(user.getUid()).addSnapshotListener((documentSnapshot, e) -> {
                if (documentSnapshot.getString("Admin").equals("Yes")){
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }else if (documentSnapshot.getString("Teacher").equals("Yes")) {
                    startActivity(new Intent(Login.this, MainActivityTeacher.class));
                    finish();
                }else {
                    startActivity(new Intent(Login.this, MainActivityStudent.class));
                    finish();
                }
            });
        }else {
            setContentView(R.layout.activity_login);
            mEmailField = findViewById(R.id.username);
            mPasswordField = findViewById(R.id.password);
            Button mLoginBtn = findViewById(R.id.login);
            Button forgot = findViewById(R.id.forgot);
            Button trouble = findViewById(R.id.trouble);

            mProgressBar =  findViewById(R.id.progressBar2);
            mProgressBar.setVisibility(View.INVISIBLE);

            mLoginBtn.setOnClickListener(view -> startSign());

            forgot.setOnClickListener(v -> {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            });

            trouble.setOnClickListener(v -> {
                startActivity(new Intent(Login.this, ForgotPassword.class));
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

    public void startSign(){
        String email = mEmailField.getText().toString().toUpperCase();
        String password = mPasswordField.getText().toString();

        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){
            KToast.warningToast(Login.this, "Fields are Empty.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
        }else if (isEmailValid(email)){
            mProgressBar.setVisibility(View.VISIBLE);
            loginUser(email,password);
        }else if (!isEmailValid(email)){
            mProgressBar.setVisibility(View.VISIBLE);
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            rootRef.collection("Users").whereEqualTo("Username",email.toUpperCase()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (final DocumentSnapshot document : task.getResult()) {
                            if (document != null && document.exists()) {
                                String Email = document.getString("Email");
                                loginUser(Email, password);
                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                KToast.errorToast(Login.this, "Oops!Please try again", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                            }
                        }
                    }else if (!isInternetAvailable()){
                        notifyUser("No internet connection");
                    }else {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        KToast.errorToast(Login.this, "No Matching Account Found", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                    }
                }
            }).addOnFailureListener(this::onFailure);
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void loginUser(String email, String password) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser user = mAuth.getCurrentUser();
                rootRef.collection("Users").document(user.getUid()).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot document = task1.getResult();
                        if (document.getString("Admin").equals("Yes")){
                            KToast.successToast(Login.this, "Logged in as Admin.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                            finish();
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                        else if (document.getString("Teacher").equals("Yes")) {
                            KToast.successToast(Login.this, "Logged in as Teacher.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                            finish();
                            startActivity(new Intent(Login.this, MainActivityTeacher.class));
                        } else {
                            KToast.successToast(Login.this, "Logged in as Student.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                            finish();
                            startActivity(new Intent(Login.this, MainActivityStudent.class));
                        }
                    }
                });
            }
        }).addOnFailureListener(this::onFailure);
    }

    public void onFailure(@NonNull Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            notifyUser("Invalid password");
        }
        else if (!isInternetAvailable()){
            notifyUser("No internet connection.");
        }
        else if (e instanceof FirebaseAuthInvalidUserException) {
            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();
            //   if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
            if ("ERROR_USER_NOT_FOUND".equals(errorCode)) {
                notifyUser("No matching Account found");
            } else if ("ERROR_USER_DISABLED".equals(errorCode)) {
                notifyUser("User account has been disabled");
            } else {
                notifyUser(e.getLocalizedMessage());
            }
        }
        else{
            notifyUser("Sign in error.Please close the app and try again.");
        }
    }

    private void notifyUser(String error) {
        mProgressBar.setVisibility(View.INVISIBLE);
        KToast.errorToast(Login.this,error,Gravity.BOTTOM,KToast.LENGTH_AUTO);
    }
}
