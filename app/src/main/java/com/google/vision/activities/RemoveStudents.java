package com.google.vision.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.vision.Adapters.RemoveStudentsAdapter;
import com.google.vision.Models.Users;
import com.google.vision.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveStudents extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private RemoveStudentsAdapter removeStudentsAdapter;
    private int total_students;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_students);

        if (getIntent().hasExtra("class_id") && getIntent().hasExtra("institute")) {
            final String class_id = getIntent().getStringExtra("class_id");
            String uid = getIntent().getStringExtra("uid");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Remove Students");
            }

            Button remove_students = findViewById(R.id.remove_students);
            parentLayout = findViewById(R.id.remove_students_parent);
            List<Users> usersList = new ArrayList<>();
            List<String> selectedUid = new ArrayList<>();

            removeStudentsAdapter = new RemoveStudentsAdapter(this, usersList, selectedUid);
            RecyclerView mMainList = findViewById(R.id.remove_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(removeStudentsAdapter);

            mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Users").document(uid).collection("Subjects").document(class_id).addSnapshotListener((documentSnapshot, e) -> {
                String total_students_text = documentSnapshot.getString("Total_Students");
                total_students = Integer.parseInt(total_students_text);
            });

            mFirestore.collection("Users").whereEqualTo(class_id, "Added").addSnapshotListener((documentSnapshots, e) -> {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    switch (doc.getType()) {
                        case ADDED:
                            Users users = doc.getDocument().toObject(Users.class);
                            usersList.add(users);
                            Collections.sort(usersList, Users.BY_NAME_ALPHABETICAL);
                            removeStudentsAdapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
            });

            remove_students.setOnClickListener(v -> {
                if (isInternetAvailable()) {
                    if (selectedUid.size() > 0) {
                        int index;
                        for (index = 0; index < selectedUid.size(); index++) {
                            int finalIndex = index;
                            mFirestore.collection("Users").whereEqualTo("Uid", selectedUid.get(index)).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> data = new HashMap<>();
                                        data.put(class_id, "Removed");
                                        document.getReference().update(data);
                                        document.getReference().collection("Subjects").document(class_id).delete();
                                        mFirestore.collection("Attendance").document(class_id).collection("Students").document(selectedUid.get(finalIndex)).delete();
                                    }
                                }
                            });
                        }

                        String new_total_student = String.valueOf(total_students - selectedUid.size());
                        Map<String, Object> data3 = new HashMap<>();
                        data3.put("Total_Students", new_total_student);
                        mFirestore.collection("Users").document(uid).collection("Subjects").document(class_id).update(data3);

                        if (selectedUid.size() == 1) {
                            KToast.successToast(RemoveStudents.this, selectedUid.size() + " Student Removed", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        }
                        if (selectedUid.size() > 1) {
                            KToast.successToast(RemoveStudents.this, selectedUid.size() + " Students Removed", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        }
                        finish();
                        startActivity(new Intent(RemoveStudents.this, MainActivity.class));
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
