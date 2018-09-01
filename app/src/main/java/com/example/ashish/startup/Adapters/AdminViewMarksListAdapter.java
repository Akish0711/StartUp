package com.example.ashish.startup.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;

import java.util.List;

public class AdminViewMarksListAdapter extends RecyclerView.Adapter<AdminViewMarksListAdapter.ViewHolder>{

    public List<Marks> viewMarksList ;

    public AdminViewMarksListAdapter(List<Marks> viewMarksList) {
        this.viewMarksList = viewMarksList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class,parent,false);
            return new ViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_marks,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // int viewType = getItemViewType(position);
        try {
            holder.display_name.setText(viewMarksList.get(position).getName());
            holder.marks.setText(viewMarksList.get(position).getInputMarks());
        }
        catch (Exception e){
            Log.e("",e.getLocalizedMessage());
        }

    }

    @Override
    public int getItemCount() {
        if(viewMarksList.size() == 0){
            return 1;
        }
        return viewMarksList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && viewMarksList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       View mView;
       public TextView display_name;
       public TextView marks;

       public ViewHolder(View itemView) {
           super(itemView);
           mView = itemView;

           display_name = mView.findViewById(R.id.display_name);
           marks = mView.findViewById(R.id.marks);
       }
    }
}
