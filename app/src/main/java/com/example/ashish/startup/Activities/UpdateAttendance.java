package com.example.ashish.startup.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ashish.startup.Adapters.AttendanceListAdapter;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

    private FirebaseFirestore rootRef;
    private FirebaseAuth mAuth;
    private TextView  classes_taken;
    AttendanceListAdapter attendanceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attendance);

        final LayoutInflater layoutInflater = LayoutInflater.from(this);

        if (getIntent().hasExtra("class_id")) {
            final String class_id = getIntent().getStringExtra("class_id");

            classes_taken = findViewById(R.id.classes_taken);

            mAuth = FirebaseAuth.getInstance();
            rootRef = FirebaseFirestore.getInstance();

            FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail();
            String email_red = email.substring(0, email.length() - 10);

            final ColorDrawable green = new ColorDrawable(Color.GREEN);
            final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            rootRef.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    t.replace(R.id.calendar1, caldroidFragment);
                    t.commitAllowingStateLoss();

                    final ArrayList<String> list = new ArrayList<>();

                    if (task.isSuccessful()){
                        for (DocumentSnapshot document : task.getResult()){
                            String database_date = document.getId().substring(0, document.getId().length() - 9);
                            list.add(database_date);
                        }

                        for (int x=0; x<list.size();x++) {
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
                                int i=0;
                                for (int x=0; x<list.size();x++) {
                                    if (list.get(x).equals(formatter.format(date))){
                                        i++;
                                    }
                                }
                                classes_taken.setText("Total Lectures: "+i);
                             //   selected_date.setText("Long Press to update.");
                            }

                            @Override
                            public void onChangeMonth(int month, int year) {
                                String text = "month: " + month + " year: " + year;

                            }

                            @Override
                            public void onLongClickDate(Date date, View view) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAttendance.this  );
                                builder.setTitle(formatter.format(date));
                                builder.setMessage("Look at this dialog!")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                            }
                                        });
                                final View attnView = layoutInflater.inflate(R.layout.add_students,null);

                                EditText editCountry =(EditText)attnView.findViewById(R.id.editCountry);
                                EditText editYear =(EditText)attnView.findViewById(R.id.editYear);

                                editCountry.setText("India");
                                editYear.setText("2018");

                                builder.setView(attnView);
                                builder.setNegativeButton("Cancel",null);

                               // builder.setIcon(R.drawable.record);
                                AlertDialog alert = builder.create();
                                alert.show();


                            }

                            @Override
                            public void onCaldroidViewCreated() {

                            }
                        };

                        caldroidFragment.setCaldroidListener(listener);
                    }
                }
            });
        }
    }
}