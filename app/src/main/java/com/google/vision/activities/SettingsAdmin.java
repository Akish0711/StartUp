package com.google.vision.activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.vision.R;
import com.google.vision.authentication.Login;
import com.google.vision.authentication.ReAuthentication;
import com.google.firebase.auth.FirebaseAuth;
import com.onurkaganaldemir.ktoastlib.KToast;

public class SettingsAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_admin);

        if (getIntent().hasExtra("uid")) {
            String uid = getIntent().getStringExtra("uid");


            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Settings");
            }

            LinearLayout change_btn = findViewById(R.id.change_btn);
            //LinearLayout editProfile = findViewById(R.id.editProfile);
            LinearLayout logout = findViewById(R.id.logout);

            change_btn.setOnClickListener(v -> startActivity(new Intent(SettingsAdmin.this, ReAuthentication.class)));

            /*editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsAdmin.this,EditProfile.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });*/

            logout.setOnClickListener(v -> new AlertDialog.Builder(SettingsAdmin.this)
                    .setMessage("Are you sure you want to Logout?")
                    .setCancelable(false)
                    .setPositiveButton("Logout", (dialog, id1) -> {
                        FirebaseAuth.getInstance().signOut();
                        finishAffinity();
                        Intent intent = new Intent(SettingsAdmin.this, Login.class);
                        startActivity(intent);
                        KToast.successToast(SettingsAdmin.this, "Logged Out", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                    })
                    .setNegativeButton("Cancel", null)
                    .show());
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
