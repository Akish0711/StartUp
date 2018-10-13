package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;

import com.example.ashish.startup.Adapters.RemoveStudentsAdapter;
import com.example.ashish.startup.Models.Users;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveStudents extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private RemoveStudentsAdapter removeStudentsAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_students);

        if (getIntent().hasExtra("class_id") && getIntent().hasExtra("institute")) {
            final String class_id = getIntent().getStringExtra("class_id");
            String email_red = getIntent().getStringExtra("username");
            String total_students_text = getIntent().getStringExtra("total_students");
            int total_students = Integer.parseInt(total_students_text);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Remove Students");
            }

            Button remove_students = findViewById(R.id.remove_students);
            List<Users> usersList = new ArrayList<>();
            List<String> selectedUsername = new ArrayList<>();
            List<String> selectedName = new ArrayList<>();
            removeStudentsAdapter = new RemoveStudentsAdapter(this, usersList, selectedUsername, selectedName);
            RecyclerView mMainList = findViewById(R.id.remove_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(removeStudentsAdapter);

            mFirestore = FirebaseFirestore.getInstance();

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
                if (selectedUsername.size()>0){
                    int index;
                    for (index=0;index<selectedUsername.size();index++){
                        int finalIndex = index;
                        mFirestore.collection("Users").whereEqualTo("Username", selectedUsername.get(index)).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put(class_id,"Removed");
                                    document.getReference().update(data);

                                    String new_total_student = String.valueOf(total_students-selectedUsername.size());
                                    Map<String, Object> data3 = new HashMap<>();
                                    data3.put("Total_Students", new_total_student);
                                    mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).update(data3);

                                    document.getReference().collection("Subjects").document(class_id).delete().addOnCompleteListener(task1 -> {
                                        if (finalIndex == 1) {
                                            KToast.successToast(RemoveStudents.this, finalIndex + " Student Removed",Gravity.BOTTOM ,KToast.LENGTH_SHORT);
                                        }
                                        if (finalIndex > 1) {
                                            KToast.successToast(RemoveStudents.this, finalIndex + " Students Removed",Gravity.BOTTOM ,KToast.LENGTH_SHORT);
                                        }
                                        finish();
                                        startActivity(new Intent(RemoveStudents.this, MainActivity.class));
                                    });
                                }
                            }
                        });
                    }
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
