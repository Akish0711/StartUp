package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ashish.startup.Models.Users;
import com.example.ashish.startup.R;

import java.util.List;

public class AddStudentsAdapter extends RecyclerView.Adapter<AddStudentsAdapter.ViewHolder> {

    private List<Users> usersList;
    private List<String> selectedList;
    public Context context;

    public AddStudentsAdapter(Context context, List<Users> usersList, List<String>selectedList){
        this.usersList = usersList;
        this.context = context;
        this.selectedList = selectedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.user_name.setText(usersList.get(position).getUsername());
        holder.display_name.setText(usersList.get(position).getName());

        final boolean[] showingFirst = {true};

        holder.check.setOnClickListener(v -> {
            if (showingFirst[0]){
                holder.check.setImageResource(R.drawable.check_box);
                showingFirst[0] = false;
                selectedList.add(usersList.get(position).getUsername());
            }else{
                holder.check.setImageResource(R.drawable.checkbox_outline);
                showingFirst[0] = true;
                selectedList.remove(usersList.get(position).getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
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