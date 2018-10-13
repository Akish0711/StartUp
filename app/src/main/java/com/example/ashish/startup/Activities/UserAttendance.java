package com.example.ashish.startup.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class UserAttendance extends AppCompatActivity {

    private static final String TAG = "MYMYMYMY";
    private FirebaseFirestore rootRef;
    private FirebaseAuth mAuth;
    private TextView classes_taken_2 , classes_attended;
    Collection<String> present = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance);

        if (getIntent().hasExtra("subject_id") && getIntent().hasExtra("subject_name") && getIntent().hasExtra("Teacher_Name")) {
            final String subject_id = getIntent().getStringExtra("subject_id");
            String subject_name = getIntent().getStringExtra("subject_name");
            String Teacher_Name = getIntent().getStringExtra("Teacher_Name");

            classes_taken_2 = findViewById(R.id.classes_taken_2);
            classes_attended = findViewById(R.id.classes_attended);

            final ColorDrawable green = new ColorDrawable(Color.GREEN);
            final ColorDrawable red = new ColorDrawable(Color.RED);

            final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            mAuth = FirebaseAuth.getInstance();
            rootRef = FirebaseFirestore.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            final String email = user.getEmail();

            rootRef.collection("Users").document(Teacher_Name).collection("Subjects").document(subject_id).collection("Attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    final CaldroidFragment caldroidFragment = new CaldroidFragment();

                    Bundle args = new Bundle();
                    Calendar cal = Calendar.getInstance();
                    args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
                    args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                    args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
                    caldroidFragment.setArguments(args);

                    android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                    t.replace(R.id.calendar2, caldroidFragment);
                    t.commit();

                    final ArrayList<String> list_present = new ArrayList<>();
                    final ArrayList<String> list_absent = new ArrayList<>();
                    final ArrayList<String> list = new ArrayList<>();

                    if (task.isSuccessful()){
                        for(DocumentSnapshot document:task.getResult()) {
                            String database__all_date = document.getId().substring(0, document.getId().length() - 9);
                            list.add(database__all_date);

                            Object o = document.get(FieldPath.of(email));
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
                                    classes_taken_2.setText("Total Lectures: " + i);
                                    classes_attended.setText("Present: " + p);
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
                }
            });
        }

    }
}