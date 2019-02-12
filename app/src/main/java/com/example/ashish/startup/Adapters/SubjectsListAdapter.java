package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.activities.AnnouncementStudents;
import com.example.ashish.startup.Models.Subject;
import com.example.ashish.startup.R;

import java.util.List;

public class SubjectsListAdapter extends RecyclerView.Adapter<SubjectsListAdapter.ViewHolder> {

    private List<Subject> subjectList;
    private Context context;
    private String uid;

    public SubjectsListAdapter(Context context , List<Subject> subjectList, String uid) {
        this.subjectList = subjectList;
        this.context = context;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class, parent, false);
            return new ViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_subjects, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2) {

            int TotalClass = subjectList.get(position).getTotal_Class();
            int TotalAttended = subjectList.get(position).getTotal_Present();
            String TeacherName = subjectList.get(position).getTeacher_Name();
            String SubjectName = subjectList.get(position).getSubject_Name();
            String TeacherID = subjectList.get(position).getTeacher_id();

            if (TotalClass == 0){
                holder.percentage.setText("0%");
            }else{
                int percentage = TotalAttended*100/TotalClass;
                holder.percentage.setText(String.valueOf(percentage)+"%");
            }

            holder.nameText.setText(SubjectName);
            holder.nameTeacher.setText(TeacherName);
            holder.total.setText(String.valueOf(TotalClass));
            holder.attended.setText(String.valueOf(TotalAttended));

            final String subject_id = subjectList.get(position).subjectID;

            holder.mView.setOnClickListener(view -> {
                Intent intent = new Intent(context, AnnouncementStudents.class);
                intent.putExtra("subject_id", subject_id);
                intent.putExtra("Teacher_id", TeacherID);
                intent.putExtra("subject_name", SubjectName);
                intent.putExtra("Teacher_Name", TeacherName);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if(subjectList.size() == 0){
            return 1;
        }
        return subjectList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && subjectList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView nameText, percentage, nameTeacher, total, attended;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameText = mView.findViewById(R.id.name_text);
            percentage = mView.findViewById(R.id.percentage);
            nameTeacher = mView.findViewById(R.id.teacher);
            total = mView.findViewById(R.id.total);
            attended = mView.findViewById(R.id.attended);
        }
    }
}