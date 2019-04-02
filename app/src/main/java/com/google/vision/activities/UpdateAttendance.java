package com.google.vision.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.vision.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateAttendance extends AppCompatActivity {

    private TextView total_lectures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attendance);

        if (getIntent().hasExtra("class_id") && getIntent().hasExtra("uid")) {
            String class_id = getIntent().getStringExtra("class_id");
            String uid = getIntent().getStringExtra("uid");

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            ProgressBar mProgressBar = findViewById(R.id.progressBarUpdateAttendance);
            total_lectures = findViewById(R.id.lectures_taken);

            mProgressBar.setVisibility(View.INVISIBLE);

            final ColorDrawable green = new ColorDrawable(Color.rgb(139, 194, 74));
            final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            final CaldroidFragment caldroidFragment = new CaldroidFragment();
            final ArrayList<String> list = new ArrayList<>();

            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();

            rootRef.collection("Attendance").document(class_id).collection("Dates").get().addOnCompleteListener(task -> {
                args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
                args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
                //args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

                caldroidFragment.setArguments(args);
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.calendar1, caldroidFragment);
                t.commitAllowingStateLoss();

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
                }
            });

            final CaldroidListener listener = new CaldroidListener() {
                @Override
                public void onSelectDate(Date date, View view) {
                    int i = 0;
                    for (int x = 0; x < list.size(); x++) {
                        if (list.get(x).equals(formatter.format(date))) {
                            i++;
                        }
                    }
                    total_lectures.setText("Lectures taken on "+formatter.format(date)+": " + i);
                }
            };
            caldroidFragment.setCaldroidListener(listener);
        }
    }
}