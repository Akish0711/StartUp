package com.google.vision.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.vision.activities.ViewSingleAnnouncement;
import com.google.vision.Models.Message;
import com.google.vision.R;

import java.util.List;

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ViewHolder> {

    private List<Message> messageList;
    private String class_id, TeacherID;
    public Context context;

    public UserMessageAdapter(Context context,List<Message> messageList, String class_id, String Teacher_id){
        this.messageList = messageList;
        this.class_id = class_id;
        this.context = context;
        this.TeacherID = Teacher_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_no_announcement, parent, false);
            return new ViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message_user,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if(viewType == 2) {
            String text_message = messageList.get(position).getMessage();
            holder.Text.setText(text_message);
            holder.Time.setText(messageList.get(position).getTime());
            final String message_id = messageList.get(position).messageID;
            String edited_time = messageList.get(position).getEdited();
            if (edited_time != null) {
                holder.Edited_time.setVisibility(View.VISIBLE);
                holder.Edited_time.setText(edited_time);
            } else {
                holder.Edited_time.setVisibility(View.GONE);
            }

            holder.mView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewSingleAnnouncement.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("message_id", message_id);
                intent.putExtra("uid", TeacherID);
                intent.putExtra("text_message", text_message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if(messageList.size() == 0){return 1;}
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && messageList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView Text, Time, Edited_time;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Text = mView.findViewById(R.id.textView_message_text);
            Time = mView.findViewById(R.id.textView_message_time);
            Edited_time = mView.findViewById(R.id.edited_time);
        }
    }
}
