package com.example.ashish.startup.Adapters;

import android.content.Context;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_attendance,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.user_name.setText(attendanceList.get(position).getUsername());
        holder.display_name.setText(attendanceList.get(position).getName());

        final boolean[] showingFirst = {true};

        holder.check.setOnClickListener(v -> {
            if (showingFirst[0]){
                holder.check.setImageResource(R.drawable.checked);
                showingFirst[0] = false;
                presentList.remove(attendanceList.get(position).getUsername());
                absentList.add(attendanceList.get(position).getUsername());
            }else{
                holder.check.setImageResource(R.drawable.present);
                showingFirst[0] = true;
                presentList.add(attendanceList.get(position).getUsername());
                absentList.remove(attendanceList.get(position).getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView user_name;
        public TextView display_name;
        ImageView check;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            user_name = mView.findViewById(R.id.user_name);
            display_name = mView.findViewById(R.id.display_name);
            check = mView.findViewById(R.id.check);
        }
    }
}
