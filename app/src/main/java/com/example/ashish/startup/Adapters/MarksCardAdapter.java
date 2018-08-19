package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MarksCardAdapter extends RecyclerView.Adapter<MarksCardAdapter.ViewHolder> {
    public List<Marks> testList;
    public Context context;
    private FirebaseFirestore rootRef;
    String subId,tId,email;

    public MarksCardAdapter(Context context, List<Marks> marksList,String subID,String tID,String email){
        this.testList = marksList;
        this.context = context;
        this.subId = subID;
        this.tId = tID;
        this.email = email;
    }

    @Override
    public MarksCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class,parent,false);
            return new ViewHolder(view);}
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tests, parent, false);
            return new ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        if(viewType == 2){
            holder.test_name.setText(testList.get(position).getMarksID());
            holder.max_marks.setText(testList.get(position).getMax_marks());

            rootRef = FirebaseFirestore.getInstance();

            rootRef.collection("Users").document(tId).collection("Subjects").document(subId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        final DocumentSnapshot documentSnapshot = task.getResult();
                        documentSnapshot.getReference().collection("Marks").document(testList.get(position).getMarksID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();
                                    String s = doc.getString(email);
                                    holder.marks_obtained.setText(s);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(testList.size() == 0){return 1;}
           return testList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && testList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView test_name, max_marks, marks_obtained;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            test_name = mView.findViewById(R.id.tName);
            max_marks = mView.findViewById(R.id.mMarksValue);
            marks_obtained = mView.findViewById(R.id.myImageViewText);
        }
    }
}
