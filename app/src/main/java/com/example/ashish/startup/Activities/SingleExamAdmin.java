package com.example.ashish.startup.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.ashish.startup.Adapters.SingleExamAdapter;
import com.example.ashish.startup.Models.SingleExam;
import com.example.ashish.startup.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleExamAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_exam_admin);

        if (getIntent().hasExtra("class_id")
                && getIntent().hasExtra("exam_id")
                && getIntent().hasExtra("exam_name")) {
            String class_id = getIntent().getStringExtra("class_id");
            String exam_id = getIntent().getStringExtra("exam_id");
            String exam_name = getIntent().getStringExtra("exam_name");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(exam_name);
            }

            List<SingleExam> usersList = new ArrayList<>();

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            SingleExamAdapter mAdapter = new SingleExamAdapter(this, usersList);
            RecyclerView mMessagesList = findViewById(R.id.student_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

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
                    long Max = document.getLong("Highest");
                    long Min = document.getLong("Lowest");
                    long Avg = document.getLong("Average");

                    List<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f, Max));
                    entries.add(new BarEntry(1f, Min));
                    entries.add(new BarEntry(2f, Avg));

                    BarDataSet set = new BarDataSet(entries, "");

                    BarData data = new BarData(set);
                    data.setBarWidth(0.7f); // set custom bar width
                    set.setColors(new int[]{R.color.bg_screen2 , R.color.bg_screen1, R.color.bg_screen4},getApplicationContext());
                    chart.setData(data);
                    chart.animateY(3000 , Easing.EasingOption.EaseOutBack );
                    chart.setFitBars(true); // make the x-axis fit exactly all bars
                    chart.invalidate();
                }
            });

            mFirestore.collection("Marks").document(class_id).collection("Exams").document(exam_id).collection("Students").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (final DocumentSnapshot document : task.getResult()) {
                        SingleExam singleExam = document.toObject(SingleExam.class);
                        usersList.add(singleExam);
                        Collections.sort(usersList, SingleExam.BY_NAME_ALPHABETICAL);
                        mAdapter.notifyDataSetChanged();
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
