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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.ashish.startup.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CreateAccount extends AppCompatActivity {

    EditText name,phoneNumber, email;
    Button createAccount;
    ProgressBar progressBar;
    Spinner spinner;
    int year;
    String StringYear, uid;
    private static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private String genPswd;
    private Random random;
    private FirebaseAuth mAuth2;
    private long total_batch_students, total_students;
    View parentLayout;
    private static final int REQUEST_CODE = 1;
    private List<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            classList = new ArrayList<>();
            classList.add("Select Class");

            Date today = new Date(); // Fri Jun 17 14:54:28 PDT 2016
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);

            year = cal.get(Calendar.YEAR);
            StringYear = Integer.toString(year);

            name = findViewById(R.id.name);
            parentLayout = findViewById(R.id.create_account_parent);
            phoneNumber = findViewById(R.id.phoneNumber);
            email = findViewById(R.id.email);
            createAccount = findViewById(R.id.createAccount);
            progressBar = findViewById(R.id.progressBar3);
            progressBar.setVisibility(View.INVISIBLE);
            spinner = findViewById(R.id.spinner);

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

            rootRef.collection("Users").document(uid).collection("Classes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (final DocumentSnapshot document : task.getResult()) {
                        String Name = document.getString("Name");
                        classList.add(Name);
                    }
                }
            }).addOnCompleteListener(task -> {
                ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CreateAccount.this,
                        android.R.layout.simple_list_item_1, classList);

                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(myAdapter);
            });

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
                getSupportActionBar().setTitle("Register Students");
            }

            createAccount.setOnClickListener(v -> verifyPermissions());
            random = new Random();
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

    public void registerUser() {
        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        String section = spinner.getSelectedItem().toString();
        String batch2 = spinner.getSelectedItem().toString();
        final String user_name = name.getText().toString();
        final String user_number = phoneNumber.getText().toString();
        final String user_email = email.getText().toString();

        final String[] batch = new String[1];

        if (user_name.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
        }else if (spinner == null || spinner.getSelectedItem() ==null || section.equals("Select Class")) {
            KToast.warningToast(this,"Class Required",Gravity.BOTTOM,KToast.LENGTH_LONG);
        }else if(user_number.isEmpty()){
            phoneNumber.setError("Contact Number required");
            phoneNumber.requestFocus();
        }else if(user_number.length()!=10){
            phoneNumber.setError("Enter Correct Contact Number");
            phoneNumber.requestFocus();
        }else if (user_email.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
        }else if (!isEmailValid(user_email)){
            email.setError("Email not properly formatted");
            email.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            rootRef.collection("Important").document("Batch").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    if (section.equals("Class 12th")||section.equals("Class 11th")) {
                        DocumentSnapshot document = task.getResult();
                        batch[0] = document.getString(section);
                    }else{
                        DocumentSnapshot document = task.getResult();
                        batch[0] = document.getString(batch2);
                    }
                }
            }).addOnSuccessListener(documentSnapshot -> rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    final String Institute = document.getString("Institute");
                    String code = document.getString("Code");
                    //long total_students = document.getLong("Total_Students");
                    //total_students++;
                    //Map<String, Object> update_data = new HashMap<>();
                    //update_data.put("Total_Students", total_students);
                    Map<String, Object> update_data_institute = new HashMap<>();

                    document.getReference().collection("Batches").document(batch[0]).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot doc = task1.getResult();
                            if (doc == null || !doc.exists()) {
                                total_batch_students = 1;
                                update_data_institute.put("Total_Batch_Students", total_batch_students);
                            }else{
                                total_batch_students = doc.getLong("Total_Batch_Students");
                                total_batch_students++;
                                update_data_institute.put("Total_Batch_Students", total_batch_students);
                            }
                        }
                    }).addOnCompleteListener(task12 -> rootRef.collection("Users").document(uid).collection("Performance").document(StringYear).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot doc = task1.getResult();
                            Map<String, Object> update_performance = new HashMap<>();
                            if (doc == null || !doc.exists()) {
                                total_students = 1;
                                update_performance.put("Total_Students", total_students);
                                update_performance.put("Year", year);
                                update_performance.put("Left_Students", 0);
                                doc.getReference().set(update_performance);
                            }else{
                                total_students = doc.getLong("Total_Students");
                                total_students++;
                                update_performance.put("Total_Students", total_students);
                                doc.getReference().update(update_performance);
                            }
                        }
                    }).addOnCompleteListener(task13 -> {
                        String new_username = (batch[0]+code+total_batch_students);
                        genPswd = genRandomPswd();

                        mAuth2.createUserWithEmailAndPassword(user_email,genPswd).addOnCompleteListener(task1 -> {
                            progressBar.setVisibility(View.GONE);
                            if (task1.isSuccessful()) {
                                FirebaseUser user2 = mAuth2.getCurrentUser();
                                String user2Uid = user2.getUid();

                                Map<String, Object> data = new HashMap<>();
                                data.put("Name", user_name);
                                data.put("Username",new_username);
                                data.put("Institute_Batch", Institute +"_"+ batch[0]);
                                data.put("Institute_Admin", Institute+"_No");
                                data.put("Admin","No");
                                data.put("Teacher","No");
                                data.put("Phone",user_number);
                                data.put("Batch", batch[0]);
                                data.put("Email",user_email.toLowerCase());
                                data.put("Uid",user2Uid);
                                data.put("Admin_Uid", uid);

                                rootRef.collection("Users").document(user2Uid).set(data).addOnCompleteListener(task123 -> rootRef.collection("Users").document(uid).collection("Current Classes").whereEqualTo("Batch",batch[0]).get().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()){
                                        for (final DocumentSnapshot document1 : task2.getResult()) {
                                            String Classid = document1.getString("Class_id");
                                            if (Classid!=null) {
                                                rootRef.collection("Users").document(user2Uid).update(Classid, "Removed");
                                            }
                                        }
                                    }
                                }));
                                //rootRef.collection("Users").document(uid).update(update_data);
                                rootRef.collection("Users").document(uid).collection("Batches").document(batch[0]).set(update_data_institute);

                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(user_number, null, "Welcome OnBoard "+user_name+"!!\n\nHere are your Login Details \n\nUsername : " + new_username + "\nPassword : " + genPswd, null, null);
                                mAuth2.signOut();
                                finishAffinity();
                                KToast.successToast(CreateAccount.this, "Student Registered", Gravity.BOTTOM, KToast.LENGTH_SHORT);

                            }else {
                                if (task1.getException() instanceof FirebaseAuthUserCollisionException) {
                                    KToast.errorToast(CreateAccount.this, "This User is already registered.", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                } else {
                                    KToast.errorToast(CreateAccount.this, task1.getException().getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                }
                            }
                        });
                    }));
                }
            }));
        }
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


