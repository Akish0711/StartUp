package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;

import java.util.List;

public class MarksInputListAdapter extends RecyclerView.Adapter<MarksInputListAdapter.ViewHolder> {

    public List<Marks> marksInputList;
    public Context context;

    public MarksInputListAdapter(Context context, List<Marks> marksInputList){
        this.marksInputList = marksInputList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_get_input_marks,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.user_name.setText(marksInputList.get(position).getUsername());
        holder.display_name.setText(marksInputList.get(position).getName());
        holder.marks.setText(marksInputList.get(position).getInputMarks());

        holder.marks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                marksInputList.get(position).inputMarks = editable.toString();
            }
        });
    }

    public List<Marks> retrieveData(){
        return marksInputList;
    }

    @Override
    public int getItemCount() {
        return marksInputList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView user_name;
        public TextView display_name;
        public EditText marks;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            user_name = mView.findViewById(R.id.user_name);
            display_name = mView.findViewById(R.id.display_name);
            marks = mView.findViewById(R.id.marksInput);

        }
    }
}
