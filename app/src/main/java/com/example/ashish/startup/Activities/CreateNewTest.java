package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.Map;

public class CreateNewTest extends AppCompatActivity {

    EditText testName, maxMarks;
    Button createTest;
    ProgressBar progressBar;
    String subject_id;

    private FirebaseAuth mAuth1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_test);

        if(getIntent().hasExtra("class_id")) {
        subject_id = getIntent().getStringExtra("class_id");

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            testName = findViewById(R.id.test_name);
            maxMarks = findViewById(R.id.max_marks);
            createTest = findViewById(R.id.createTest);
            progressBar = findViewById(R.id.progressBar4);

            mAuth1 = FirebaseAuth.getInstance();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Create New Test");
            }

            createTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateTest();
                }
            });
        }
    }

    private void CreateTest() {
        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

        final String tName = testName.getText().toString();
        final String mMarks = maxMarks.getText().toString();

        if (tName.isEmpty()) {
            testName.setError("Test Name is required");
            testName.requestFocus();
            return;
        }

        if (mMarks.isEmpty()) {
            maxMarks.setError("Max Marks is required");
            maxMarks.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String email = mAuth1.getCurrentUser().getEmail();
        String email_red = email.substring(0, email.length() - 10);

        final Map<String ,String> data = new HashMap<>();
        data.put("Max_marks",mMarks);

        rootRef.collection("Users").document(email_red).collection("Subjects").document(subject_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    document.getReference().collection("Marks").document(tName).set(data);
                    progressBar.setVisibility(View.GONE);
                    KToast.successToast(CreateNewTest.this,"New Test Created Succesfully.",Gravity.BOTTOM,KToast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
