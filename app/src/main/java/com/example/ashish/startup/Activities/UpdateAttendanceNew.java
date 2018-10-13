package com.example.ashish.startup.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.example.ashish.startup.Adapters.UpdateAttendanceListAdapter;
import com.example.ashish.startup.Models.UpdateAttendanceModel;
import com.example.ashish.startup.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateAttendanceNew extends AppCompatActivity {

    private String email_red;
    private List<UpdateAttendanceModel> updateAttendanceModelList;
    private UpdateAttendanceListAdapter updateAttendanceListAdapter;
    private RecyclerView recyclerView;
    private Map<String, Object> hm ;

    private FirebaseFirestore rootRef;
    private String timeStamp;
    private String classId;
    private Button updateAttendanceButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_attendance_alert_dialog_view);

        if (getIntent().hasExtra("classId") && getIntent().hasExtra("email_red") && getIntent().hasExtra("timeStamp")) {
            classId = getIntent().getStringExtra("classId");
            email_red = getIntent().getStringExtra("email_red");
            timeStamp = getIntent().getStringExtra("timeStamp");

            rootRef = FirebaseFirestore.getInstance();

            Toolbar toolbar = findViewById(R.id.update_attendance_toolbar);
            setSupportActionBar(toolbar);


            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Update Attendance List");
                getSupportActionBar().setSubtitle("Click on the card to edit.");
            }

            updateAttendanceButton = findViewById(R.id.updateAttendanceButton);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView = findViewById(R.id.alertDialogRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);

//            DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
//            recyclerView.addItemDecoration(decoration);

            recyclerView.setAdapter(null);

            updateAttendanceModelList = new ArrayList<>();

            rootRef.collection("Users").document(email_red).collection("Subjects")
                    .document(classId).collection("Attendance")
                    .document(timeStamp).get().addOnSuccessListener(documentSnapshot -> {
                if( documentSnapshot != null && documentSnapshot.exists()) {
                    hm = documentSnapshot.getData();
                    updateAttendanceListAdapter = new UpdateAttendanceListAdapter(hm,email_red,classId,
                            timeStamp,updateAttendanceButton);
                    recyclerView.setAdapter(updateAttendanceListAdapter);
                    updateAttendanceListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
