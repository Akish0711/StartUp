package com.google.vision.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.vision.R;
import com.google.vision.authentication.Login;
import com.google.vision.authentication.ReAuthentication;
import com.google.firebase.auth.FirebaseAuth;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;

public class SettingsAdmin extends AppCompatActivity {
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_admin);

        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");


            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Settings");
            }

            LinearLayout change_btn = findViewById(R.id.change_btn);
            View parentLayout = findViewById(R.id.settings_parent);

            //LinearLayout editProfile = findViewById(R.id.editProfile);
            LinearLayout logout = findViewById(R.id.logout);

            change_btn.setOnClickListener(v -> startActivity(new Intent(SettingsAdmin.this, ReAuthentication.class)));

            /*editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsAdmin.this,EditProfile.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });*/

            logout.setOnClickListener(v ->
                    new AlertDialog.Builder(SettingsAdmin.this)
                            .setMessage("Are you sure you want to Logout?")
                            .setCancelable(false)
                            .setPositiveButton("Logout", (dialog, id1) -> {
                                if (isInternetAvailable()) {
                                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                    rootRef.collection("Users").document(uid).update("token", FieldValue.delete()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            finishAffinity();
                                            Intent intent = new Intent(SettingsAdmin.this, Login.class);
                                            startActivity(intent);
                                            FirebaseAuth.getInstance().signOut();
                                            KToast.successToast(SettingsAdmin.this, "Logged Out", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                        }
                                    });
                                }else{
                                    Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show());
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
