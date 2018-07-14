package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public List<Message> messageList;
    public Context context;

    public MessageAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.message.setText(messageList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView message;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            message = mView.findViewById(R.id.messgae_text_layout);


        }
    }
}
