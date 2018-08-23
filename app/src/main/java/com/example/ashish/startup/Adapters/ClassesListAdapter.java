package com.example.ashish.startup.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashish.startup.Activities.FullScreenImage;
import com.example.ashish.startup.Activities.MainActivity;
import com.example.ashish.startup.Activities.NewClass2;
import com.example.ashish.startup.Activities.TakeAttendance;
import com.example.ashish.startup.Authentication.Login;
import com.example.ashish.startup.Models.Classes;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.List;

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ViewHolder> {

    public List<Classes> classesList;
    private FirebaseFirestore mFirestore;
    public Context context;
    private FirebaseAuth firebaseAuth;

    public ClassesListAdapter(Context context,List<Classes> classesList){
        this.classesList = classesList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == 2){

            holder.nameText.setText(classesList.get(position).getName());
            final String class_id = classesList.get(position).classID;
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,NewClass2.class);
                    intent.putExtra("class_id", class_id);
                    context.startActivity(intent);
                }
            });

            holder.option_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, holder.option_view);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.classes_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.rename_option:
                                    break;
                                case R.id.delete_option:
                                    mFirestore = FirebaseFirestore.getInstance();
                                    firebaseAuth = FirebaseAuth.getInstance();
                                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String email = user.getEmail();
                                    final String email_red = email.substring(0, email.length() - 10);
                                    final String[] Institute = new String[1];
                                    mFirestore.collection("Users").document(email_red).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                Institute[0] = document.getString("Institute");
                                            }
                                        }
                                    });
                                    new AlertDialog.Builder(context)
                                            .setTitle("Delete this Class?")
                                            .setMessage("Warning : You cannot undo this.")
                                            .setCancelable(false)
                                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Intent intent= new Intent(context,MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(intent);
                                                            KToast.successToast((Activity) context, "Class Deleted", Gravity.BOTTOM,KToast.LENGTH_LONG);

                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(context, "Error in deleting classes", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                    mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute[0] +"_No").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (final DocumentSnapshot document : task.getResult()) {
                                                                    document.getReference().collection("Subjects").document(class_id).delete();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
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
        public TextView nameText;
        public Button option_view;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            nameText = mView.findViewById(R.id.name_text);
            option_view = mView.findViewById(R.id.option_view);
        }
    }
}
