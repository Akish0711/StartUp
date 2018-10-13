package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.ashish.startup.Adapters.AddStudentsAdapter;
import com.example.ashish.startup.Models.Users;
import com.example.ashish.startup.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStudents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);

        if (getIntent().hasExtra("class_id") && getIntent().hasExtra("institute") && getIntent().hasExtra("username")) {
            final String class_id = getIntent().getStringExtra("class_id");
            String Institute = getIntent().getStringExtra("institute");
            String email_red = getIntent().getStringExtra("username");
            String batch = getIntent().getStringExtra("batch");
            String class_name = getIntent().getStringExtra("class_name");
            String total_students_text = getIntent().getStringExtra("total_students");
            int total_students = Integer.parseInt(total_students_text);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Add Students");
            }

            Button selected = findViewById(R.id.selected);
            List<Users> usersList = new ArrayList<>();
            List<String> selectedUsername = new ArrayList<>();
            List<String> selectedName = new ArrayList<>();
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            AddStudentsAdapter mAdapter = new AddStudentsAdapter(this, usersList, selectedUsername, selectedName);
            RecyclerView mMessagesList = findViewById(R.id.student_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute+"_No").whereEqualTo("Batch",batch).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (final DocumentSnapshot document : task.getResult()) {
                        document.getReference().collection("Subjects").document(class_id).get().addOnCompleteListener(task1 -> {
                            progressBar.setVisibility(View.GONE);
                            if (task1.isSuccessful()) {
                                DocumentSnapshot doc = task1.getResult();
                                if (doc == null || !doc.exists()) {
                                    Users users = document.toObject(Users.class);
                                    usersList.add(users);
                                    Collections.sort(usersList, Users.BY_NAME_ALPHABETICAL);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            });

            selected.setOnClickListener(v -> {
                if (selectedUsername.size()>0){
                    int index;
                    for (index=0;index<selectedUsername.size();index++){
                        int finalIndex = index;
                        mFirestore.collection("Users").whereEqualTo("Username", selectedUsername.get(index)).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("Teacher_Name", email_red);
                                    data.put("Subject_Name", class_name);
                                    data.put("Total_Present",0);
                                    data.put("Total_Class",0);
                                    data.put("Percentage",0);
                                    data.put("Username", selectedUsername.get(finalIndex));
                                    data.put("Name",selectedName.get(finalIndex));
                                    document.getReference().collection("Subjects").document(class_id).set(data);

                                    Map<String, Object> data2 = new HashMap<>();
                                    data2.put(class_id,"Added");
                                    document.getReference().update(data2);
                                }
                            }
                        });
                    }
                    String new_total_student = String.valueOf(selectedUsername.size()+total_students);
                    Map<String, Object> data3 = new HashMap<>();
                    data3.put("Total_Students", new_total_student);
                    mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).update(data3);

                    if (index == 1) {
                        KToast.successToast(AddStudents.this, index + " Student Added",Gravity.BOTTOM ,KToast.LENGTH_AUTO);
                    }
                    if (index > 1) {
                        KToast.successToast(AddStudents.this, index + " Students Added",Gravity.BOTTOM ,KToast.LENGTH_AUTO);
                    }
                    finish();
                    startActivity(new Intent(AddStudents.this, MainActivity.class));
                } else {
                    KToast.errorToast(AddStudents.this,"Please select a Student",Gravity.CENTER,KToast.LENGTH_SHORT);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
