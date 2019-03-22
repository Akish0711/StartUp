package com.example.ashish.startup.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ashish.startup.Adapters.AttendanceListAdapter;
import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
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
    private String default_time = null;
    private String default_date = null;
    Button mark_present;
    String uid, class_id;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        attendanceList = new ArrayList<>();
        presentList = new ArrayList<>();
        absentList = new ArrayList<>();
        attendanceListAdapter = new AttendanceListAdapter(this,attendanceList, presentList, absentList);

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("uid")) {
            class_id = getIntent().getStringExtra("class_id");
            uid = getIntent().getStringExtra("uid");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Attendance");
            }

            mark_present = findViewById(R.id.mark_present);
            parentLayout = findViewById(R.id.take_attendance_teacher);
            DateFormat buttonTextFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            String buttonText = buttonTextFormat.format(Calendar.getInstance().getTime());
            mark_present.setText("SUBMIT ATTENDANCE\nDATE: "+buttonText);

            RecyclerView mMainList = findViewById(R.id.attendance_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(attendanceListAdapter);

            mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Attendance").document(class_id).collection("Students").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (final DocumentSnapshot document : task.getResult()) {
                        Attendance attendance = document.toObject(Attendance.class);
                        //presentList.add(attendance.getUid());
                        attendanceList.add(attendance);
                        Collections.sort(attendanceList, Attendance.BY_NAME_ALPHABETICAL);
                        attendanceListAdapter.notifyDataSetChanged();
                    }
                }
            });

            mark_present.setOnClickListener(view -> {
                if (isInternetAvailable()) {
                    final int[] counter_total = new int[1];
                    final int[] counter_present = new int[1];
                    DateFormat timeformat = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss", Locale.ENGLISH);
                    DateFormat dateformat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    final String time, date;
                    if (default_time == null) {
                        date = dateformat.format(Calendar.getInstance().getTime());
                        time = timeformat.format(Calendar.getInstance().getTime());
                    } else {
                        date = default_date;
                        time = default_time;
                    }

                    Map<String, String> datedata = new HashMap<>();
                    datedata.put("Date", date);
                    mFirestore.collection("Attendance").document(class_id).collection("Dates")
                            .document(time).set(datedata);

                    for (int index2 = 0; index2 < presentList.size(); index2++) {
                        int finalIndex = index2;
                        mFirestore.collection("Users").document(presentList.get(finalIndex)).collection("Subjects")
                                .document(class_id).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                counter_present[0] = document.getLong("Total_Present").intValue();
                                counter_total[0] = document.getLong("Total_Class").intValue();
                                counter_total[0]++;
                                counter_present[0]++;
                                int percentage = (counter_present[0] * 100 / counter_total[0]);

                                Map<String, Object> data = new HashMap<>();
                                data.put("Total_Present", counter_present[0]);
                                data.put("Total_Class", counter_total[0]);
                                document.getReference().update(data).addOnCompleteListener(task1 -> {
                                    Map<String, Object> data2 = new HashMap<>();
                                    data2.put("Percentage", percentage);
                                    mFirestore.collection("Attendance").document(class_id).collection("Students")
                                            .document(presentList.get(finalIndex)).update(data2);
                                    Map<String, Object> data3 = new HashMap<>();
                                    data3.put(presentList.get(finalIndex), true);

                                    mFirestore.collection("Attendance").document(class_id).collection("Dates")
                                            .document(time).set(data3, SetOptions.merge());
                                });
                            }
                        });
                    }
                    int index;
                    for (index = 0; index < absentList.size(); index++) {
                        int finalIndex = index;
                        mFirestore.collection("Users").document(absentList.get(finalIndex)).collection("Subjects")
                                .document(class_id).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                counter_total[0] = document.getLong("Total_Class").intValue();
                                counter_present[0] = document.getLong("Total_Present").intValue();
                                counter_total[0]++;
                                int percentage = (counter_present[0] * 100 / counter_total[0]);

                                Map<String, Object> data = new HashMap<>();
                                data.put("Total_Class", counter_total[0]);
                                document.getReference().update(data).addOnCompleteListener(task12 -> {
                                    Map<String, Object> data2 = new HashMap<>();
                                    data2.put("Percentage", percentage);
                                    mFirestore.collection("Attendance").document(class_id).collection("Students")
                                            .document(absentList.get(finalIndex)).update(data2);

                                    Map<String, Object> data3 = new HashMap<>();
                                    data3.put(absentList.get(finalIndex), false);
                                    mFirestore.collection("Attendance").document(class_id).collection("Dates")
                                            .document(time).set(data3, SetOptions.merge());
                                });
                            }
                        });
                    }
                    if (absentList.size() == 0) {
                        KToast.successToast(TakeAttendance.this, "Everyone marked present", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                    } else if (index == 1) {
                        KToast.successToast(TakeAttendance.this, absentList.size() + " Student Marked Absent", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                    } else {
                        KToast.successToast(TakeAttendance.this, absentList.size() + " Students Marked Absent", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                    }
                    finish();
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

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        if(id == R.id.select_date){
            final Calendar calendar = Calendar.getInstance();
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(TakeAttendance.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss", Locale.ENGLISH);
                        final SimpleDateFormat formatter2 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                        calendar.set(year,monthOfYear,dayOfMonth);
                        default_time = formatter.format(calendar.getTime());
                        default_date = formatter2.format(calendar.getTime());
                        mark_present.setText("SUBMIT ATTENDANCE\nDATE: "+default_date);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
            return true;
        }

        if (id == R.id.history){
            Intent intent = new Intent(TakeAttendance.this,UpdateAttendance.class);
            intent.putExtra("uid", uid);
            intent.putExtra("class_id", class_id);
            startActivity(intent);
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
