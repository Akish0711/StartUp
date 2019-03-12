package com.example.ashish.startup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStudents extends AppCompatActivity {
    private String name;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);

        if (getIntent().hasExtra("class_id")
                && getIntent().hasExtra("institute")
                && getIntent().hasExtra("uid")
                && getIntent().hasExtra("batch")
                && getIntent().hasExtra("class_name")
                &&getIntent().hasExtra("Student_Count")) {
            final String class_id = getIntent().getStringExtra("class_id");
            String Institute = getIntent().getStringExtra("institute");
            String uid = getIntent().getStringExtra("uid");
            String batch = getIntent().getStringExtra("batch");
            String class_name = getIntent().getStringExtra("class_name");
            String Student_Count = getIntent().getStringExtra("Student_Count");
            int total_students =  Integer.parseInt(Student_Count);
            View parentLayout = findViewById(R.id.add_students_parent);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Add Students");
            }

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("Users").document(uid).addSnapshotListener((documentSnapshot, e) -> {
                name = documentSnapshot.getString("Name");
            });

            Button selected = findViewById(R.id.selected);
            List<Users> usersList = new ArrayList<>();
            List<String> selectedUid = new ArrayList<>();
            List<String> uidList = new ArrayList<>();
            List<String> StudentName = new ArrayList<>();
            List<String> username = new ArrayList<>();


            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            AddStudentsAdapter mAdapter = new AddStudentsAdapter(this, usersList, selectedUid, uidList, StudentName, username);
            RecyclerView mMessagesList = findViewById(R.id.student_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            mFirestore.collection("Users").whereEqualTo(class_id, "Removed").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()){
                        KToast.errorToast(AddStudents.this, "No Students to be Added", Gravity.CENTER, KToast.LENGTH_SHORT);
                        progressBar.setVisibility(View.GONE);
                    }else {
                        for (final DocumentSnapshot document : task.getResult()) {
                            progressBar.setVisibility(View.GONE);
                            Users users = document.toObject(Users.class);
                            usersList.add(users);
                            Collections.sort(usersList, Users.BY_NAME_ALPHABETICAL);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

            selected.setOnClickListener(v -> {
                if (isInternetAvailable()) {
                    if (selectedUid.size() > 0) {
                        int index;
                        for (index = 0; index < selectedUid.size(); index++) {
                            int finalIndex = index;
                            mFirestore.collection("Users").document(selectedUid.get(index)).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("Teacher_Name", name);
                                    data.put("Total_Present", 0);
                                    data.put("Total_Class", 0);
                                    data.put("Teacher_id", uid);
                                    data.put("Subject_Name", class_name);
                                    document.getReference().update(class_id, "Added");
                                    document.getReference().collection("Subjects").document(class_id).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Map<String, Object> data2 = new HashMap<>();
                                            data2.put("Name", StudentName.get(finalIndex));
                                            data2.put("Username", username.get(finalIndex));
                                            data2.put("Uid", selectedUid.get(finalIndex));
                                            data2.put("Percentage", 0);
                                            mFirestore.collection("Attendance").document(class_id)
                                                    .collection("Students").document(selectedUid.get(finalIndex)).set(data2);
                                        }
                                    });
                                }
                            }).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String new_total_student = String.valueOf(selectedUid.size() + total_students);
                                    Map<String, Object> data3 = new HashMap<>();
                                    data3.put("Total_Students", new_total_student);
                                    mFirestore.collection("Users").document(uid).collection("Subjects").document(class_id).update(data3);
                                    if (selectedUid.size() == 1) {
                                        KToast.successToast(AddStudents.this, selectedUid.size() + " Student Added", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                    }
                                    if (selectedUid.size() > 1) {
                                        KToast.successToast(AddStudents.this, selectedUid.size() + " Students Added", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                    }
                                    startActivity(new Intent(AddStudents.this, MainActivity.class));
                                    finish();
                                }
                            });
                        }
                    } else {
                        KToast.errorToast(AddStudents.this, "No Students Selected", Gravity.CENTER, KToast.LENGTH_SHORT);
                    }
                }else{
                    Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
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

    private void notifyUser(String exception) {
        progressBar.setVisibility(View.GONE);
        KToast.errorToast(AddStudents.this,exception,Gravity.CENTER,KToast.LENGTH_SHORT);
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