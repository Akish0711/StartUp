package com.example.ashish.startup.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ashish.startup.R;
import com.roomorama.caldroid.CaldroidFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserAttendance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance);

        if (getIntent().hasExtra("subject_id") && getIntent().hasExtra("subject_name")) {
            final String subject_id = getIntent().getStringExtra("subject_id");
            String subject_name = getIntent().getStringExtra("subject_name");

            final ColorDrawable green = new ColorDrawable(Color.GREEN);
            final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

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
        }
    }
}
