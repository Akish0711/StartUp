package com.google.vision.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.vision.Models.Marks;
import com.google.vision.R;

import java.util.List;

public class AddMarksAdpater extends RecyclerView.Adapter<AddMarksAdpater.ViewHolder> {

    private List<Marks> marksList;
    private List<String> nameList;
    private List<String> usernameList;
    private List<Double> marks;
    private List<String> uidList;

    public Context context;

    public AddMarksAdpater(Context context, List<Marks> marksList, List<Double> marks, List<String> nameList, List<String> usernameList, List<String> uidList){
        this.marksList = marksList;
        this.context = context;
        this.marks = marks;
        this.nameList = nameList;
        this.usernameList = usernameList;
        this.uidList = uidList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users_marks,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = marksList.get(position).getName();
        String username = marksList.get(position).getUsername();
        holder.user_name.setText(username);
        holder.display_name.setText(name);

        nameList.add(name);
        uidList.add(marksList.get(position).getUid());
        usernameList.add(username);
        marks.add(null);

        holder.marks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")||editable.toString().equals(".")) {
                    marks.set(position, null);

                }else{
                    marks.set(position, Double.parseDouble(editable.toString()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return marksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView user_name, display_name, marks;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            user_name = mView.findViewById(R.id.user_name);
            display_name = mView.findViewById(R.id.display_name);
            marks = mView.findViewById(R.id.marks);
        }
    }
}
