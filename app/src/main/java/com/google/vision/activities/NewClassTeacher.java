package com.google.vision.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.vision.R;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewClassTeacher extends AppCompatActivity {

    private EditText name_class;
    ProgressBar progressBar;
    String uid, admin_uid;
    Spinner spinner;
    View parentLayout;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    final String[] Code = new String[1];
    private List<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class_teacher);

        if (getIntent().hasExtra("uid") && getIntent().hasExtra("admin_uid")) {
            uid = getIntent().getStringExtra("uid");
            admin_uid = getIntent().getStringExtra("admin_uid");

            classList = new ArrayList<>();
            classList.add("Class");

            name_class = findViewById(R.id.name_class);
            Button new_class = findViewById(R.id.new_class);
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            parentLayout = findViewById(R.id.new_class_teacher_parent);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Create New Class");
            }

            spinner = findViewById(R.id.spinner);

            rootRef.collection("Institute Classes").document(admin_uid).collection("Classes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (final DocumentSnapshot document : task.getResult()) {
                        String Name = document.getString("Name");
                        classList.add(Name);
                    }
                }
            }).addOnCompleteListener(task -> {
                ArrayAdapter<String> myAdapter = new ArrayAdapter<>(NewClassTeacher.this,
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
            KToast.warningToast(this,"Class Required", Gravity.BOTTOM,KToast.LENGTH_SHORT);
        }else if (!isInternetAvailable()){
            Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
        }else {
            progressBar.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.VISIBLE);

            rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Code[0] = document.getString("Code");
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    rootRef.collection("Important").document("Batch").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            DocumentSnapshot document = task1.getResult();
                            batch[0] = document.getString(section);
                        }
                    }).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            final Map<String, Object> subject = new HashMap<>();
                            subject.put("Name", className);
                            subject.put("Section", section);
                            subject.put("Total_Students", "0");
                            subject.put("Batch", batch[0]);

                            DocumentReference ref = rootRef.collection("Users").document(uid).collection("Subjects").document();
                            String myId = ref.getId();
                            rootRef.collection("Users").document(uid).collection("Subjects").document(myId).set(subject).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    rootRef.collection("Users").whereEqualTo("Code_Student", Code[0] + "_Yes").whereEqualTo("Batch", batch[0]).get().addOnCompleteListener(task121 -> {
                                        if (task121.isSuccessful()) {
                                            for (DocumentSnapshot document : task121.getResult()) {
                                                String docID = document.getId();
                                                rootRef.collection("Users").document(docID).update(myId, "Removed");
                                            }
                                        }
                                    }).addOnCompleteListener(task13 -> {
                                        if (task13.isSuccessful()) {
                                            Map<String, Object> newClass = new HashMap<>();
                                            newClass.put("Batch", batch[0]);
                                            newClass.put("Class_id", myId);

                                            rootRef.collection("Attendance").document(myId).set(newClass);
                                            rootRef.collection("Current Classes").document(admin_uid).collection("Classes").document(myId).set(newClass);

                                            progressBar.setVisibility(View.INVISIBLE);
                                            KToast.successToast(NewClassTeacher.this, "New Class Created", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
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
        KToast.errorToast(NewClassTeacher.this,error,Gravity.BOTTOM,KToast.LENGTH_SHORT);
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
