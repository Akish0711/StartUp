package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.R;

import java.util.List;

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.ViewHolder> {

    private List<Attendance> attendanceList;
    private List<String> presentList;
    private List<String> absentList;
    public Context context;

    public AttendanceListAdapter(Context context, List<Attendance> attendanceList, List<String>presentList, List<String>absentList){
        this.attendanceList = attendanceList;
        this.context = context;
        this.presentList = presentList;
        this.absentList = absentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class, parent, false);
            return new ViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_attendance,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2) {
            holder.user_name.setText(attendanceList.get(position).getUsername());
            holder.display_name.setText(attendanceList.get(position).getName());
            int percentage = attendanceList.get(position).getPercentage();

            if (percentage >= 75) {
                holder.percentage.setTextColor(Color.rgb(139, 194, 74));
                holder.percentage.setText(String.valueOf(percentage) + "%");
            } else {
                holder.percentage.setTextColor(Color.rgb(229, 57, 53));
                holder.percentage.setText(String.valueOf(percentage) + "%");
            }

            String UID = attendanceList.get(position).getUid();
            presentList.add(position,UID);

            final boolean[] showingFirst = {true};
            holder.check.setOnClickListener(v -> {
                if (showingFirst[0]) {
                    holder.check.setImageResource(R.drawable.checked_remove);
                    showingFirst[0] = false;
                    presentList.remove(position);
                    absentList.add(position,UID);
                } else {
                    holder.check.setImageResource(R.drawable.present_check);
                    showingFirst[0] = true;
                    presentList.add(position,UID);
                    absentList.remove(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(attendanceList.size() == 0){return 1;}
        return attendanceList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && attendanceList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView user_name,display_name,percentage;
        ImageView check;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            percentage = mView.findViewById(R.id.percentage);
            user_name = mView.findViewById(R.id.user_name);
            display_name = mView.findViewById(R.id.display_name);
            check = mView.findViewById(R.id.check);
        }
    }
}
