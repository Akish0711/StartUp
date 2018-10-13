package com.example.ashish.startup.Activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.ashish.startup.Adapters.UpdateAttendanceAdapter;
import com.example.ashish.startup.Models.UpdateAttendanceModel;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class UpdateAttendance extends AppCompatActivity {

    private FirebaseFirestore rootRef;
    private FirebaseAuth mAuth;
    private RecyclerView mAttendanceList ;
    private List<UpdateAttendanceModel> updateAttendanceModelList;
    private UpdateAttendanceAdapter updateAttendanceAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attendance);

        if (getIntent().hasExtra("class_id")) {
            final String class_id = getIntent().getStringExtra("class_id");

            mAuth = FirebaseAuth.getInstance();
            rootRef = FirebaseFirestore.getInstance();

            FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail();
            String email_red = email.substring(0, email.length() - 10);

            mProgressBar = findViewById(R.id.progressBarUpdateAttendance);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mAttendanceList = findViewById(R.id.update_attendance_timestamp_list);
            mAttendanceList.setHasFixedSize(true);
            mAttendanceList.setLayoutManager(layoutManager);

            DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
            mAttendanceList.addItemDecoration(decoration);

            updateAttendanceModelList = new ArrayList<>();
            updateAttendanceAdapter = new UpdateAttendanceAdapter(updateAttendanceModelList,class_id,email_red);
            mAttendanceList.setAdapter(updateAttendanceAdapter);

            final ColorDrawable green = new ColorDrawable(Color.GREEN);
            final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            rootRef.collection("Users").document(email_red).collection("Subjects").document(class_id)
                    .collection("Attendance").get().addOnCompleteListener(task -> {

                final CaldroidFragment caldroidFragment = new CaldroidFragment();
                Bundle args = new Bundle();
                Calendar cal = Calendar.getInstance();
                args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
                args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
               // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

                caldroidFragment.setArguments(args);
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.calendar1, caldroidFragment);
                t.commitAllowingStateLoss();

                final ArrayList<String> list = new ArrayList<>();

                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        String database_date = doc.getId().substring(0, doc.getId().length() - 9);
                        list.add(database_date);
                    }

                    for (int x = 0; x < list.size(); x++) {
                        try {
                            Date database_date = format.parse(list.get(x));

                            caldroidFragment.setBackgroundDrawableForDate(green, database_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    final CaldroidListener listener = new CaldroidListener() {

                        @Override
                        public void onSelectDate(Date date, View view) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            int i = 0;
                            for (int x = 0; x < list.size(); x++) {
                                if (list.get(x).equals(formatter.format(date))) {
                                    i++;
                                }
                            }

                            updateAttendanceModelList.clear();

                            rootRef.collection("Users").document(email_red).collection("Subjects").document(class_id)
                                    .collection("Attendance").whereEqualTo("Date",formatter.format(date)).get()
                                    .addOnCompleteListener(task12 -> {
                                        if(task12.isSuccessful()){
                                            for (final DocumentSnapshot document : task12.getResult()) {
                                                if( document != null && document.exists()) {
                                                    updateAttendanceModelList.add(new UpdateAttendanceModel(document.getId()));
                                                }
                                            }
                                            Collections.sort(updateAttendanceModelList,UpdateAttendanceModel.BY_TIMESTAMP_LATEST);
                                            updateAttendanceAdapter.notifyDataSetChanged();
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });

                            final AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAttendance.this);
                            builder.setTitle(formatter.format(date));
                            builder.setMessage("Look at this dialog!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, id) -> {

                                    });
                            builder.setNegativeButton("Cancel", null);

                            AlertDialog alert = builder.create();
                //            alert.show();
                        }

                        @Override
                        public void onChangeMonth(int month, int year) {
                            String text = "month: " + month + " year: " + year;

                        }

                        @Override
                        public void onLongClickDate(Date date, View view) {

                        }

                        @Override
                        public void onCaldroidViewCreated() {

                        }
                    };
                    caldroidFragment.setCaldroidListener(listener);
                }
            });
        }
    }
}