package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ashish.startup.Models.Users;
import com.example.ashish.startup.R;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> implements Filterable {

    public Context context;
    private List<Users> usersList;
    private List<Users> usersListFiltered;
    private UsersAdapterListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView user_name;
        public TextView display_name;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            user_name = mView.findViewById(R.id.user_name);
            display_name = mView.findViewById(R.id.display_name);
            checkBox = mView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onUsersSelected(usersListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public UsersListAdapter(Context context, List<Users> usersList, UsersListAdapter.UsersAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.usersList = usersList;
        this.usersListFiltered = usersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.user_name.setText(usersListFiltered.get(position).getUsername());
        holder.display_name.setText(usersListFiltered.get(position).getName());
        holder.checkBox.setChecked(usersListFiltered.get(position).isSelected());
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox)view;
                int clickedPos = ((Integer)cb.getTag()).intValue();
                usersListFiltered.get(clickedPos).setSelected(cb.isChecked());
                notifyDataSetChanged();
            }
        });
    }

    public  List getSelectedItem(){
        List userModelList = new ArrayList<>();
        for (int i =0; i < usersListFiltered.size(); i++){
            Users users = usersListFiltered.get(i);
            if (users.isSelected()){
                userModelList.add(users);
            }
        }
        return userModelList;
    }

    @Override
    public int getItemCount() {
        return usersListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    usersListFiltered = usersList;
                } else {
                    List<Users> filteredList = new ArrayList<>();
                    for (Users row : usersList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getUsername().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    usersListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = usersListFiltered;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                usersListFiltered = (ArrayList<Users>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface UsersAdapterListener {
        void onUsersSelected(Users users);
    }
}