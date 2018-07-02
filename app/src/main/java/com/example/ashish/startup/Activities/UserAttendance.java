package com.example.ashish.startup.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.roomorama.caldroid.CaldroidFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.dom.DOMLocator;

public class UserAttendance extends AppCompatActivity {

    private FirebaseFirestore rootRef;
    private FirebaseAuth mAuth;
    String admin_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance);

        if (getIntent().hasExtra("subject_id") && getIntent().hasExtra("subject_name")) {
            final String subject_id = getIntent().getStringExtra("subject_id");
            String subject_name = getIntent().getStringExtra("subject_name");

            mAuth = FirebaseAuth.getInstance();
            rootRef = FirebaseFirestore.getInstance();

            FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail();
            String email_red = email.substring(0, email.length() - 10);


            final ColorDrawable green = new ColorDrawable(Color.GREEN);
            final ColorDrawable blue = new ColorDrawable(Color.BLUE);
            final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            final Date date = new Date();
            String datte = "June 30, 2018 13:26:48";

            rootRef.collection("Users").document("dikshant").collection("Subjects").document(subject_id).collection("Attendance").document(datte).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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

                    final ArrayList<String> list = new ArrayList<>();

                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        String status = document.getString("RahulG@gmail.com");
                        Log.e("status",status + "");
                    }
                }

                });
        }
    }
}
