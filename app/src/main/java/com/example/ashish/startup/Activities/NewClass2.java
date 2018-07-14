package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewClass2 extends AppCompatActivity {

    private Button add_students, take_attendance, marks, announcement;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class2);

        if (getIntent().hasExtra("class_id")){
            final String class_id = getIntent().getStringExtra("class_id");

            add_students = findViewById(R.id.add_students);
            take_attendance = findViewById(R.id.take_attendance);
            marks = findViewById(R.id.marks);
            announcement = findViewById(R.id.announcement);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Your Class");
            }

            mFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            String email = mAuth.getCurrentUser().getEmail();
            String email_red = email.substring(0, email.length() - 10);
            final String[] Institute = new String[1];
            mFirestore.collection("Users").document(email_red).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Institute[0] = document.getString("Institute");
                    }
                }
            });

            add_students.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NewClass2.this,AddStudents.class);
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("institute",Institute[0]);
                    startActivity(intent);
                }
            });

            take_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NewClass2.this,TakeAttendance.class);
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("institute",Institute[0]);
                    startActivity(intent);
                }
            });

            marks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewClass2.this,UploadMarks.class);
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("institute",Institute[0]);
                    startActivity(intent);
                }
            });

            announcement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewClass2.this,Announcement.class);
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("institute",Institute[0]);
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
