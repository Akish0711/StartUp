package com.example.ashish.startup.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.example.ashish.startup.Adapters.AdminViewMarksListAdapter;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewMarks extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView marksListView;
    private FirebaseFirestore mFirestore;
    private List<Marks> marksList;
    private Set<Integer> mMarks = new HashSet<Integer>();
    private AdminViewMarksListAdapter adminViewMarksListAdapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_marks);

        marksList = new ArrayList<>();
        adminViewMarksListAdapter = new AdminViewMarksListAdapter(marksList);
        mAuth = FirebaseAuth.getInstance();

        if(getIntent().hasExtra("class_id") && getIntent().hasExtra("email_red") && getIntent().hasExtra("marksID")){
            final String class_id = getIntent().getStringExtra("class_id");
            final String marksID = getIntent().getStringExtra("marksID");
            final String email_red = getIntent().getStringExtra("email_red");

            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_view_marks);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("View Marks");
            }

            marksListView = findViewById(R.id.view_marks_recycler_view);
            progressBar = findViewById(R.id.progressBarViewMarks);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setScaleY(2f);

            marksListView.setHasFixedSize(true);
            marksListView.setLayoutManager(new LinearLayoutManager(this));
            marksListView.setAdapter(adminViewMarksListAdapter);
            mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Marks").document(marksID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        DocumentSnapshot doc = task.getResult();
                        if(doc != null && doc.exists()){
                           Map<String,Object> hm = doc.getData();
                           Set<String> a = hm.keySet();
                           for(String b : a){
                                      try{
                                          progressBar.setVisibility(View.GONE);
                                          Marks marks = new Marks() ;

                                          marks.setName(b.replace("@gmail",""));
                                          marks.setMax_marks((String)doc.get("Max_marks"));

                                          Log.e("onComplete: ",Integer.parseInt((String)doc.get(b+".com"))+"");

                                          mMarks.add(Integer.parseInt((String)doc.get(b+".com")));

                                          marks.setInputMarks((String)doc.get(b+".com"));
                                          marks.setUsername(b.replace("@gmail",""));
                                          if(!b.equals("Max_marks")){
                                              marksList.add(marks);
                                          }
                                          Collections.sort(marksList,Marks.BY_NAME_ALPHABETICAL);
                                          adminViewMarksListAdapter.notifyDataSetChanged();
                                        }
                                        catch (Exception e){
                                            Log.e("error : ",e.getLocalizedMessage()+"" );
                                        }
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

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        if(id == R.id.admin_graph){

            final LayoutInflater inflater = getLayoutInflater();
            final View alertLayout = inflater.inflate(R.layout.graph_plot, null);
            AlertDialog.Builder alert = new AlertDialog.Builder(ViewMarks.this);
            alert.setTitle("Graph");
            // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            alert.setView(alertLayout);
            alert.setCancelable(false);

            alert.setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show());
            alert.setPositiveButton("Done", (dialog, which) -> Toast.makeText(getBaseContext(), "Done click", Toast.LENGTH_SHORT).show());

            AlertDialog dialog = alert.create();

            dialog.show();

            try {

//                // Eazegraph Bar Graph
//                BarChart mBarChart =  dialog.findViewById(R.id.barChart);
//
//                int i=0;
//                for(int m : mMarks){
//                    mBarChart.addBar(new BarModel(m, 0xFF123456));
//                    i++;
//                }
//
//
//                mBarChart.startAnimation();


                //HICharts
//            HIChartView chartView =  dialog.findViewById(R.id.hc);

//                HIOptions options = new HIOptions();
//
//                HIChart chart = new HIChart();
//                chart.setType("column");
//                options.setChart(chart);
//
//                HITitle title = new HITitle();
//                title.setText("Demo chart");
//                options.setTitle(title);
//
//                HIColumn series = new HIColumn();
//                series.setData(new ArrayList<>(Arrays.asList(49.9, 71.5, 106.4, 129.2, 144, 176, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4)));
//                options.setSeries(new ArrayList<HISeries>(Collections.singletonList(series)));
//
//                chartView.setOptions(options);
//

                //MPChart Library
                BarChart mChart = dialog.findViewById(R.id.mChart);

                mChart.setDrawBarShadow(false);
                mChart.setDrawValueAboveBar(true);

                mChart.getDescription().setEnabled(false);
                mChart.setMaxVisibleValueCount(60);

                // scaling can now only be done on x- and y-axis separately
                mChart.setPinchZoom(true);

                mChart.setDrawGridBackground(false);

                List<BarEntry> entries = new ArrayList<>();
                  int i=0;
                for(int m : mMarks){
                    entries.add(new BarEntry(i, m));
                    i++;
                }
                BarDataSet set = new BarDataSet(entries, "Marks");

                BarData data = new BarData(set);
                data.setBarWidth(0.9f); // set custom bar width
                mChart.setData(data);
                mChart.setFitBars(true);
                mChart.invalidate(); // refresh

            }catch (NullPointerException e){
                Log.e("",e.getLocalizedMessage());
            }
            Toast.makeText(getApplicationContext(),"This is graph",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
