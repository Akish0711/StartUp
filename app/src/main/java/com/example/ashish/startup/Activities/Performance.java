package com.example.ashish.startup.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.ashish.startup.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Performance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        if (getIntent().hasExtra("uid")) {
            String uid = getIntent().getStringExtra("uid");

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Performance");
            }

            BarChart chart = findViewById(R.id.chart);
            chart.setPinchZoom(false);
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.getAxisLeft().setEnabled(false);
            chart.getAxisRight().setEnabled(false);
            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.setTouchEnabled(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setAxisMinimum(1);

            List<BarEntry> entries = new ArrayList<>();

            final int[] i = {0};

            rootRef.collection("Users").document(uid).collection("Performance").orderBy("Year").get().addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    for (final DocumentSnapshot document : task2.getResult()) {
                        long year = document.getLong("Year");
                        long total_students = document.getLong("Total_Students");
                        entries.add(new BarEntry(year, total_students));
                        i[0]++;
                    }
                }
            }).addOnCompleteListener(task -> {
                BarDataSet set = new BarDataSet(entries, "");

                BarData data = new BarData(set);
                data.setBarWidth(0.7f); // set custom bar width
                chart.setData(data);
                chart.animateY(3000 , Easing.EasingOption.EaseOutBack );
                chart.setFitBars(true); // make the x-axis fit exactly all bars
                chart.invalidate();
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
