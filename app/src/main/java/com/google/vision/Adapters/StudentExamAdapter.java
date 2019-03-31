package com.google.vision.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.vision.Models.Exams;
import com.google.vision.R;
import com.google.vision.activities.SingleExamStudent;

import java.util.List;

public class StudentExamAdapter extends RecyclerView.Adapter<StudentExamAdapter.ViewHolder>{

    private List<Exams> examsList;
    public Context context;
    private String class_id, uid;

    public StudentExamAdapter(Context context, List<Exams> examsList, String class_id, String uid){
        this.examsList = examsList;
        this.context = context;
        this.class_id = class_id;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_exams,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String exam_id = examsList.get(position).examID;
        // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
        // put an unique string id as value, can be any string which uniquely define the data
        String examName = examsList.get(position).getName();
        String maxMarks = "Max Marks: "+examsList.get(position).getMax_Marks();
        String examDate = examsList.get(position).getDate();
        holder.exam_name.setText(examName);
        holder.max_marks.setText(maxMarks);
        holder.date.setText(examDate);

        holder.frontLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, SingleExamStudent.class);
            intent.putExtra("class_id", class_id);
            intent.putExtra("exam_id", exam_id);
            intent.putExtra("exam_name", examName);
            intent.putExtra("uid", uid);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return examsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView exam_name, max_marks, date;
        private View frontLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            frontLayout = itemView.findViewById(R.id.front_layout);
            exam_name = mView.findViewById(R.id.exam_name);
            max_marks = mView.findViewById(R.id.max_marks);
            date = mView.findViewById(R.id.date);
        }
    }
}

