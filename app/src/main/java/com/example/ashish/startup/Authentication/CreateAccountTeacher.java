package com.example.ashish.startup.authentication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ashish.startup.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateAccountTeacher extends AppCompatActivity {

    EditText name,phoneNumber, email;
    Button createAccount;
    ProgressBar progressBar;
    String uid;
    private static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private String genPswd;
    private Random random;
    private FirebaseAuth mAuth2;
    View parentLayout;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_teacher);

        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            name = findViewById(R.id.name);
            email = findViewById(R.id.email);
            parentLayout = findViewById(R.id.create_teacher_parent);
            phoneNumber = findViewById(R.id.phoneNumber);
            createAccount = findViewById(R.id.createAccount);
            progressBar = findViewById(R.id.progressBar3);
            progressBar.setVisibility(View.INVISIBLE);

            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://startup-ec618.firebaseio.com")
                    .setApiKey("AIzaSyCRS7dY1FKZdfFmfLLTeRtQm_hyTbsERn8")
                    .setApplicationId("startup-ec618").build();

            try {
                FirebaseApp app = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "app");
                mAuth2 = FirebaseAuth.getInstance(app);
            } catch (IllegalStateException e) {
                mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("app"));
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Register Teachers");
            }

            createAccount.setOnClickListener(v -> verifyPermissions());
            random = new Random();
        }
    }

    public String genRandomPswd(){
        StringBuilder sb = new StringBuilder(8);
        for(int i = 0; i < 8; i++){
            sb.append(DATA.charAt(random.nextInt(DATA.length())));
        }
        return sb.toString();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void registerUser() {
        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final String user_name = name.getText().toString();
        final String user_number = phoneNumber.getText().toString();
        final String user_email = email.getText().toString();

        if (user_name.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
        }else if(user_number.isEmpty()){
            phoneNumber.setError("Contact Number required");
            phoneNumber.requestFocus();
        }else if(user_number.length()!=10){
            phoneNumber.setError("Enter Correct Contact Number");
            phoneNumber.requestFocus();
        }else if(user_email.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
        }else if (!isEmailValid(user_email)){
            email.setError("Email not properly formatted");
            email.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    String code = document.getString("Code");
                    long overall_teachers = document.getLong("Overall_Teachers");
                    long total_teachers = document.getLong("Total_Teachers");
                    overall_teachers++;

                    Map<String, Object> update_data = new HashMap<>();
                    update_data.put("Total_Teachers", total_teachers);
                    update_data.put("Overall_Teachers", overall_teachers);

                    String new_username = (code+overall_teachers);
                    genPswd = genRandomPswd();

                    mAuth2.createUserWithEmailAndPassword(user_email, genPswd).addOnCompleteListener(task1 -> {
                        progressBar.setVisibility(View.GONE);
                        if (task1.isSuccessful()) {
                            FirebaseUser user2 = mAuth2.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(new_username).build();
                            user2.updateProfile(profileUpdates);

                            Map<String, Object> data = new HashMap<>();
                            data.put("Name", user_name);
                            data.put("Username", new_username);
                            data.put("Code", code);
                            data.put("Teacher","Yes");
                            data.put("Admin", "No");
                            data.put("Email", user_email);
                            data.put("Phone",user_number);
                            data.put("Admin_Uid", uid);
                            rootRef.collection("Users").document(user2.getUid()).set(data);
                            rootRef.collection("Users").document(uid).update(update_data);

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(user_number, null, "Welcome OnBoard "+user_name+"!!\n\nHere are your Login Details \n\nUsername : " + new_username + "\nPassword : " + genPswd, null, null);
                            mAuth2.signOut();
                            finishAffinity();
                            KToast.successToast(CreateAccountTeacher.this, "Teacher Registered", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                        } else {
                            if (task1.getException() instanceof FirebaseAuthUserCollisionException) {
                                KToast.errorToast(CreateAccountTeacher.this, "This User is already registered.", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                            } else {
                                KToast.errorToast(CreateAccountTeacher.this, task1.getException().getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            });
        }
    }

    private void verifyPermissions() {
        String[] permissions = {Manifest.permission.SEND_SMS};
        if(ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            if (isInternetAvailable()){
                registerUser();
            }else {
                Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
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

    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // finish the activity
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}


