package com.example.ashish.startup.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ashish.startup.Adapters.AttendanceListAdapter;
import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeAttendance extends AppCompatActivity {

    private RecyclerView mMainList;
    private FirebaseFirestore mFirestore;
    private AttendanceListAdapter attendanceListAdapter;
    private List<Attendance> attendanceList;
    private Button mark_present,view_attendance, update_attendance;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        mAuth = FirebaseAuth.getInstance();

        attendanceList = new ArrayList<>();
        attendanceListAdapter = new AttendanceListAdapter(this,attendanceList);

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("institute")) {
            final String class_id = getIntent().getStringExtra("class_id");
            final String Institute = getIntent().getStringExtra("institute");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Take Attendance");
            }

            view_attendance = (Button)findViewById(R.id.view_attendance);
            update_attendance = (Button)findViewById(R.id.update_attendance);
            mark_present = (Button)findViewById(R.id.mark_present);
            progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setScaleY(2f);
            dialog = new ProgressDialog(this);


            mMainList = (RecyclerView) findViewById(R.id.attendance_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(attendanceListAdapter);

            mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute+"_No").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().collection("Subjects").document(class_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            document.getReference().getParent().getParent().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        progressBar.setVisibility(View.GONE);
                                                        DocumentSnapshot doc = task.getResult();
                                                        Attendance attendance = doc.toObject(Attendance.class);
                                                        attendanceList.add(attendance);
                                                        Collections.sort(attendanceList, Attendance.BY_NAME_ALPHABETICAL);

                                                        attendanceListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });

            view_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TakeAttendance.this,ViewAttendance.class);
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("institute",Institute);
                    startActivity(intent);
                }
            });

            update_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TakeAttendance.this,UpdateAttendance.class);
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("institute",Institute);
                    startActivity(intent);
                }
            });



            mark_present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.setMessage("Submitting Attendance");
                    dialog.show();
                    String email = mAuth.getCurrentUser().getEmail();
                    final String email_red = email.substring(0, email.length() - 10);
                    final int[] counter_total = new int[1];
                    final int[] counter_present = new int[1];
                    DateFormat df1=new SimpleDateFormat("MMMM d, yyyy HH:mm:ss", Locale.ENGLISH);
                    final String time=df1.format(Calendar.getInstance().getTime());
                    List list = attendanceListAdapter.getSelectedItem();
                    List list2 = attendanceListAdapter.getUnselectedItem();
                        int index2 = 0;
                        for (index2 = 0; index2 < list2.size(); index2++) {
                            final Attendance model = (Attendance) list2.get(index2);
                            mFirestore.collection("Users").whereEqualTo("Username", model.getUsername()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (final DocumentSnapshot document : task.getResult()) {
                                             document.getReference().collection("Subjects").document(class_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                     if (task.isSuccessful()) {
                                                         DocumentSnapshot document = task.getResult();
                                                         counter_total[0] = document.getLong("Total_Class").intValue();
                                                         counter_present[0] = document.getLong("Total_Present").intValue();
                                                         if (counter_total[0] == 0) {
                                                             Map<String, Object> data = new HashMap<>();
                                                             data.put("Total_Present", 1);
                                                             data.put("Total_Class",1);
                                                             data.put("Percentage",100);
                                                             document.getReference().update(data);
                                                         } else {
                                                             counter_total[0]++;
                                                             counter_present[0]++;
                                                             int percentage = (counter_present[0]*100/counter_total[0]);
                                                             Map<String, Object> data = new HashMap<>();
                                                             data.put("Total_Present", counter_present[0]);
                                                             data.put("Total_Class", counter_total[0]);
                                                             data.put("Percentage",percentage);
                                                             document.getReference().update(data);
                                                         }
                                                         Map<String, Object> data = new HashMap<>();
                                                         data.put(model.getUsername(), true );
                                                         mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Attendance").document(time).set(data, SetOptions.merge());
                                                     }
                                                 }
                                             });
                                        }
                                    }
                                }
                            });
                        }
                    int index = 0;
                    for (index = 0; index < list.size(); index++) {
                        final Attendance model = (Attendance) list.get(index);
                        mFirestore.collection("Users").whereEqualTo("Username", model.getUsername()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (final DocumentSnapshot document : task.getResult()) {
                                        document.getReference().collection("Subjects").document(class_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    counter_total[0] = document.getLong("Total_Class").intValue();
                                                    counter_present[0] = document.getLong("Total_Present").intValue();
                                                    if (counter_total[0] == 0) {
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("Total_Class",1);
                                                        data.put("Percentage",0);
                                                        document.getReference().update(data);
                                                    } else {
                                                        counter_total[0]++;
                                                        int percentage = (counter_present[0]*100/counter_total[0]);
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("Total_Class",counter_total[0]);
                                                        data.put("Percentage",percentage);
                                                        document.getReference().update(data);
                                                    }
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put(model.getUsername(), false );
                                                    mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Attendance").document(time).set(data, SetOptions.merge());
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                    Runnable progressRunnable = new Runnable() {

                        @Override
                        public void run() {
                            dialog.cancel();
                        }
                    };

                    Handler pdCanceller = new Handler();
                    pdCanceller.postDelayed(progressRunnable, 3000);
                    if (index == 0) {
                        Toast.makeText(TakeAttendance.this, "Everyone marked present", Toast.LENGTH_LONG).show();
                    }
                    else if (index == 1) {
                        Toast.makeText(TakeAttendance.this, index + " Student Marked Absent", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(TakeAttendance.this, index + " Students Marked Absent", Toast.LENGTH_LONG).show();
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
