package com.google.vision.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
                && getIntent().hasExtra("uid")
                && getIntent().hasExtra("max_marks")) {
            String class_id = getIntent().getStringExtra("class_id");
            String exam_id = getIntent().getStringExtra("exam_id");
            String uid = getIntent().getStringExtra("uid");
            String exam_name = getIntent().getStringExtra("exam_name");
            String max_marks = getIntent().getStringExtra("max_marks");

            int intMax = Integer.parseInt(max_marks);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(exam_name);
            }

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            ArrayList<String> labels = new ArrayList<>();
            labels.add("Your Marks");
            labels.add("Average");
            labels.add("Highest");

            HorizontalBarChart mChart = findViewById(R.id.chart);
            TextView maxMarks = findViewById(R.id.maxMarks);
            TextView userMarks = findViewById(R.id.userMarks);
            TextView percentage = findViewById(R.id.percentage);
            TextView comment = findViewById(R.id.comment);

            maxMarks.setText("Max Marks: "+max_marks);

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
            SingleExamStudent.CategoryBarChartXaxisFormatter xaxisFormatter = new SingleExamStudent.CategoryBarChartXaxisFormatter(labels);
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
                    entries.add(new BarEntry(0f, Marks));
                    entries.add(new BarEntry(1f, Avg));
                    entries.add(new BarEntry(2f, Max));

                    userMarks.setText("Your Marks: "+Marks);
                    double percent = Marks*100/intMax;
                    percentage.setText("Percentage: "+percent+"%");

                    if (Marks>Avg){
                        comment.setText("Good going!! Your marks are above average. Keep up the hard work.");
                    }else{
                        comment.setText("Uh oh!! You marks are below average. Focus and see yourself go up.");
                    }

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
            }));
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
