package com.example.ashish.startup.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

    private String class_id, exam_id, exam_name, max_marks;
    List<SingleExam> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_exam_admin);

        if (getIntent().hasExtra("class_id")
                && getIntent().hasExtra("exam_id")
                && getIntent().hasExtra("exam_name")
                && getIntent().hasExtra("max_marks")) {
            class_id = getIntent().getStringExtra("class_id");
            exam_id = getIntent().getStringExtra("exam_id");
            exam_name = getIntent().getStringExtra("exam_name");
            max_marks = getIntent().getStringExtra("max_marks");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(exam_name);
            }

            usersList = new ArrayList<>();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        if (id == R.id.edit_button) {
            Intent intent = new Intent(SingleExamAdmin.this,EditMarks.class);
            intent.putExtra("class_id", class_id);
            intent.putExtra("exam_id", exam_id);
            intent.putExtra("exam_name", exam_name);
            intent.putExtra("max_marks", max_marks);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
