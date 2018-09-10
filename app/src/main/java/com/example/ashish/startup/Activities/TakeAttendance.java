package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.ashish.startup.Adapters.AttendanceListAdapter;
import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeAttendance extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private AttendanceListAdapter attendanceListAdapter;
    private List<Attendance> attendanceList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        mAuth = FirebaseAuth.getInstance();
        attendanceList = new ArrayList<>();
        attendanceListAdapter = new AttendanceListAdapter(this,attendanceList);

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("institute")&& getIntent().hasExtra("username")) {
            final String class_id = getIntent().getStringExtra("class_id");
            final String Institute = getIntent().getStringExtra("institute");
            final String email_red = getIntent().getStringExtra("username");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Take Attendance");
            }

            Button view_attendance = findViewById(R.id.view_attendance);
            Button update_attendance = findViewById(R.id.update_attendance);
            Button mark_present = findViewById(R.id.mark_present);

            RecyclerView mMainList = findViewById(R.id.attendance_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(attendanceListAdapter);

            mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Students").addSnapshotListener((documentSnapshots, e) -> {
                for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    switch (doc.getType()) {
                        case ADDED:
                            Attendance attendance = doc.getDocument().toObject(Attendance.class);
                            attendanceList.add(attendance);
                            Collections.sort(attendanceList, Attendance.BY_NAME_ALPHABETICAL);
                            attendanceListAdapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
            });

            view_attendance.setOnClickListener(view -> {
                Intent intent = new Intent(TakeAttendance.this,ViewAttendance.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",Institute);
                startActivity(intent);
            });

            update_attendance.setOnClickListener(view -> {
                Intent intent = new Intent(TakeAttendance.this,UpdateAttendance.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",Institute);
                startActivity(intent);
            });

            mark_present.setOnClickListener(view -> {
                String email = mAuth.getCurrentUser().getEmail();
                final String email_red1 = email.substring(0, email.length() - 10);
                final int[] counter_total = new int[1];
                final int[] counter_present = new int[1];
                DateFormat df1=new SimpleDateFormat("MMMM d, yyyy HH:mm:ss", Locale.ENGLISH);
                final String time=df1.format(Calendar.getInstance().getTime());
                List list = attendanceListAdapter.getSelectedItem();
                List list2 = attendanceListAdapter.getUnselectedItem();
                int index2;
                for (index2 = 0; index2 < list2.size(); index2++) {
                    final Attendance model = (Attendance) list2.get(index2);
                    mFirestore.collection("Users").document(model.getUsername()).collection("Subjects").document(class_id).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            counter_total[0] = document.getLong("Total_Class").intValue();
                            counter_present[0] = document.getLong("Total_Present").intValue();
                            counter_total[0]++;
                            counter_present[0]++;
                            int percentage = (counter_present[0]*100/counter_total[0]);

                            Map<String, Object> data = new HashMap<>();
                            data.put("Total_Present",counter_present[0]);
                            data.put("Total_Class",counter_total[0]);
                            data.put("Percentage",percentage);
                            document.getReference().update(data);

                            Map<String, Object> data2 = new HashMap<>();
                            data2.put(model.getUsername(), true );
                            Map<String,Object> userData = new HashMap<>();
                            userData.put(time,true);
                            mFirestore.collection("Users").document(email_red1).collection("Subjects").document(class_id).collection("Attendance").document(time).set(data2, SetOptions.merge());
                        }
                    });
                }
                int index;
                for (index = 0; index < list.size(); index++) {
                    final Attendance model = (Attendance) list.get(index);
                    mFirestore.collection("Users").document(model.getUsername()).collection("Subjects").document(class_id).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            counter_total[0] = document.getLong("Total_Class").intValue();
                            counter_present[0] = document.getLong("Total_Present").intValue();
                            counter_total[0]++;
                            int percentage = (counter_present[0]*100/counter_total[0]);

                            Map<String, Object> data = new HashMap<>();
                            data.put("Total_Class",counter_total[0]);
                            data.put("Percentage",percentage);
                            document.getReference().update(data);

                            Map<String, Object> data2 = new HashMap<>();
                            data2.put(model.getUsername(), false );
                            mFirestore.collection("Users").document(email_red1).collection("Subjects").document(class_id).collection("Attendance").document(time).set(data2, SetOptions.merge());
                        }
                    });
                }
                if (index == 0) {
                    Toast.makeText(TakeAttendance.this, "Everyone marked present", Toast.LENGTH_LONG).show();
                }
                else if (index == 1) {
                    Toast.makeText(TakeAttendance.this, index + " Student Marked Absent", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(TakeAttendance.this, index + " Students Marked Absent", Toast.LENGTH_LONG).show();
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
