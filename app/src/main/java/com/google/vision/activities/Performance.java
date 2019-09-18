package com.google.vision.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.google.vision.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
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
            ArrayList<String> xLabels = new ArrayList<>();

            BarChart chart2 = findViewById(R.id.chart2);
            chart2.setPinchZoom(false);
            chart2.getDescription().setEnabled(false);
            chart2.setDrawGridBackground(false);
            chart2.getAxisLeft().setEnabled(false);
            chart2.getAxisRight().setEnabled(false);
            chart2.getDescription().setEnabled(false);
            chart2.getLegend().setEnabled(false);
            chart2.setTouchEnabled(false);

            XAxis xAxis2 = chart2.getXAxis();
            xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis2.setDrawGridLines(false);

            YAxis leftAxis2 = chart2.getAxisLeft();
            leftAxis2.setAxisMinimum(1);

            List<BarEntry> entries2 = new ArrayList<>();
            ArrayList<String> xLabels2 = new ArrayList<>();

            final ColorDrawable green = new ColorDrawable(Color.rgb(139, 194, 74));

            rootRef.collection("Users").document(uid).collection("Performance").orderBy("Year").get().addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    int i = 0;
                    for (final DocumentSnapshot document : task2.getResult()) {
                        long year = document.getLong("Year");
                        long total_students = document.getLong("Total_Students");
                        long left_students = document.getLong("Left_Students");
                        xLabels.add(""+year);
                        entries.add(new BarEntry(i,total_students));
                        xLabels2.add(""+year);
                        entries2.add(new BarEntry(i,left_students));
                        i++;
                    }
                    xAxis.setLabelCount(i);
                    xAxis2.setLabelCount(i);
                }
            }).addOnCompleteListener(task -> {
                BarDataSet set = new BarDataSet(entries, "");
                set.setColor(getResources().getColor(R.color.custom_green));
                BarData data = new BarData(set);
                data.setBarWidth(0.9f); // set custom bar width
                data.setValueTextSize(15);
                chart.setData(data);
                chart.animateY(3000 , Easing.EasingOption.EaseOutBack );
                chart.setFitBars(true); // make the x-axis fit exactly all bars
                chart.invalidate();
                chart.getData().setValueFormatter(new MyValueFormatter());

                BarDataSet set2 = new BarDataSet(entries2, "");

                BarData data2 = new BarData(set2);
                set2.setColor(getResources().getColor(R.color.custom_red));
                data2.setBarWidth(0.9f); // set custom bar width
                data2.setValueTextSize(15);
                chart2.setData(data2);
                chart2.animateY(3000 , Easing.EasingOption.EaseOutBack );
                chart2.setFitBars(true); // make the x-axis fit exactly all bars
                chart2.invalidate();
                chart2.getData().setValueFormatter(new MyValueFormatter());

            });

            chart.getXAxis().setValueFormatter(new IntegerFormatter());

            xAxis.setValueFormatter((value, axis) -> xLabels.get((int) value));

            chart2.getXAxis().setValueFormatter(new IntegerFormatter());

            xAxis2.setValueFormatter((value, axis) -> xLabels2.get((int) value));
        }
    }

    public class IntegerFormatter implements IAxisValueFormatter {
        private DecimalFormat mFormat;

        IntegerFormatter() {
            mFormat = new DecimalFormat("0");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value);
        }
    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        MyValueFormatter() {
            mFormat = new DecimalFormat("0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
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
