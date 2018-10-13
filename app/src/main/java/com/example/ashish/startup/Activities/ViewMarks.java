package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.ashish.startup.Adapters.AdminViewMarksListAdapter;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.highsoft.highcharts.Common.HIChartsClasses.HIChart;
import com.highsoft.highcharts.Common.HIChartsClasses.HIExporting;
import com.highsoft.highcharts.Common.HIChartsClasses.HIHistogram;
import com.highsoft.highcharts.Common.HIChartsClasses.HILegend;
import com.highsoft.highcharts.Common.HIChartsClasses.HIMarker;
import com.highsoft.highcharts.Common.HIChartsClasses.HIOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HIScatter;
import com.highsoft.highcharts.Common.HIChartsClasses.HITitle;
import com.highsoft.highcharts.Common.HIChartsClasses.HIXAxis;
import com.highsoft.highcharts.Common.HIChartsClasses.HIYAxis;
import com.highsoft.highcharts.Core.HIChartView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewMarks extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView marksListView;
    private List<Marks> marksList;
    private Set<Float> mMarks = new HashSet<>();
    private AdminViewMarksListAdapter adminViewMarksListAdapter;
    private FirebaseAuth mAuth;
    private String email_red, marksID, class_id, institute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_marks);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("class_id") && getIntent().hasExtra("email_red")
                && getIntent().hasExtra("marksID") && getIntent().hasExtra("institute")) {
            class_id = getIntent().getStringExtra("class_id");
            marksID = getIntent().getStringExtra("marksID");
            email_red = getIntent().getStringExtra("email_red");
            institute = getIntent().getStringExtra("institute");

            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_view_marks);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(marksID + " Marks");
            }
            marksList = new ArrayList<>();
            adminViewMarksListAdapter = new AdminViewMarksListAdapter(marksList, marksID, class_id, email_red);

            marksListView = findViewById(R.id.view_marks_recycler_view);
            progressBar = findViewById(R.id.progressBarViewMarks);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setScaleY(2f);

            marksListView.setHasFixedSize(true);
            marksListView.setLayoutManager(new LinearLayoutManager(this));
            marksListView.setAdapter(adminViewMarksListAdapter);
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id)
                    .collection("Marks").document(marksID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        Map<String, Object> hm = doc.getData();
                        Set<String> a = hm.keySet();
                        for (String b : a) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                Marks marks = new Marks();
                                marks.setName(b.replace("@gmail", ""));
                                marks.setMax_marks((String) doc.get("Max_marks"));

                                if (doc.get(b + ".com") == null || ((String) doc.get(b + ".com")).isEmpty()) {
                                    if (b.equals("Max_marks")) {
                                    } else {
                                        mMarks.add(Float.valueOf(0));
                                    }
                                } else {
                                    mMarks.add(Float.parseFloat((String) doc.get(b + ".com")));
                                }

                                marks.setInputMarks((String) doc.get(b + ".com"));
                                marks.setUsername(b.replace("@gmail", ""));

                                if (!b.equals("Max_marks")) {
                                    marksList.add(marks);
                                }

                                Collections.sort(marksList, Marks.BY_NAME_ALPHABETICAL);
                                adminViewMarksListAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("error : ", e.getLocalizedMessage() + "" + e.getMessage());
                            }
                        }
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graph_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.enter_marks) {
            Intent intent = new Intent(ViewMarks.this, GetMarks.class);
            intent.putExtra("class_id", class_id);
            intent.putExtra("marksID", marksID);
            intent.putExtra("institute", institute);
            startActivity(intent);
        }

        if (id == R.id.admin_graph) {

            final LayoutInflater inflater = getLayoutInflater();
            final View alertLayout = inflater.inflate(R.layout.graph_plot, null);

            try {
                //HICharts
                HIChartView chartView = alertLayout.findViewById(R.id.hc);
                chartView.plugins = new ArrayList<>(Arrays.asList("histogram-bellcurve"));

                HIOptions options = new HIOptions();

                HIChart chart = new HIChart();
                chart.setType("variwide");
                options.setChart(chart);

                HITitle title = new HITitle();
                title.setText("Score Division");
                options.setTitle(title);

                HIXAxis xaxis1 = new HIXAxis();
                HITitle ht = new HITitle();
                ht.setText("Count");
                xaxis1.setTitle(ht);

                HIXAxis xaxis2 = new HIXAxis();
                xaxis2.setTitle(new HITitle());
                xaxis2.setOpposite(true);

                options.setXAxis(new ArrayList<>(Arrays.asList(xaxis1, xaxis2)));

                HIYAxis yaxis1 = new HIYAxis();
                HITitle ht2 = new HITitle();
                ht2.setText("Marks");
                yaxis1.setTitle(ht2);

                HIYAxis yaxis2 = new HIYAxis();
                yaxis2.setTitle(new HITitle());
                yaxis2.setOpposite(true);

                options.setYAxis(new ArrayList<>(Arrays.asList(yaxis1, yaxis2)));

                HILegend legend = new HILegend();
                legend.setEnabled(true);
                options.setLegend(legend);

                HIHistogram series1 = new HIHistogram();
                series1.setType("histogram");
                series1.setName("Histogram");
                series1.setXAxis(1);
                series1.setYAxis(1);
                series1.setBaseSeries("s1");
                series1.setZIndex(-1);

                HIScatter series2 = new HIScatter();
                series2.setType("scatter");
                series2.setName("Data");

                Number[] series2_data = new Number[mMarks.size()];

                int i = 0;
                for (float m : mMarks) {
                    series2_data[i] = m;
                    i++;
                }

                series2.setId("s1");
                series2.setData(new ArrayList<>(Arrays.asList(series2_data)));
                series2.setMarker(new HIMarker());
                series2.getMarker().setRadius(2.5);

                options.setSeries(new ArrayList<>(Arrays.asList(series1, series2)));

                options.setExporting(new HIExporting());
                options.getExporting().setEnabled(false);

                chartView.setOptions(options);

                AlertDialog.Builder alert = new AlertDialog.Builder(ViewMarks.this);
                alert.setTitle("Graph");

                alert.setView(alertLayout);
                alert.setCancelable(false);

                alert.setPositiveButton("Done", (dialog, which) -> {
                });

                AlertDialog dialog = alert.create();

                dialog.show();
            } catch (NullPointerException e) {
                Log.e("", e.getLocalizedMessage());
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
