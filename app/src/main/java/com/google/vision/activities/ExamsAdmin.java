package com.google.vision.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.vision.Adapters.ExamsAdapter;
import com.google.vision.Models.Classes;
import com.google.vision.Models.Exams;
import com.google.vision.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExamsAdmin extends AppCompatActivity {
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_admin);

        if (getIntent().hasExtra("class_id")
                &&getIntent().hasExtra("uid")) {
            String class_id = getIntent().getStringExtra("class_id");
            String uid = getIntent().getStringExtra("uid");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Exams");
            }

            List<Exams> examList = new ArrayList<>();
            List<String> keyList = new ArrayList<>();

            android.support.design.widget.FloatingActionButton add_exam = findViewById(R.id.add_exam);
            parentLayout = findViewById(R.id.exams_admin_parent);

            ExamsAdapter mAdapter = new ExamsAdapter(this, examList, class_id, parentLayout);
            RecyclerView mMainList = findViewById(R.id.student_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(mAdapter);


            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            rootRef.collection("Marks").whereEqualTo("Class_id", class_id).addSnapshotListener((documentSnapshots, e) -> {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    switch (doc.getType()) {
                        case ADDED:
                            String exam_id = doc.getDocument().getId();
                            Exams exams = doc.getDocument().toObject(Exams.class).withID(exam_id);
                            examList.add(exams);
                            keyList.add(exam_id);
                            mAdapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                           /* exam_id = doc.getDocument().getId();
                            int index = keyList.indexOf(exam_id);
                            Exams exams2 = doc.getDocument().toObject(Exams.class).withID(exam_id);
                            examList.set(index, exams2);
                            mAdapter.notifyDataSetChanged();*/
                            break;
                        case REMOVED:
                            exam_id = doc.getDocument().getId();
                            int index2 = keyList.indexOf(exam_id);
                            examList.remove(index2);
                            keyList.remove(index2);
                            mAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            });

            add_exam.setOnClickListener(v -> {
                Intent intent = new Intent(ExamsAdmin.this, AddMarks.class);
                intent.putExtra("class_id", class_id);
                startActivity(intent);
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
