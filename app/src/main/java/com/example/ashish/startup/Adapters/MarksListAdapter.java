package com.example.ashish.startup.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Activities.ViewMarks;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.List;

public class MarksListAdapter extends RecyclerView.Adapter<MarksListAdapter.ViewHolder> {

    public List<Marks> testList;
    public Context context;
    public String email_red,c,institute;
    private FirebaseFirestore mFirestore;

    public MarksListAdapter(Context context, List<Marks> marksList,String email_red,String c,String institute){
        this.testList = marksList;
        this.context = context;
        this.email_red = email_red;
        this.institute = institute;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class, parent, false);
            return new ViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tests, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if(viewType == 2) {
            holder.test_name.setText(testList.get(position).getMarksID());
            holder.max_marks.setText(testList.get(position).getMax_marks());

            final String marks_id = testList.get(position).marksID;
            mFirestore = FirebaseFirestore.getInstance();

            holder.mView.setOnClickListener(view -> {
                Intent intent = new Intent(context,ViewMarks.class);
                intent.putExtra("marksID", marks_id);
                intent.putExtra("class_id",c);
                intent.putExtra("email_red",email_red);
                intent.putExtra("institute",institute);
                context.startActivity(intent);
            });

            holder.mView.setOnLongClickListener(view -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete this test ?")
                        .setMessage("Warning : You cannot undo this.")
                        .setCancelable(false)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            mFirestore.collection("Users").document(email_red).collection("Subjects")
                                    .document(c).collection("Marks").document(testList.get(position).getMarksID()).delete().addOnSuccessListener(aVoid ->
                                    KToast.infoToast((Activity) context, "Test Deleted", Gravity.BOTTOM,KToast.LENGTH_LONG))
                                    .addOnFailureListener(e -> {
                                        KToast.errorToast((Activity) context, "Couldn't Delete.Try Again !!", Gravity.BOTTOM,KToast.LENGTH_LONG);
                                    });
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        if (testList.size() == 0) {
            return 1;
        } else return testList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && testList.size() == 0) {
            return 1;
        }
        return 2;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView test_name;
        public TextView max_marks;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            test_name = mView.findViewById(R.id.tName);
            max_marks = mView.findViewById(R.id.mMarksValue);
        }
    }
}
