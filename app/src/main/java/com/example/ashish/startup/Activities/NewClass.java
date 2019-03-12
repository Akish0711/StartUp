package com.example.ashish.startup.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewClass extends AppCompatActivity {

    private EditText name_class;
    ProgressBar progressBar;
    String uid;
    Spinner spinner;
    View parentLayout;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    final String[] Institute = new String[1];
    private List<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");

            classList = new ArrayList<>();
            classList.add("Class");

            name_class = findViewById(R.id.name_class);
            Button new_class = findViewById(R.id.new_class);
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            parentLayout = findViewById(R.id.new_class_parent);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Create New Class");
            }

            spinner = findViewById(R.id.spinner);

            rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Institute[0] = document.getString("Institute");
                }
            });

            rootRef.collection("Institute").document(uid).collection("Classes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (final DocumentSnapshot document : task.getResult()) {
                            String Name = document.getString("Name");
                            classList.add(Name);
                        }
                    }
                }
            }).addOnCompleteListener(task -> {
                ArrayAdapter<String> myAdapter = new ArrayAdapter<>(NewClass.this,
                        android.R.layout.simple_list_item_1, classList);

                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(myAdapter);
            });

            /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    spinner.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });*/

            new_class.setOnClickListener(view -> createNewClass());
        }
    }

    private void createNewClass() {
        final String className = name_class.getText().toString();
        String section = spinner.getSelectedItem().toString();
        final String[] batch = new String[1];

        if (className.isEmpty()){
            name_class.setError("Name required");
            name_class.requestFocus();
        }else if (spinner == null || spinner.getSelectedItem() ==null || section.equals("Class")) {
            KToast.warningToast(this,"Class Required",Gravity.BOTTOM,KToast.LENGTH_SHORT);
        }else if (!isInternetAvailable()){
            Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            rootRef.collection("Important").document("Batch").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    batch[0] = document.getString(section);
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final Map<String, Object> subject = new HashMap<>();
                    subject.put("Name", className);
                    subject.put("Section", section);
                    subject.put("Total_Students", "0");
                    subject.put("Batch", batch[0]);
                    subject.put("Institute", Institute[0]);

                    DocumentReference ref = rootRef.collection("Users").document(uid).collection("Subjects").document();
                    String myId = ref.getId();
                    rootRef.collection("Users").document(uid).collection("Subjects").document(myId).set(subject).addOnCompleteListener(task1 -> {
                    }).addOnCompleteListener(task12 -> rootRef.collection("Users").whereEqualTo("Batch", batch[0]).get().addOnCompleteListener(task121 -> {
                        if (task121.isSuccessful()){
                            for (final DocumentSnapshot document : task121.getResult()) {
                                String docID = document.getId();
                                rootRef.collection("Users").document(docID).update(myId, "Removed");
                            }
                        }
                    })).addOnCompleteListener(task13 -> {
                        final Map<String, Object> newClass = new HashMap<>();
                        newClass.put("Name", className);
                        newClass.put("Batch", batch[0]);
                        newClass.put("Class_id", myId);

                        rootRef.collection("Institute").document(uid).collection("Current Classes").document().set(newClass);

                        progressBar.setVisibility(View.INVISIBLE);
                        KToast.successToast(NewClass.this, "New Class Created", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        finish();
                    });
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

    private void notifyUser(String error) {
        progressBar.setVisibility(View.INVISIBLE);
        KToast.errorToast(NewClass.this,error,Gravity.BOTTOM,KToast.LENGTH_SHORT);
    }

    public void onFailure(@NonNull Exception e) {
        if (!isInternetAvailable()){
            notifyUser("Internet connection required for this action.");
        }
        else if (e instanceof FirebaseAuthInvalidUserException) {
            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();
            //   if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
            if ("ERROR_USER_NOT_FOUND".equals(errorCode)) {
                notifyUser("No matching Account found");
            } else if ("ERROR_USER_DISABLED".equals(errorCode)) {
                notifyUser("User account has been disabled");
            } else {
                notifyUser(e.getLocalizedMessage());
            }
        }
        else{
            notifyUser("Error with the servers. Please close the app and try again.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
