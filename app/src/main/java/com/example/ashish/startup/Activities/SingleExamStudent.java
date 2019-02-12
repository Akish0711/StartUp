package com.example.ashish.startup.activities;

import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SingleExamStudent extends AppCompatActivity {

    long Max, Avg, Marks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_exam_student);

        if (getIntent().hasExtra("class_id")
                && getIntent().hasExtra("exam_id")
                && getIntent().hasExtra("exam_name")
                && getIntent().hasExtra("uid")) {
            String class_id = getIntent().getStringExtra("class_id");
            String exam_id = getIntent().getStringExtra("exam_id");
            String uid = getIntent().getStringExtra("uid");
            String exam_name = getIntent().getStringExtra("exam_name");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(exam_name);
            }

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            BarChart chart = findViewById(R.id.chart);
            chart.setPinchZoom(false);
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.getXAxis().setDrawLabels(false);
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

            mFirestore.collection("Marks").document(class_id).collection("Exams").document(exam_id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Max = document.getLong("Highest");
                    Avg = document.getLong("Average");
                }
            }).addOnCompleteListener(task -> mFirestore.collection("Marks").document(class_id).collection("Exams").document(exam_id).collection("Students").document(uid).get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    Marks = document1.getLong("Marks");

                    List<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f, Max));
                    entries.add(new BarEntry(1f, Avg));
                    entries.add(new BarEntry(2f, Marks));

                    BarDataSet set = new BarDataSet(entries, "");

                    BarData data = new BarData(set);
                    data.setBarWidth(0.9f); // set custom bar width
                    set.setColors(new int[]{R.color.bg_screen2 , R.color.bg_screen1, R.color.bg_screen4},getApplicationContext());
                    chart.setData(data);
                    chart.animateY(3000 , Easing.EasingOption.EaseOutBack );
                    chart.setFitBars(true); // make the x-axis fit exactly all bars
                    chart.invalidate();
                }
            }));
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
