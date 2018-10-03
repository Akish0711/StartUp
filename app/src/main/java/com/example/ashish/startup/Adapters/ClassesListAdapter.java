package com.example.ashish.startup.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashish.startup.Activities.AnnouncementAdmin;
import com.example.ashish.startup.Activities.MainActivity;
import com.example.ashish.startup.Models.Classes;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ViewHolder> {

    private List<Classes> classesList;
    private FirebaseFirestore mFirestore;
    public Context context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef;

    public ClassesListAdapter(Context context,List<Classes> classesList){
        this.classesList = classesList;
        this.context = context;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2){

            holder.nameText.setText(classesList.get(position).getName());
            final String class_id = classesList.get(position).classID;
            final String class_name = classesList.get(position).getName();
            holder.mView.setOnClickListener(view -> {
                Intent intent = new Intent(context,AnnouncementAdmin.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("class_name",class_name);
                context.startActivity(intent);
            });

            holder.option_view.setOnClickListener(view -> {
                mFirestore = FirebaseFirestore.getInstance();
                mRootRef = FirebaseDatabase.getInstance().getReference();
                firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                final String email = user.getEmail();
                final String email_red = email.substring(0, email.length() - 10);
                final String[] Institute = new String[1];
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.option_view);
                //inflating menu from xml resource
                popup.inflate(R.menu.classes_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.edit_option:
                            mFirestore.collection("Users").document(email_red).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Institute[0] = document.getString("Institute");
                                }
                            });
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setTitle("Edit Class");
                            final LayoutInflater layoutInflater = LayoutInflater.from(context);
                            final View attnView = layoutInflater.inflate(R.layout.activity_rename,null);
                            final EditText rename_text =attnView.findViewById(R.id.rename);
                            rename_text.setText(classesList.get(position).getName());
                            builder.setMessage("Changes made here will be reflected on Student side")
                                    .setCancelable(false)
                                    .setPositiveButton("SAVE", (dialog, id) -> {
                                        String displayName = rename_text.getText().toString();

                                        if (displayName.equals(classesList.get(position).getName())){
                                            rename_text.setError("Name is still same");
                                            rename_text.requestFocus();
                                            return;
                                        }

                                        if (displayName.isEmpty()){
                                            rename_text.setError("Name required");
                                            rename_text.requestFocus();
                                            return;
                                        }

                                        final Map<String, Object> data = new HashMap<>();
                                        data.put("Name",displayName);

                                        final Map<String, Object> data2 = new HashMap<>();
                                        data2.put("Subject_Name",displayName);

                                        mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).update(data);
                                        mFirestore.collection("Users").whereEqualTo("Institute_Admin",Institute[0]+"_No").get().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){
                                                for (final DocumentSnapshot document : task.getResult()) {
                                                    document.getReference().collection("Subjects").document(class_id).update(data2);
                                                }
                                            }
                                        });
                                        Toast.makeText(context, "Class Edited", Toast.LENGTH_LONG).show();
                                        ((Activity)context).finish();
                                        context.startActivity(new Intent(context, MainActivity.class));
                                    });
                            builder.setView(attnView);
                            builder.setNegativeButton("Cancel",null);

                            // builder.setIcon(R.drawable.record);
                            android.app.AlertDialog alert = builder.create();
                            alert.show();
                            break;
                        case R.id.delete_option:
                            mFirestore.collection("Users").document(email_red).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Institute[0] = document.getString("Institute");
                                }
                            });
                            new AlertDialog.Builder(context)
                                    .setTitle("Delete this Class?")
                                    .setMessage("Warning : You cannot undo this.")
                                    .setCancelable(false)
                                    .setPositiveButton("DELETE", (dialog, id) ->
                                        mFirestore.collection("Users").document(email_red).collection("Subjects")
                                                                                                     .document(class_id).delete().addOnSuccessListener(aVoid -> {
                                            mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute[0] +"_No").get()
                                                                                                                                     .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    for (final DocumentSnapshot document : task.getResult()) {
                                                        document.getReference().collection("Subjects").document(class_id).delete();
                                                    }
                                                }
                                            });
                                            mRootRef.child("Announcement").child(email_red).child(class_id).removeValue();
                                            mRootRef.child("Chat").child(email_red).child(class_id).removeValue();
                                            KToast.successToast((Activity) context, "Class Deleted", Gravity.BOTTOM,KToast.LENGTH_LONG);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(context, "Error in deleting classes", Toast.LENGTH_LONG).show()))
                                    .setNegativeButton("Cancel", null)
                                    .show();
                            break;
                    }
                    return false;
                });
                //displaying the popup
                popup.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        if(classesList.size() == 0){return 1;}
        return classesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && classesList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView nameText;
        public Button option_view;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            nameText = mView.findViewById(R.id.name_text);
            option_view = mView.findViewById(R.id.option_view);
        }
    }
}
