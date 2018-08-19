package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.ashish.startup.Activities.NewClass2;
import com.example.ashish.startup.Models.Classes;
import com.example.ashish.startup.R;

import java.util.List;

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ViewHolder> {

    public List<Classes> classesList;
    public Context context;

    public ClassesListAdapter(Context context,List<Classes> classesList){
        this.classesList = classesList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class, parent, false);
            return new ViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2){
            holder.nameText.setText(classesList.get(position).getName());
            final String class_id = classesList.get(position).classID;
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,NewClass2.class);
                    intent.putExtra("class_id", class_id);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(classesList.size() == 0){return 1;}
            return classesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && classesList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView nameText;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            nameText = mView.findViewById(R.id.name_text);
        }
    }
}
