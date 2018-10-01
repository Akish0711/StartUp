package com.example.ashish.startup.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminViewMarksListAdapter extends RecyclerView.Adapter<AdminViewMarksListAdapter.ViewHolder>{

    private List<Marks> viewMarksList ;
    private FirebaseFirestore mFirestore;
    private String marksID,class_id,email_red;

    public AdminViewMarksListAdapter(List<Marks> viewMarksList,String marksID,String class_id,String email_red) {
        this.viewMarksList = viewMarksList;
        this.marksID = marksID;
        this.class_id = class_id;
        this.email_red = email_red;
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_class,parent,false);
            return new ViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_marks,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // int viewType = getItemViewType(position);
        try {
            holder.display_name.setText(viewMarksList.get(position).getName());
            holder.marks.setText(viewMarksList.get(position).getInputMarks());

            holder.mView.setOnClickListener(v -> {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());

                alertDialog.setTitle("Edit ?");
                alertDialog.setIcon(R.drawable.ic_stat_ic_notification);
                v = LayoutInflater.from(v.getContext()).inflate(R.layout.edit_marks_dialog_view, null);
                View viewInflated = v;
                final EditText input = viewInflated.findViewById(R.id.inputEditedMarks);
                input.setOnFocusChangeListener((view, hasFocus) -> {
                    if (hasFocus) {
                        input.setHint(viewMarksList.get(position).getInputMarks());
                    } else {
                        input.setHint("");
                    }
                });

                View finalV = v;
                alertDialog.setPositiveButton("Done",
                        (dialog, which) -> {
                            String m_Text = input.getText().toString();
                            if(TextUtils.isEmpty(m_Text.trim())){
                                Toast.makeText(finalV.getContext(), "Enter mark !!",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Marks")
                                    .document(marksID).update(viewMarksList.get(position).getUsername()+"@gmail.com",m_Text).addOnSuccessListener(aVoid -> {
                                        Log.e("error","success");
                                        Toast.makeText(finalV.getContext(), "Updated Successfully",Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(finalV.getContext(), "Couldn't update.Please try again.",Toast.LENGTH_SHORT).show();
                                        Log.e("error",e.getLocalizedMessage());
                                    });
                        }
                );
                alertDialog.setNegativeButton("Cancel",
                                (dialog, which) -> {
                                    // Do the stuff..
                                }
                );
                alertDialog.setView(viewInflated);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // Now set the textchange listener for edittext
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Check if edittext is empty
                        if (TextUtils.isEmpty(s)) {
                            // Disable ok button
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            // Something into edit text. Enable the button.
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }

                    }
                });
            });
        }
        catch (Exception e){
            Log.e("",e.getLocalizedMessage());
        }

    }



    @Override
    public int getItemCount() {
        if(viewMarksList.size() == 0){
            return 1;
        }
        return viewMarksList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && viewMarksList.size() == 0) {
            return 1;
        }
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       View mView;
       public TextView display_name;
       public TextView marks;

       public ViewHolder(View itemView) {
           super(itemView);
           mView = itemView;

           display_name = mView.findViewById(R.id.display_name);
           marks = mView.findViewById(R.id.marks);
       }
    }
}
