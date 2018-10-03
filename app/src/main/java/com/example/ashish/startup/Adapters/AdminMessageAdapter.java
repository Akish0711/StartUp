package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.content.Intent;
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

import com.example.ashish.startup.Activities.EditAnnouncement;
import com.example.ashish.startup.Activities.ViewSingleAnnouncement;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminMessageAdapter extends RecyclerView.Adapter<AdminMessageAdapter.ViewHolder> {

    private List<Message> messageList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef;
    private String class_id, email_red;
    private Context context;

    public AdminMessageAdapter(Context context, List<Message> messageList, String class_id, String email_red){
        this.messageList = messageList;
        this.context = context;
        this.class_id = class_id;
        this.email_red = email_red;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String text_message = messageList.get(position).getMessage();
        String text_time = messageList.get(position).getTime();
        String edited_time = messageList.get(position).getEdited();
        if (edited_time!=null){
            holder.Edited_time.setVisibility(View.VISIBLE);
            holder.Edited_time.setText(edited_time);
        }else{
            holder.Edited_time.setVisibility(View.GONE);
        }

        holder.Text.setText(text_message);
        final String message_id = messageList.get(position).messageID;
        holder.Time.setText(messageList.get(position).getTime());
        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(context,ViewSingleAnnouncement.class);
            intent.putExtra("class_id", class_id);
            intent.putExtra("Teacher_Name", email_red);
            intent.putExtra("message_id",message_id);
            intent.putExtra("text_message",text_message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
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
                        Intent intent = new Intent(context, EditAnnouncement.class);
                        intent.putExtra("email_red", email_red);
                        intent.putExtra("class_id",class_id);
                        intent.putExtra("message_id", message_id);
                        intent.putExtra("text_message",text_message);
                        intent.putExtra("text_time",text_time);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case R.id.delete_option:
                        new AlertDialog.Builder(context)
                                .setTitle("Delete this Announcement?")
                                .setMessage("Warning : You cannot undo this.")
                                .setPositiveButton("DELETE", (dialog, which) -> mRootRef.child("Announcement").child(email_red).child(class_id).child(message_id).removeValue().addOnCompleteListener(task -> Toast.makeText(context, "Announcement Deleted", Toast.LENGTH_LONG).show())).setNegativeButton("Cancel", null)
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
        TextView Text, Time, Edited_time;
        public Button delete_option;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Text = mView.findViewById(R.id.textView_message_text);
            Time = mView.findViewById(R.id.textView_message_time);
            delete_option = mView.findViewById(R.id.option_view);
            Edited_time = mView.findViewById(R.id.edited_time);
        }
    }
}