package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messageList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef;
    private String class_id;
    private Context context;

    public MessageAdapter(Context context, List<Message> messageList, String class_id){
        this.messageList = messageList;
        this.context = context;
        this.class_id = class_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.Text.setText(messageList.get(position).getMessage());
        final String message_id = messageList.get(position).messageID;
        holder.Time.setText(messageList.get(position).getTime());
        holder.delete_option.setOnClickListener(view -> {
            mRootRef = FirebaseDatabase.getInstance().getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            final String email = user.getEmail();
            final String email_red = email.substring(0, email.length() - 10);
            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, holder.delete_option);
            //inflating menu from xml resource
            popup.inflate(R.menu.classes_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit_option:
                        break;
                    case R.id.delete_option:
                        new AlertDialog.Builder(context)
                                .setTitle("Delete this Announcement?")
                                .setMessage("Warning : You cannot undo this.")
                                .setPositiveButton("DELETE", (dialog, which) -> {
                                    mRootRef.child("SingleAnnouncementAdmin").child(email_red).child(class_id).child(message_id).removeValue().addOnCompleteListener(task -> Toast.makeText(context, "SingleAnnouncementAdmin Deleted", Toast.LENGTH_LONG).show());
                                }).setNegativeButton("Cancel", null)
                                .show();
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView Text, Time;
        public Button delete_option;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Text = mView.findViewById(R.id.textView_message_text);
            Time = mView.findViewById(R.id.textView_message_time);
            delete_option = mView.findViewById(R.id.option_view);
        }
    }
}