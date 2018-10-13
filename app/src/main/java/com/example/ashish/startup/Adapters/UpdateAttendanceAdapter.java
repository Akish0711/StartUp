package com.example.ashish.startup.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashish.startup.Activities.UpdateAttendanceNew;
import com.example.ashish.startup.Models.UpdateAttendanceModel;
import com.example.ashish.startup.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UpdateAttendanceAdapter extends RecyclerView.Adapter<UpdateAttendanceAdapter.ViewHolder> {

    public List<UpdateAttendanceModel> updateAttendanceModelList;
    private FirebaseFirestore rootRef;
    private String classId,email_red;
    private List<String> names,status;

    public UpdateAttendanceAdapter(List<UpdateAttendanceModel> updateAttendanceModelList,String classId,String email_red) {
        this.updateAttendanceModelList = updateAttendanceModelList;
        this.classId = classId;
        this.email_red = email_red;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_attendance_record,parent,false);
            return new ViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_update_attendance, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2) {
            names = new ArrayList<>();
            status = new ArrayList<>();
            holder.timeStamp.setText(updateAttendanceModelList.get(position).getTimeStamp());

            rootRef = FirebaseFirestore.getInstance();

            holder.delete_record.setOnClickListener((View v) -> {


                AlertDialog.Builder alertBox = new AlertDialog.Builder(v.getRootView().getContext());
                alertBox.setTitle("Delete ?");
                alertBox.setMessage("Warning : You cannot undo this.");

                alertBox.setPositiveButton("Ok", (dialog, which) -> {
                    rootRef.collection("Users").document(email_red).collection("Subjects")
                            .document(classId).collection("Attendance")
                            .document(updateAttendanceModelList.get(position).getTimeStamp()).delete().addOnCompleteListener(task -> {
                        Toast.makeText(v.getContext(),"Record deleted successfully.",Toast.LENGTH_SHORT).show();
                            }
                    )
                            .addOnFailureListener(e -> {
                                Log.e("Record not deleted",e.getLocalizedMessage());
                                Toast.makeText(v.getContext(),"Couldn't delete record.Try again.",Toast.LENGTH_SHORT).show();
                            });
                    dialog.dismiss();
                });

                AlertDialog dialog = alertBox.create();
                dialog.show();
            });

            holder.mView.setOnClickListener(v -> {
                names.clear();
                status.clear();

                Intent intent = new Intent(v.getContext(), UpdateAttendanceNew.class);
                intent.putExtra("classId",classId);
                intent.putExtra("email_red",email_red);
                intent.putExtra("timeStamp",updateAttendanceModelList.get(position).getTimeStamp());
                v.getContext().startActivity(intent);
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && updateAttendanceModelList.size() == 0) {
            return 1;
        }
        return 2;
    }

    @Override
    public int getItemCount() {
        if(updateAttendanceModelList.size() == 0){
            return 1;
        }
        return updateAttendanceModelList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView timeStamp;
        public ImageView delete_record;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            timeStamp = mView.findViewById(R.id.timeStamp);
            delete_record = mView.findViewById(R.id.delete_record);
        }
    }
}
