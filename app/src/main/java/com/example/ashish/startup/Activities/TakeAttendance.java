package com.example.ashish.startup.Activities;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
    private List<String> presentList;
    private List<String> absentList;
    private FirebaseAuth mAuth;
    private int mYear, mMonth, mDay;
    private String datee = null;
    private String datee2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        mAuth = FirebaseAuth.getInstance();
        attendanceList = new ArrayList<>();
        presentList = new ArrayList<>();
        absentList = new ArrayList<>();
        attendanceListAdapter = new AttendanceListAdapter(this,attendanceList, presentList, absentList);

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("institute")) {
            final String class_id = getIntent().getStringExtra("class_id");
            final String Institute = getIntent().getStringExtra("institute");
            String email_red = getIntent().getStringExtra("username");

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

            mFirestore.collection("Users").whereEqualTo(class_id,"Added").addSnapshotListener((documentSnapshots, e) -> {
                for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    switch (doc.getType()) {
                        case ADDED:
                            Attendance attendance = doc.getDocument().toObject(Attendance.class);
                            attendanceList.add(attendance);
                            presentList.add(attendance.getUsername());
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
                final int[] counter_total = new int[1];
                final int[] counter_present = new int[1];
                DateFormat df1 = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss", Locale.ENGLISH);
                DateFormat df2 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                final String time,date;
                if(datee == null){
                    date = df2.format(Calendar.getInstance().getTime());
                    time = df1.format(Calendar.getInstance().getTime());
                }
                else{
                    date = datee2;
                    time = datee;
                }

                Map<String,String> d = new HashMap<>();
                d.put("Date",date);
                mFirestore.collection("Users").document(email_red).collection("Subjects")
                        .document(class_id).collection("Attendance").document(time).set(d);

                for (int index2 = 0; index2 < presentList.size(); index2++) {
                    int finalIndex = index2;
                    mFirestore.collection("Users").document(presentList.get(index2))
                            .collection("Subjects").document(class_id).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

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
                            data2.put(presentList.get(finalIndex), true );


                            mFirestore.collection("Users").document(email_red).collection("Subjects")
                                    .document(class_id).collection("Attendance").document(time).set(data2, SetOptions.merge());
                        }
                    });
                }
                int index;
                for (index = 0; index < absentList.size(); index++) {
                    int finalIndex = index;
                    mFirestore.collection("Users").document(absentList.get(index)).collection("Subjects")
                            .document(class_id).get().addOnCompleteListener(task -> {
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
                            data2.put(absentList.get(finalIndex), false );
                            mFirestore.collection("Users").document(email_red).collection("Subjects")
                                    .document(class_id).collection("Attendance").document(time).set(data2, SetOptions.merge());
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
                finish();
            });
        }
    }
    @TargetApi(24)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        if(id == R.id.select_date){
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) ->
                    {
                        final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss", Locale.ENGLISH);
                        final SimpleDateFormat formatter2 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                        c.set(year,monthOfYear,dayOfMonth);
                        datee = formatter.format(c.getTime());
                        datee2 = formatter2.format(c.getTime());
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.attendance_menu, menu);
        return true;
    }
}
