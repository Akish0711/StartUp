package com.example.ashish.startup.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.example.ashish.startup.Adapters.AddMarksAdpater;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;

public class EditMarks extends AppCompatActivity {

    private String class_id, exam_id;
    EditText name_exam, max_marks;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        if (getIntent().hasExtra("class_id")
                && getIntent().hasExtra("exam_id")
                && getIntent().hasExtra("exam_name")) {
            class_id = getIntent().getStringExtra("class_id");
            exam_id = getIntent().getStringExtra("exam_id");
            String exam_name = getIntent().getStringExtra("exam_name");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Edit Exam");
            }

            name_exam = findViewById(R.id.exam_name);
            max_marks = findViewById(R.id.max_marks);

            /*AddMarksAdpater mAdapter = new AddMarksAdpater(this, marksList, uidList, userMarks, nameList, usernameList);
            RecyclerView mMainList = findViewById(R.id.student_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(mAdapter);

            mFirestore.collection("Attendance").document(class_id).collection("Students").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (final DocumentSnapshot document : task.getResult()) {
                        Marks marks = document.toObject(Marks.class);
                        marksList.add(marks);
                        Collections.sort(marksList, Marks.BY_NAME_ALPHABETICAL);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });*/
        }
    }
}
