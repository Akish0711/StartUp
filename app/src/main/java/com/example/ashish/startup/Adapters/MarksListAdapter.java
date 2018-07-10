package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Activities.GetMarks;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MarksListAdapter extends RecyclerView.Adapter<MarksListAdapter.ViewHolder> {

    public List<Marks> testList;
    public Context context;
    public String institute,c;

    public MarksListAdapter(Context context, List<Marks> marksList,String ins,String c){
        this.testList = marksList;
        this.context = context;
        this.institute = ins;
        this.c = c;
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

        final String marks_id = testList.get(position).marksID;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,GetMarks.class);
                intent.putExtra("marksID", marks_id);
                intent.putExtra("institute",institute);
                intent.putExtra("class_id",c);
                context.startActivity(intent);
            }
        });
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
