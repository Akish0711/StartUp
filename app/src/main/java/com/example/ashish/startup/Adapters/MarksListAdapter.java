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

    public List<Marks> marksList;
    public Context context;
    public MarksListAdapter(Context context, List<Marks> marksList){
        this.marksList = marksList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_marks,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.user_name.setText(marksList.get(position).getUsername());
        holder.display_name.setText(marksList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return marksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView user_name;
        public TextView display_name;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            user_name = (TextView)mView.findViewById(R.id.user_name);
            display_name = (TextView)mView.findViewById(R.id.display_name);
        }
    }
}
