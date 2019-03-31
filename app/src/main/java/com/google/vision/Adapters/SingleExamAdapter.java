package com.google.vision.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.vision.Models.SingleExam;
import com.google.vision.R;

import java.util.List;

public class SingleExamAdapter extends RecyclerView.Adapter<SingleExamAdapter.ViewHolder> {

    private List<SingleExam> usersList;

    public Context context;

    public SingleExamAdapter(Context context, List<SingleExam> usersList){
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_exam,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String Username = usersList.get(position).getUsername();
        String Name = usersList.get(position).getName();
        double Marks = usersList.get(position).getMarks();

        holder.user_name.setText(Username);
        holder.display_name.setText(Name);
        holder.marks.setText(String.valueOf(Marks));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView user_name, display_name, marks;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            user_name = mView.findViewById(R.id.user_name);
            display_name = mView.findViewById(R.id.display_name);
            marks = mView.findViewById(R.id.marks);
        }
    }
}

