package com.google.vision.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.vision.Models.Users;
import com.google.vision.R;

import java.util.List;

public class RemoveStudentsAdapter extends RecyclerView.Adapter<RemoveStudentsAdapter.ViewHolder> {

    private List<Users> usersList;
    private List<String> selectedUid;

    public Context context;

    public RemoveStudentsAdapter(Context context, List<Users> usersList, List<String> selectedUid){
        this.usersList = usersList;
        this.context = context;
        this.selectedUid = selectedUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class, parent, false);
            return new ViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2) {
            holder.user_name.setText(usersList.get(position).getUsername());
            holder.display_name.setText(usersList.get(position).getName());

            holder.check.setImageResource(usersList.get(position).getChecked()? R.drawable.checked_remove: R.drawable.checkbox_outline);

            holder.check.setOnClickListener(v -> {
                usersList.get(position).setChecked(!usersList.get(position).getChecked());
                if (usersList.get(position).getChecked()) {
                    holder.check.setImageResource(R.drawable.checked_remove);
                    selectedUid.add(usersList.get(position).getUid());
                } else {
                    holder.check.setImageResource(R.drawable.checkbox_outline);
                    selectedUid.remove(usersList.get(position).getUid());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(usersList.size() == 0){return 1;}
        return usersList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && usersList.size() == 0) {
            return 1;
        }
        return 2;
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
