package com.google.vision.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FieldValue;
import com.google.vision.activities.AnnouncementAdmin;
import com.google.vision.Models.Classes;
import com.google.vision.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ViewHolder> {
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public ClassesListAdapter() {
        // uncomment the line below if you want to open only one row at a time
        //viewBinderHelper.setOpenOnlyOne(true);
    }

    private List<Classes> classesList;
    public Context context;
    private DatabaseReference mRootRef;
    private String uid, Institute, admin_uid;
    private View parentLayout;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public ClassesListAdapter(Context context,List<Classes> classesList,String uid, View parentLayout){
        this.classesList = classesList;
        this.context = context;
        this.uid = uid;
        this.parentLayout = parentLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_no_class, parent, false);
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

            String class_id = classesList.get(position).classID;
            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            viewBinderHelper.bind(holder.swipeLayout, class_id);
            viewBinderHelper.setOpenOnlyOne(true);

            String class_name = classesList.get(position).getName();
            String section = classesList.get(position).getSection();
            int check = Integer.parseInt(classesList.get(position).getTotal_Students());
            String total_students = "";
            if (check>1) {
                total_students = classesList.get(position).getTotal_Students() + " Students";
            }else{
                total_students = classesList.get(position).getTotal_Students() + " Student";
            }
            String batch = classesList.get(position).getBatch();
            Institute = classesList.get(position).getInstitute();

            holder.nameText.setText(class_name);
            holder.sectionText.setText(section);
            holder.total_students_Text.setText(total_students);

            String finalTotal_students = classesList.get(position).getTotal_Students();
            holder.frontLayout.setOnClickListener(view -> {
                Intent intent = new Intent(context,AnnouncementAdmin.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("Student_Count", finalTotal_students);
                intent.putExtra("class_name",class_name);
                intent.putExtra("section", section);
                intent.putExtra("uid", uid);
                intent.putExtra("institute",Institute);
                intent.putExtra("batch",batch);
                context.startActivity(intent);
            });

            holder.edit.setOnClickListener(v -> {
                if (isInternetAvailable()) {
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setTitle("Edit Class");
                    final LayoutInflater layoutInflater = LayoutInflater.from(context);
                    final View attnView = layoutInflater.inflate(R.layout.activity_rename, null);
                    final EditText rename_text = attnView.findViewById(R.id.rename);
                    rename_text.setText(classesList.get(position).getName());
                    builder.setMessage("Changes made here will be reflected on Student side")
                            .setCancelable(false)
                            .setPositiveButton("SAVE", (dialog, id) -> {
                                String displayName = rename_text.getText().toString();

                                if (displayName.equals(classesList.get(position).getName())) {
                                    rename_text.setError("Name is still same");
                                    rename_text.requestFocus();
                                    return;
                                }

                                if (displayName.isEmpty()) {
                                    rename_text.setError("Name required");
                                    rename_text.requestFocus();
                                    return;
                                }

                                final Map<String, Object> data = new HashMap<>();
                                data.put("Name", displayName);

                                final Map<String, Object> data2 = new HashMap<>();
                                data2.put("Subject_Name", displayName);

                                mFirestore.collection("Users").document(uid).collection("Subjects").document(class_id).update(data);
                                mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute + "_No").get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (final DocumentSnapshot document : task.getResult()) {
                                            document.getReference().collection("Subjects").document(class_id).update(data2);
                                        }
                                    }
                                });
                                Toast.makeText(context, "Class Edited", Toast.LENGTH_LONG).show();
                            });
                    builder.setView(attnView);
                    builder.setNegativeButton("Cancel", null);

                    // builder.setIcon(R.drawable.record);
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();

                }
            });

            holder.delete.setOnClickListener(v -> {
                if (isInternetAvailable()) {
                    mRootRef = FirebaseDatabase.getInstance().getReference();
                    new AlertDialog.Builder(context)
                            .setTitle("Delete this Class?")
                            .setMessage("Warning : You cannot undo this.")
                            .setCancelable(false)
                            .setPositiveButton("DELETE", (dialog, id) ->

                                    mFirestore.collection("Users").document(uid).collection("Subjects")
                                            .document(class_id).delete().addOnCompleteListener(task1 -> {
                                        mFirestore.collection("Users").whereEqualTo(class_id,"Added").get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            document.getReference().update(class_id, FieldValue.delete());
                                                            document.getReference().collection("Subjects").document(class_id).delete();
                                                        }
                                                    }
                                                });
                                        mFirestore.collection("Marks").whereEqualTo("Class_id", class_id).get().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    document.getReference().delete();
                                                }
                                            }
                                        });

                                        mFirestore.collection("Attendance").whereEqualTo("Class_id", class_id).get().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    document.getReference().delete();
                                                }
                                            }
                                        });

                                        mFirestore.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                admin_uid = document.getString("Admin_Uid");
                                            }
                                        }).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){
                                                mFirestore.collection("Current Classes").document(admin_uid).collection("Classes").document(class_id).delete();
                                            }
                                        });
                                        mRootRef.child("Announcement").child(uid).child(class_id).removeValue();
                                        KToast.successToast((Activity) context, "Class Deleted", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                    }).addOnFailureListener(e -> Toast.makeText(context, "Error in deleting classes", Toast.LENGTH_SHORT).show()))
                            .setNegativeButton("Cancel", null)
                            .show();
                }else{
                    Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = process.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
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
        TextView nameText, sectionText, total_students_Text;
        public ImageView edit,delete;
        private View frontLayout;
        private SwipeRevealLayout swipeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            frontLayout = itemView.findViewById(R.id.front_layout);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            nameText = mView.findViewById(R.id.name_text);
            edit = mView.findViewById(R.id.edit);
            delete = mView.findViewById(R.id.delete);
            sectionText = mView.findViewById(R.id.section_text);
            total_students_Text = mView.findViewById(R.id.total_students_number);
        }
    }
}
