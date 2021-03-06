package com.google.vision.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.vision.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
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

public class UserAttendance extends AppCompatActivity {

    private static final String TAG = "MYMYMYMY";
    private TextView default_text, total_lectures, attended_lectures, bunked_lectures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance);

        if (getIntent().hasExtra("subject_id") && getIntent().hasExtra("subject_name") && getIntent().hasExtra("Teacher_Name")) {
            final String subject_id = getIntent().getStringExtra("subject_id");
            String subject_name = getIntent().getStringExtra("subject_name");
            String Teacher_Name = getIntent().getStringExtra("Teacher_Name");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Attendance");
            }

            default_text = findViewById(R.id.defult_text);
            total_lectures = findViewById(R.id.total_lectures);
            attended_lectures = findViewById(R.id.attended_lectures);
            bunked_lectures = findViewById(R.id.bunked_lectures);

            total_lectures.setVisibility(View.INVISIBLE);
            attended_lectures.setVisibility(View.INVISIBLE);
            bunked_lectures.setVisibility(View.INVISIBLE);

            //final ColorDrawable green = new ColorDrawable(Color.rgb(139, 194, 74));
            final ColorDrawable green = new ColorDrawable(getResources().getColor(R.color.custom_green));
            final ColorDrawable red = new ColorDrawable(getResources().getColor(R.color.custom_red));

            //final ColorDrawable red = new ColorDrawable(Color.RED);

            final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            final String uid = user.getUid();

            rootRef.collection("Attendance").document(subject_id).collection("Dates").get().addOnCompleteListener(task -> {
                final CaldroidFragment caldroidFragment = new CaldroidFragment();

                Bundle args = new Bundle();
                Calendar cal = Calendar.getInstance();
                args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
                args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
                caldroidFragment.setArguments(args);

                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.calendar2, caldroidFragment);
                t.commit();

                final ArrayList<String> list_present = new ArrayList<>();
                final ArrayList<String> list_absent = new ArrayList<>();
                final ArrayList<String> list = new ArrayList<>();

                if (task.isSuccessful()){
                    for(DocumentSnapshot document:task.getResult()) {
                        String database__all_date = document.getId().substring(0, document.getId().length() - 9);
                        list.add(database__all_date);

                        Object o = document.get(FieldPath.of(uid));
                        final Boolean status = (Boolean) o;
                        try {
                            if (status) {
                                String database_date = document.getId().substring(0, document.getId().length() - 9);
                                list_present.add(database_date);
                            } else {
                                String database_date = document.getId().substring(0, document.getId().length() - 9);
                                list_absent.add(database_date);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < list_present.size(); x++) {
                            try {
                                Date database_date = format.parse(list_present.get(x));
                                caldroidFragment.setBackgroundDrawableForDate(green, database_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                        final CaldroidListener listener = new CaldroidListener() {
                            @Override
                            public void onSelectDate(Date date, View view) {
                                default_text.setVisibility(View.INVISIBLE);
                                total_lectures.setVisibility(View.VISIBLE);
                                attended_lectures.setVisibility(View.VISIBLE);
                                bunked_lectures.setVisibility(View.VISIBLE);

                                int i = 0,p=0;
                                for (int x = 0; x < list.size(); x++) {
                                    if (list.get(x).equals(formatter.format(date))) {
                                        i++;
                                    }
                                }
                                for(int k=0;k < list_present.size();k++){
                                    if(list_present.get(k).equals(formatter.format(date)))
                                        p++;
                                }

                                int z = i-p;
                                total_lectures.setText("Total Lectures: " + i);
                                attended_lectures.setText("Attended Lectures: " + p);
                                bunked_lectures.setText("Bunked Lectures: " + z);
                            }
                        };

                        caldroidFragment.setCaldroidListener(listener);

                        for (int y = 0; y < list_absent.size(); y++) {
                            try {
                                Date database_date = format.parse(list_absent.get(y));
                                caldroidFragment.setBackgroundDrawableForDate(red, database_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}