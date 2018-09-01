package com.example.ashish.startup.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Models.Status;
import com.example.ashish.startup.R;

import java.util.List;

public class StatusListAdapter extends RecyclerView.Adapter<StatusListAdapter.ViewHolder>{

    public List<Status>statusList;

    public StatusListAdapter(List<Status> statusList){
        this.statusList = statusList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class,parent,false);
            return new ViewHolder(view);}
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_status, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if(viewType == 2) {
            if (statusList.get(position).getPercentage() >= 75) {
                holder.percentage.setTextColor(Color.rgb(50, 205, 50));
                holder.percentage.setText(String.valueOf(statusList.get(position).getPercentage()) + "%");
            } else {
                holder.percentage.setTextColor(Color.RED);
                holder.percentage.setText(String.valueOf(statusList.get(position).getPercentage()) + "%");
            }

            holder.user_name.setText(statusList.get(position).getUsername());
            holder.display_name.setText(statusList.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        if(statusList.size() == 0){
            return 1;
        }
        return statusList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && statusList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView percentage;
        public TextView user_name;
        public TextView display_name;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            percentage = mView.findViewById(R.id.percentage);
            user_name = mView.findViewById(R.id.user_name);
            display_name = mView.findViewById(R.id.display_name);
        }
    }
}
