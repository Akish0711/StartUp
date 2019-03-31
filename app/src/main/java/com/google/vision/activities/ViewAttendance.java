package com.google.vision.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.vision.Adapters.StatusListAdapter;
import com.google.vision.Models.Status;
import com.google.vision.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewAttendance extends AppCompatActivity {

    private RecyclerView mMainList;
    private FirebaseFirestore mFirestore;
    private List<Status> statusList;
    private StatusListAdapter statusListAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        statusList = new ArrayList<>();
        statusListAdapter = new StatusListAdapter(statusList);

        if (getIntent().hasExtra("class_id") && getIntent().hasExtra("institute")) {
            final String class_id = getIntent().getStringExtra("class_id");
            final String Institute = getIntent().getStringExtra("institute");

            Toolbar toolbar = findViewById(R.id.view_attendance_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("View Attendance");
            }

            progressBar = findViewById(R.id.progressBarUpdateAttendance);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setScaleY(2f);

            mMainList = findViewById(R.id.attendance_details_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(statusListAdapter);

            mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute+"_No").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (final DocumentSnapshot document : task.getResult()) {
                        document.getReference().collection("Subjects").document(class_id).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot doc = task1.getResult();
                                if (doc != null && doc.exists()) {
                                    progressBar.setVisibility(View.GONE);
                                    Status status = doc.toObject(Status.class);
                                    statusList.add(status);
                                    Collections.sort(statusList, Status.BY_NAME_ALPHABETICAL);
                                    statusListAdapter.notifyDataSetChanged();
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
