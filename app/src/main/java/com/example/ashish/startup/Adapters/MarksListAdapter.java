package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;

import java.util.List;

public class MarksListAdapter extends RecyclerView.Adapter<MarksListAdapter.ViewHolder> {

    public List<Marks> testList;
    public Context context;

    public MarksListAdapter(Context context, List<Marks> marksList){
        this.testList = marksList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tests,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.test_name.setText(testList.get(position).getMarksID());
        holder.max_marks.setText(testList.get(position).getMax_marks());
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView test_name;
        public TextView max_marks;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            test_name = mView.findViewById(R.id.tName);
            max_marks = mView.findViewById(R.id.mMarksValue);
        }
    }
}
