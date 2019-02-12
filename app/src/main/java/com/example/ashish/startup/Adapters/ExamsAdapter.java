package com.example.ashish.startup.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.ashish.startup.Models.Exams;
import com.example.ashish.startup.Models.SingleExam;
import com.example.ashish.startup.R;
import com.example.ashish.startup.activities.AnnouncementAdmin;
import com.example.ashish.startup.activities.SingleExamAdmin;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamsAdapter  extends RecyclerView.Adapter<ExamsAdapter.ViewHolder>{
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public ExamsAdapter() {
        // uncomment the line below if you want to open only one row at a time
        //viewBinderHelper.setOpenOnlyOne(true);
    }

    private List<Exams> examsList;
    public Context context;
    private String class_id;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private View parentLayout;

    public ExamsAdapter(Context context, List<Exams> examsList, String class_id, View parentLayout){
        this.examsList = examsList;
        this.context = context;
        this.class_id = class_id;
        this.parentLayout = parentLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_exams,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String exam_id = examsList.get(position).examID;
        // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
        // put an unique string id as value, can be any string which uniquely define the data
        viewBinderHelper.bind(holder.swipeLayout, exam_id);
        viewBinderHelper.setOpenOnlyOne(true);
        String examName = examsList.get(position).getName();
        String maxMarks = "Max Marks: "+examsList.get(position).getMax_Marks();
        String examDate = examsList.get(position).getDate();
        holder.exam_name.setText(examName);
        holder.max_marks.setText(maxMarks);
        holder.date.setText(examDate);

        holder.frontLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, SingleExamAdmin.class);
            intent.putExtra("class_id", class_id);
            intent.putExtra("exam_id", exam_id);
            intent.putExtra("exam_name", examName);
            context.startActivity(intent);
        });

        holder.delete.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete this Exam?")
                        .setMessage("Warning : You cannot undo this.")
                        .setCancelable(false)
                        .setPositiveButton("DELETE", (dialog, id) ->
                                mFirestore.collection("Marks").document(class_id).collection("Exams")
                                        .document(exam_id).delete().addOnSuccessListener(aVoid -> {
                                    KToast.successToast((Activity) context, "Exam Deleted", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                }).addOnFailureListener(e -> Toast.makeText(context, "Error in deleting classes", Toast.LENGTH_SHORT).show()))
                        .setNegativeButton("Cancel", null)
                        .show();
            }else{
                Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = process.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return examsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView exam_name, max_marks, date;
        private View frontLayout;
        private SwipeRevealLayout swipeLayout;
        public ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            frontLayout = itemView.findViewById(R.id.front_layout);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            exam_name = mView.findViewById(R.id.exam_name);
            delete = mView.findViewById(R.id.delete);
            max_marks = mView.findViewById(R.id.max_marks);
            date = mView.findViewById(R.id.date);
        }
    }
}
