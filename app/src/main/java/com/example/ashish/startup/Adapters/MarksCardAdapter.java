package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarksCardAdapter extends RecyclerView.Adapter<MarksCardAdapter.ViewHolder> {
    public List<Marks> testList;
    public Context context;
    private FirebaseFirestore rootRef;
    private String subId,tId,email;
    private Set<Float> mMarks ;
    View alertLayout;

    public MarksCardAdapter(Context context, List<Marks> marksList,String subID,String tID,String email){
        this.testList = marksList;
        this.context = context;
        this.subId = subID;
        this.tId = tID;
        this.email = email;
    }

    @Override
    public MarksCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class,parent,false);
            return new ViewHolder(view);}
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tests, parent, false);
            return new ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        if(viewType == 2){
            holder.test_name.setText(testList.get(position).getMarksID());
            holder.max_marks.setText(testList.get(position).getMax_marks());

            rootRef = FirebaseFirestore.getInstance();

            rootRef.collection("Users").document(tId).collection("Subjects").document(subId).collection("Marks").document(testList.get(position).getMarksID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot doc = task.getResult();
                        if (doc != null && doc.exists()) {
                            try {
                                holder.marks_obtained.setText((String)doc.get(email));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            });



            holder.mView.setOnClickListener(v -> {

                rootRef.collection("Users").document(tId).collection("Subjects").document(subId).collection("Marks").document(testList.get(position).getMarksID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            mMarks = new HashSet<>();
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                Map<String, Object> hm = doc.getData();
                                Set<String> a = hm.keySet();
                                for (String b : a) {
                                    try {
                                        holder.marks_obtained.setText((String)doc.get(email));

                                        if(!b.equals("Max_marks")){
                                            Log.e( "onComplete: ", doc.get(b+".com") + "  " +doc.getId() );
                                            mMarks.add(Float.parseFloat((String)doc.get(b+".com")));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                                alertLayout = LayoutInflater.from(context).inflate(R.layout.graph_plot,null);
                                try{
                                    //HICharts
                                    HIChartView chartView =  alertLayout.findViewById(R.id.hc);
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
                                    for(float m : mMarks){
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

                                    AlertDialog.Builder alertBox = new AlertDialog.Builder(v.getRootView().getContext());
                                    alertBox.setTitle("Graph");

                                    alertBox.setView(alertLayout);
                                    alertBox.setCancelable(false);

                                    alertBox.setPositiveButton("Done", (dialog, which) -> dialog.dismiss());

                                    AlertDialog dialog = alertBox.create();
                                    dialog.show();
                                }
                                catch (NullPointerException e){
                                    Log.e("",e.getLocalizedMessage());
                                }
                                finally {
                                    if(mMarks != null){
                                        mMarks.clear();
                                    }
                                }

                            }
                        }
                    }
                });


            });
        }
    }


    @Override
    public int getItemCount() {
        if(testList.size() == 0){return 1;}
        return testList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && testList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView test_name, max_marks, marks_obtained;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            test_name = mView.findViewById(R.id.tName);
            max_marks = mView.findViewById(R.id.mMarksValue);
            marks_obtained = mView.findViewById(R.id.myImageViewText);
        }
    }
}
