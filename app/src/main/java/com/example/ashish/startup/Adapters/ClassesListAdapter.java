package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashish.startup.Activities.MainActivity;
import com.example.ashish.startup.Activities.NewClass2;
import com.example.ashish.startup.Authentication.Login;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView nameText;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            nameText = (TextView)mView.findViewById(R.id.name_text);
        }
    }
}
