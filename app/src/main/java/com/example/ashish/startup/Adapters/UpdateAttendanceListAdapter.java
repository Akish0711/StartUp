package com.example.ashish.startup.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashish.startup.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdateAttendanceListAdapter extends RecyclerView.Adapter<UpdateAttendanceListAdapter.ViewHolder> {
    private List<String> names,status;
    private Map<String, Object> detailsMap ;
    private Set<String> keySet;
    private String email_red,classId,timeStamp;
    private Button updateAttendanceButton;
    private FirebaseFirestore mFirestore;

    public UpdateAttendanceListAdapter(Map<String, Object> hm, String email_red, String classId, String timeStamp,
                                       Button updateAttendanceButton) {
        this.detailsMap = hm;
        this.email_red = email_red;
        this.classId = classId;
        this.timeStamp = timeStamp;
        this.updateAttendanceButton = updateAttendanceButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_attendance_record,parent,false);
            return new ViewHolder(view);}
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_show_attendance_records, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2) {

            mFirestore = FirebaseFirestore.getInstance();

            names = new ArrayList<>();
            status = new ArrayList<>();

            keySet = detailsMap.keySet();

            for(String key : keySet){
                if(!key.equals("Date")){
                    names.add(key);
                    status.add(detailsMap.get(key).toString());
                }
            }

            List<String> temp = new ArrayList<>(status);

            holder.userName.setText(names.get(position));
            if(status.get(position).equals("true")){
                holder.checkBox.setImageResource(R.drawable.present);
            }
            else{
                holder.checkBox.setImageResource(R.drawable.checked);
            }
            final boolean[] showingFirst = {true};

            holder.checkBox.setOnClickListener(v -> {
                if (showingFirst[0]){
                    holder.checkBox.setImageResource(R.drawable.checked);
                    showingFirst[0] = false;
                    status.set(position,"false");
                }else{
                    holder.checkBox.setImageResource(R.drawable.present);
                    showingFirst[0] = true;
                    status.set(position,"true");
                }
            });

            List<String> updatePresentList = new ArrayList<>();
            List<String> updateAbsenttList = new ArrayList<>();

            updateAttendanceButton.setOnClickListener(v -> {
                        for (int i = 0; i < status.size(); i++) {
                            if (!status.get(i).equals(temp.get(i))) {
                                if (status.get(i).equals("false")) {
                                    updateAbsenttList.add(names.get(i));
                                } else if (status.get(i).equals("true")) {
                                    updatePresentList.add(names.get(i));
                                }
                            }
                        }

                        if (!updateAbsenttList.isEmpty() && !updatePresentList.isEmpty()) {

                        if (!updateAbsenttList.isEmpty()) {

                            final int[] counter_total = new int[1];
                            final int[] counter_present = new int[1];

                            for (int j = 0; j < updateAbsenttList.size(); j++) {
                                int finalIndex = j;
                                Map<String, Boolean> data2 = new HashMap<>();
                                data2.put(updateAbsenttList.get(finalIndex), false);

                                mFirestore.collection("Users")
                                        .document(updateAbsenttList.get(j).substring(0, updateAbsenttList.get(j).length() - 10))
                                        .collection("Subjects").document(classId).get().addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {

                                        DocumentSnapshot document = task.getResult();
                                        counter_present[0] = document.getLong("Total_Present").intValue();
                                        counter_total[0] = document.getLong("Total_Class").intValue();
                                        counter_present[0]--;
                                        int percentage = (counter_present[0] * 100 / counter_total[0]);

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("Total_Present", counter_present[0]);
                                        data.put("Percentage", percentage);
                                        document.getReference().update(data);

                                        mFirestore.collection("Users").document(email_red).collection("Subjects")
                                                .document(classId).collection("Attendance").document(timeStamp)
                                                .set(data2, SetOptions.merge())
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Toast.makeText(v.getContext(), "Absent List Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            Log.e("error in update present", e.getLocalizedMessage());
                                        });
                                    }
                                });
                            }
                        }

                        if (!updatePresentList.isEmpty()) {

                            final int[] counter_total = new int[1];
                            final int[] counter_present = new int[1];

                            for (int j = 0; j < updatePresentList.size(); j++) {
                                int finalIndex = j;
                                Map<String, Boolean> data2 = new HashMap<>();
                                data2.put(updatePresentList.get(finalIndex), true);

                                mFirestore.collection("Users")
                                        .document(updatePresentList.get(j).substring(0, updatePresentList.get(j).length() - 10))
                                        .collection("Subjects").document(classId).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot document = task.getResult();
                                        counter_present[0] = document.getLong("Total_Present").intValue();
                                        counter_total[0] = document.getLong("Total_Class").intValue();
                                        counter_present[0]++;
                                        int percentage = (counter_present[0] * 100 / counter_total[0]);

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("Total_Present", counter_present[0]);
                                        data.put("Percentage", percentage);
                                        document.getReference().update(data);

                                        mFirestore.collection("Users").document(email_red).collection("Subjects")
                                                .document(classId).collection("Attendance").document(timeStamp).set(data2, SetOptions.merge())
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Toast.makeText(v.getContext(), "Present List Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            Log.e("error in update present", e.getLocalizedMessage());
                                        });
                                    }
                                });
                            }
                        }
                    }
                    else{
                            Toast.makeText(v.getContext(), "Nothing to update. No changes made.", Toast.LENGTH_SHORT).show();
                        }
                Log.e("","update button clicked");
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && detailsMap.size() == 0) {
            return 1;
        }
        return 2;
    }

    @Override
    public int getItemCount() {
        if(detailsMap.size() == 0){
            return 1;
        }
        return (detailsMap.size()-1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView userName;
        public ImageView checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            userName = mView.findViewById(R.id.userName);
            checkBox = mView.findViewById(R.id.checkBox);
        }
    }
}
