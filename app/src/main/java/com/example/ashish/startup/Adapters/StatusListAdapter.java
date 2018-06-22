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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_status,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (statusList.get(position).getPercentage()>=75){
            holder.percentage.setTextColor(Color.rgb(50,205,50));
            holder.percentage.setText(String.valueOf(statusList.get(position).getPercentage())+"%");
        }else {
            holder.percentage.setTextColor(Color.RED);
            holder.percentage.setText(String.valueOf(statusList.get(position).getPercentage())+"%");
        }

        holder.user_name.setText(statusList.get(position).getUsername());
        holder.display_name.setText(statusList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView percentage;
        public TextView user_name;
        public TextView display_name;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            percentage = (TextView)mView.findViewById(R.id.percentage);
            user_name = (TextView)mView.findViewById(R.id.user_name);
            display_name = (TextView)mView.findViewById(R.id.display_name);
        }
    }
}
