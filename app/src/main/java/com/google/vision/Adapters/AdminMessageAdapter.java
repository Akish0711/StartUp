package com.google.vision.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.vision.activities.EditAnnouncement;
import com.google.vision.activities.ViewSingleAnnouncement;
import com.google.vision.Models.Message;
import com.google.vision.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminMessageAdapter extends RecyclerView.Adapter<AdminMessageAdapter.ViewHolder> {

    private List<Message> messageList;
    private DatabaseReference mRootRef;
    private String class_id, uid;
    private Context context;

    public AdminMessageAdapter(Context context, List<Message> messageList, String class_id, String uid){
        this.messageList = messageList;
        this.context = context;
        this.class_id = class_id;
        this.uid = uid;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2) {

            String text_message = messageList.get(position).getMessage();
            String text_time = messageList.get(position).getTime();
            String edited_time = messageList.get(position).getEdited();
            if (edited_time != null) {
                holder.Edited_time.setVisibility(View.VISIBLE);
                holder.Edited_time.setText(edited_time);
            } else {
                holder.Edited_time.setVisibility(View.GONE);
            }

            holder.Text.setText(text_message);
            final String message_id = messageList.get(position).messageID;
            holder.Time.setText(messageList.get(position).getTime());
            holder.mView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewSingleAnnouncement.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("uid", uid);
                intent.putExtra("message_id", message_id);
                intent.putExtra("text_message", text_message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            holder.edit.setOnClickListener(v -> {
                mRootRef = FirebaseDatabase.getInstance().getReference();
                Intent intent = new Intent(context, EditAnnouncement.class);
                intent.putExtra("uid", uid);
                intent.putExtra("class_id", class_id);
                intent.putExtra("message_id", message_id);
                intent.putExtra("text_message", text_message);
                intent.putExtra("text_time", text_time);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            holder.delete.setOnClickListener(v -> {
                mRootRef = FirebaseDatabase.getInstance().getReference();
                new AlertDialog.Builder(context)
                        .setTitle("Delete this Announcement?")
                        .setMessage("Warning : You cannot undo this.")
                        .setPositiveButton("DELETE", (dialog, which) -> mRootRef.child(uid).child(class_id).child(message_id).removeValue()
                                .addOnCompleteListener(task -> Toast.makeText(context, "Announcement Deleted", Toast.LENGTH_SHORT).show()))
                        .setNegativeButton("Cancel", null)
                        .show();
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
        ImageButton edit, delete;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Text = mView.findViewById(R.id.textView_message_text);
            Time = mView.findViewById(R.id.textView_message_time);
            Edited_time = mView.findViewById(R.id.edited_time);
            edit = mView.findViewById(R.id.edit);
            delete = mView.findViewById(R.id.delete);

        }
    }
}