package com.google.vision.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.vision.Adapters.SingleExamAdapter;
import com.google.vision.Models.SingleExam;
import com.google.vision.R;
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

            ArrayList<String> labels = new ArrayList<>();
            labels.add("Average");
            labels.add("Lowest");
            labels.add("Highest");

            HorizontalBarChart mChart = findViewById(R.id.chart);
            mChart.setDrawBarShadow(false);
            mChart.setDrawValueAboveBar(true);
            mChart.getDescription().setEnabled(false);
            mChart.setPinchZoom(false);
            mChart.setDrawGridBackground(false);
            mChart.setTouchEnabled(false);

            XAxis xl = mChart.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setDrawAxisLine(true);
            xl.setDrawGridLines(false);
            CategoryBarChartXaxisFormatter xaxisFormatter = new CategoryBarChartXaxisFormatter(labels);
            xl.setValueFormatter(xaxisFormatter);
            xl.setGranularity(1);

            YAxis yl = mChart.getAxisLeft();
            yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            yl.setDrawGridLines(false);
            yl.setEnabled(false);
            yl.setAxisMinimum(0f);

            YAxis yr = mChart.getAxisRight();
            yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            yr.setDrawGridLines(false);
            yr.setAxisMinimum(0f);


            mFirestore.collection("Marks").document(exam_id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    long Max = document.getLong("Highest");
                    long Min = document.getLong("Lowest");
                    long Avg = document.getLong("Average");

                    List<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f, Avg));
                    entries.add(new BarEntry(1f, Min));
                    entries.add(new BarEntry(2f, Max));

                    BarDataSet set1;
                    set1 = new BarDataSet(entries, "DataSet 1");
                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);
                    BarData data = new BarData(dataSets);
                    //data.setValueTextSize(12f);
                    set1.setColors(ColorTemplate.MATERIAL_COLORS);
                    mChart.setData(data);
                    mChart.getLegend().setEnabled(false);
                    data.setBarWidth(0.9f);
                    //data.setBarWidth(0.7f); // set custom bar width
                    mChart.animateY(3000 , Easing.EasingOption.EaseOutBack );
                    mChart.setFitBars(true); // make the x-axis fit exactly all bars
                    //chart.invalidate();
                }
            });

            mFirestore.collection("Marks").document(exam_id).collection("Students").get().addOnCompleteListener(task -> {
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

    public class CategoryBarChartXaxisFormatter implements IAxisValueFormatter {

        ArrayList<String> mValues;

        CategoryBarChartXaxisFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            int val = (int) value;
            String label = "";
            if (val >= 0 && val < mValues.size()) {
                label = mValues.get(val);
            } else {
                label = "";
            }
            return label;
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
