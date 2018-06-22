package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.R;

import java.util.ArrayList;
import java.util.List;

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.ViewHolder> {

    public List<Attendance>attendanceList;
    public Context context;
    public AttendanceListAdapter(Context context, List<Attendance> attendanceList){
        this.attendanceList = attendanceList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_attendance,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.user_name.setText(attendanceList.get(position).getUsername());
        holder.display_name.setText(attendanceList.get(position).getName());
        holder.checkBox.setChecked(attendanceList.get(position).isSelected());
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox)view;
                int clickedPos = (Integer) cb.getTag();
                attendanceList.get(clickedPos).setSelected(cb.isChecked());
                notifyDataSetChanged();

            }
        });
    }

    public  List getUnselectedItem(){
        List itemModelList = new ArrayList<>();
        for (int i =0; i < attendanceList.size(); i++){
            Attendance attendance = attendanceList.get(i);
            if (!attendance.isSelected()){
                itemModelList.add(attendance);
            }
        }
        return itemModelList;
    }

    public  List getSelectedItem(){
        List itemModelList = new ArrayList<>();
        for (int i =0; i < attendanceList.size(); i++){
            Attendance attendance = attendanceList.get(i);
            if (attendance.isSelected()){
                itemModelList.add(attendance);
            }
        }
        return itemModelList;
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView user_name;
        public TextView display_name;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            user_name = (TextView)mView.findViewById(R.id.user_name);
            display_name = (TextView)mView.findViewById(R.id.display_name);
            checkBox = (CheckBox)mView.findViewById(R.id.checkbox);

        }
    }
}
