package com.google.vision.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.vision.Adapters.StudentExamAdapter;
import com.google.vision.Models.Exams;
import com.google.vision.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExamStudent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_student);
        if (getIntent().hasExtra("class_id")
            && getIntent().hasExtra("uid")) {
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

            StudentExamAdapter mAdapter = new StudentExamAdapter(this, examList, class_id, uid);
            RecyclerView mMainList = findViewById(R.id.student_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(mAdapter);


            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            rootRef.collection("Marks").document(class_id).collection("Exams").orderBy("TimeStamp").addSnapshotListener((documentSnapshots, e) -> {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    switch (doc.getType()) {
                        case ADDED:
                            String exam_id = doc.getDocument().getId();
                            Exams exams = doc.getDocument().toObject(Exams.class).withID(exam_id);
                            examList.add(exams);
                            //keyList.add(class_id);
                            mAdapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                           /* class_id = doc.getDocument().getId();
                            int index = keyList.indexOf(class_id);
                            Classes classes2 = doc.getDocument().toObject(Classes.class).withID(class_id);
                            classesList.set(index, classes2);
                            classesListAdapter.notifyDataSetChanged();*/
                            break;
                        case REMOVED:
                            /*class_id = doc.getDocument().getId();
                            int index2 = keyList.indexOf(class_id);
                            classesList.remove(index2);
                            keyList.remove(index2);
                            classesListAdapter.notifyDataSetChanged();*/
                            break;
                    }
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

