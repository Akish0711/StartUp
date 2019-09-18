package com.google.vision.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
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
import com.google.vision.others.CircleTransform;
import com.google.vision.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeleteAccount extends AppCompatActivity {
    ProgressBar progressBar;
    String Code, uid, StringYear;
    int year;
    private long left_students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");

            Date today = new Date(); // Fri Jun 17 14:54:28 PDT 2016
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);

            year = cal.get(Calendar.YEAR);
            StringYear = Integer.toString(year);

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
                    Code = documentSnapshot.getString("Code");
                }
            });

            delete.setOnClickListener(v -> {
                String user_email = email.getText().toString();
                if (user_email.isEmpty()) {
                    email.setError("Field is empty");
                    email.requestFocus();
                }else if (isEmailValid(user_email)){
                    progressBar.setVisibility(View.VISIBLE);
                    rootRef.collection("Users").whereEqualTo("Code_Admin",Code+"_No").whereEqualTo("Email",user_email).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            for (final DocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    String Name = documentSnapshot.getString("Name");
                                    String Username = documentSnapshot.getString("Username");
                                    String Email = documentSnapshot.getString("Email");
                                    String UserUid = documentSnapshot.getString("Uid");
                                    progressBar.setVisibility(View.GONE);
                                    dialogOpener(Name, Username, Email, UserUid);
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
                    rootRef.collection("Users").whereEqualTo("Code_Admin",Code+"_No").whereEqualTo("Username",user_email.toUpperCase()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            for (final DocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    String Name = documentSnapshot.getString("Name");
                                    String Username = documentSnapshot.getString("Username");
                                    String Email = documentSnapshot.getString("Email");
                                    String UserUid = documentSnapshot.getString("Uid");
                                    progressBar.setVisibility(View.GONE);
                                    dialogOpener(Name, Username, Email, UserUid);
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

    private void dialogOpener(String name, String username, String email, String userUid) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete this Account?");
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View attnView = layoutInflater.inflate(R.layout.activity_delete_account_dialog,null);
        final TextView name_detail =attnView.findViewById(R.id.name_detail);
        final TextView username_detail =attnView.findViewById(R.id.username_detail);
        final TextView email_detail =attnView.findViewById(R.id.email_detail);
        final ImageView image_detail = attnView.findViewById(R.id.image_detail);

        name_detail.setText(name);
        username_detail.setText(username);
        email_detail.setText(email);

        Glide.with(this)
                .load(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .crossFade()
                .bitmapTransform(new CircleTransform(this))
                .into(image_detail);

        builder.setMessage("WARNING: You cannot undo this.")
                .setPositiveButton("DELETE", (dialog, id) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    if (isInternetAvailable()) {
                        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

                        rootRef.collection("Users").document(userUid).collection("Subjects").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for (final DocumentSnapshot document : task.getResult()) {
                                    String Classid = document.getId();
                                    String Teacher_id = document.getString("Teacher_id");
                                    rootRef.collection("Users").document(Teacher_id).collection("Subjects").document(Classid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                Map<String, Object> update_count = new HashMap<>();

                                                String total_students = doc.getString("Total_Students");
                                                int int_students = Integer.parseInt(total_students);
                                                int_students--;
                                                total_students = Integer.toString(int_students);
                                                update_count.put("Total_Students", total_students);
                                                doc.getReference().update(update_count);
                                            }
                                        }
                                    });
                                    rootRef.collection("Attendance").document(Classid).collection("Students").document(userUid).delete();
                                }
                            }
                        }).addOnCompleteListener(task -> rootRef.collection("Users").document(userUid).delete().addOnCompleteListener(task1 -> {
                            rootRef.collection("Users").document(uid).collection("Performance").document(StringYear).get().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    DocumentSnapshot doc = task12.getResult();
                                    Map<String, Object> update_performance = new HashMap<>();

                                    if (doc == null || !doc.exists()) {
                                        left_students = 1;
                                        update_performance.put("Left_Students", left_students);
                                        update_performance.put("Total_Students", 0);
                                        update_performance.put("Year", year);
                                        doc.getReference().set(update_performance);

                                    }else{
                                        left_students = doc.getLong("Left_Students");
                                        left_students++;
                                        update_performance.put("Left_Students", left_students);
                                        doc.getReference().update(update_performance);
                                    }
                                }
                            }).addOnCompleteListener(task2 -> {
                                rootRef.collection("Important").document("Deleted").update(username, email);
                                progressBar.setVisibility(View.GONE);
                                finish();
                                KToast.successToast(DeleteAccount.this, "Account Deleted", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                Intent intent = new Intent(DeleteAccount.this, DeleteAccount.class);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            });
                        }));
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
