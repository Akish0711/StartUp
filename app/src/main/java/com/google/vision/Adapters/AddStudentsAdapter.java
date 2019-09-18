package com.google.vision.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.vision.Models.Users;
import com.google.vision.R;

import java.util.List;

public class AddStudentsAdapter extends RecyclerView.Adapter<AddStudentsAdapter.ViewHolder> {

    private List<Users> usersList;
    private List<String> selectedUid;
    private List<String> name;
    private List<String> username;

    public Context context;

    public AddStudentsAdapter(Context context, List<Users> usersList, List<String>selectedUid, List<String> name,List<String> username){
        this.usersList = usersList;
        this.context = context;
        this.selectedUid = selectedUid;
        this.name = name;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String Username = usersList.get(position).getUsername();
        String Name = usersList.get(position).getName();

        holder.user_name.setText(Username);
        holder.display_name.setText(Name);
        String UID = usersList.get(position).getUid();

        final boolean[] showingFirst = {true};

        holder.check.setOnClickListener(v -> {
            if (showingFirst[0]){
                holder.check.setImageResource(R.drawable.check_box);
                showingFirst[0] = false;
                selectedUid.add(UID);
                name.add(Name);
                username.add(Username);
            }else{
                holder.check.setImageResource(R.drawable.checkbox_outline);
                showingFirst[0] = true;
                selectedUid.remove(UID);
                name.remove(Name);
                username.remove(Username);
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

    /*public void sortByName(boolean isDescending) {
        if (usersList.size() > 0) {
            Collections.sort(usersList, new Comparator<Users>() {
                @Override
                public int compare(Users object1, Users object2) {
                    if (isDescending)
                        return object2.getUsername().toLowerCase().compareTo(object1.getUsername().toLowerCase());
                    else
                        return object1.getUsername().toLowerCase().compareTo(object2.getUsername().toLowerCase());
                }
            });
            notifyDataSetChanged();
        }
    }*/
}
