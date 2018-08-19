package com.example.ashish.startup.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserNewClass extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private Button view_marks, view_atttendance, announcement;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String Teacher_Name, phone, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_new_class);

        if (getIntent().hasExtra("subject_id") && getIntent().hasExtra("subject_name") && getIntent().hasExtra("Teacher_Name")) {
            final String subject_id = getIntent().getStringExtra("subject_id");
            final String subject_name = getIntent().getStringExtra("subject_name");
            Teacher_Name = getIntent().getStringExtra("Teacher_Name");

            mFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            mFirestore.collection("Users").document(Teacher_Name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        email = document.getString("Email");
                        phone = document.getString("Phone");
                    }
                }
            });

            view_atttendance = findViewById(R.id.view_attendance);
            view_marks = findViewById(R.id.view_marks);
            announcement = findViewById(R.id.announcement_student);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(subject_name);
            }

            String email = mAuth.getCurrentUser().getEmail();

            view_atttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserNewClass.this, UserAttendance.class);
                    intent.putExtra("subject_id", subject_id);
                    intent.putExtra("subject_name", subject_name);
                    intent.putExtra("Teacher_Name", Teacher_Name);
                    startActivity(intent);
                }
            });

            view_marks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserNewClass.this, UserMarks.class);
                    intent.putExtra("subject_id", subject_id);
                    intent.putExtra("subject_name", subject_name);
                    intent.putExtra("Teacher_Name", Teacher_Name);
                    startActivity(intent);
                }
            });

            announcement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserNewClass.this, AnnouncementStudents.class);
                    intent.putExtra("subject_id", subject_id);
                    intent.putExtra("Teacher_Name", Teacher_Name);
                    startActivity(intent);
                }
            });
        }
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_contact) {


            new FancyGifDialog.Builder(this)
                    .setTitle("Contact " + capitalize(Teacher_Name))
                    .setMessage(phone + "\n\n" + email)
                    .setNegativeBtnText("Mail")
                    .setPositiveBtnBackground("#55acee")
                    .setPositiveBtnText("Call")
                    .setNegativeBtnBackground("#dd4b39")
                    .setGifResource(R.drawable.gif10)   //Pass your Gif here
                    .isCancellable(true)
                    .OnPositiveClicked(new FancyGifDialogListener() {
                        @Override
                        public void OnClick() {
                            verifyPermissions();
                        }
                    })
                    .OnNegativeClicked(new FancyGifDialogListener() {
                        @Override
                        public void OnClick() {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:"+email));
                            startActivity(emailIntent);
                        }
                    })
                    .build();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact,menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    private void verifyPermissions() {
        String[] permissions = {Manifest.permission.CALL_PHONE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phone));
            startActivity(callIntent);

        }else{
            ActivityCompat.requestPermissions(UserNewClass.this, permissions, REQUEST_CODE);
        }

    }
}