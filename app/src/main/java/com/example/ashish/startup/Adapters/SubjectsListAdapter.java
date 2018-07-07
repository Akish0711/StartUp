package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Activities.UserNewClass;
import com.example.ashish.startup.Models.Subject;
import com.example.ashish.startup.R;

import java.util.List;

public class SubjectsListAdapter extends RecyclerView.Adapter<SubjectsListAdapter.ViewHolder> {

    public List<Subject> subjectList;
    public Context context;

    public SubjectsListAdapter(Context context , List<Subject> subjectList) {
        this.subjectList = subjectList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_subjects,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.nameText.setText(subjectList.get(position).getSubject_Name());
        holder.percentage.setText(subjectList.get(position).getPercentage()+ " %");
        final String subject_id = subjectList.get(position).subjectID;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,UserNewClass.class);
                intent.putExtra("subject_id", subject_id);
                intent.putExtra("subject_name",subjectList.get(position).getSubject_Name());
                intent.putExtra("Teacher_Name", subjectList.get(position).getTeacher_Name());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView nameText;
        public TextView percentage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameText = mView.findViewById(R.id.name_text);
            percentage = mView.findViewById(R.id.percentage);
        }
    }

}