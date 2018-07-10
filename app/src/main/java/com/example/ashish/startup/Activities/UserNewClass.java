package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

public class UserNewClass extends AppCompatActivity {

    private Button view_marks,view_atttendance;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_new_class);

        if(getIntent().hasExtra("subject_id")&& getIntent().hasExtra("subject_name") && getIntent().hasExtra("Teacher_Name")){
            final String subject_id = getIntent().getStringExtra("subject_id");
            final String subject_name = getIntent().getStringExtra("subject_name");
            final String Teacher_Name = getIntent().getStringExtra("Teacher_Name");

            view_atttendance = findViewById(R.id.view_attendance);
            view_marks = findViewById(R.id.view_marks);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(subject_name);
            }

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            String email = mAuth.getCurrentUser().getEmail();
            String email_red = email.substring(0, email.length() - 10);

            view_atttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserNewClass.this,UserAttendance.class);
                    intent.putExtra("subject_id",subject_id);
                    intent.putExtra("subject_name",subject_name);
                    intent.putExtra("Teacher_Name",Teacher_Name);
                    startActivity(intent);
                }
            });

            view_marks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserNewClass.this,UserMarks.class);
                    intent.putExtra("subject_id",subject_id);
                    intent.putExtra("subject_name",subject_name);
                    intent.putExtra("Teacher_Name",Teacher_Name);
                    startActivity(intent);
                }
            });
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}