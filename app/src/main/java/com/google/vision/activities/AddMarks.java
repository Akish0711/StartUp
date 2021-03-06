package com.google.vision.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.vision.Adapters.AddMarksAdpater;
import com.google.vision.Models.Marks;
import com.google.vision.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddMarks extends AppCompatActivity {

    EditText name_exam, max_marks;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    List<Marks> marksList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();
    List<String> usernameList = new ArrayList<>();
    List<String> uidList = new ArrayList<>();
    List<Double> userMarks = new ArrayList<>();
    private String class_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        if (getIntent().hasExtra("class_id")) {
            class_id = getIntent().getStringExtra("class_id");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Add Exams");
            }

            name_exam = findViewById(R.id.exam_name);
            max_marks = findViewById(R.id.max_marks);

            AddMarksAdpater mAdapter = new AddMarksAdpater(this, marksList, userMarks, nameList, usernameList, uidList);
            RecyclerView mMainList = findViewById(R.id.student_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(mAdapter);

            mFirestore.collection("Attendance").document(class_id).collection("Students").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()) {
                        Marks marks = document.toObject(Marks.class);
                        marksList.add(marks);
                        Collections.sort(marksList, new Comparator<Marks>() {
                            public int compare(Marks o1, Marks o2) {
                                return extractInt(o1.getUsername()) - extractInt(o2.getUsername());
                            }

                            int extractInt(String s) {
                                String num = s.replaceAll("\\D", "");
                                // return 0 if no digits found
                                return num.isEmpty() ? 0 : Integer.parseInt(num);
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_button) {
            String examName = name_exam.getText().toString();
            String maxMarks = max_marks.getText().toString();
            if (examName.isEmpty()){
                KToast.warningToast(AddMarks.this, "Exam Name Required", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                name_exam.requestFocus();
            }else if (maxMarks.isEmpty()){
                KToast.warningToast(AddMarks.this, "Max Marks Required", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                max_marks.requestFocus();
            }else if (userMarks.contains(null)){
                KToast.warningToast(AddMarks.this, "Marks cannot be empty", Gravity.BOTTOM, KToast.LENGTH_SHORT);
            }else{

                DateFormat buttonTextFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                String date = buttonTextFormat.format(Calendar.getInstance().getTime());
                int maxMarksInt = Integer.parseInt(maxMarks);
                double max = userMarks.get(0);
                double min = userMarks.get(0);
                int sum = 0;

                // Iterate through all elements and add them to sum
                for (int i = 0; i < userMarks.size(); i++) {
                    sum += userMarks.get(i);
                }

                for (int i = 1; i < userMarks.size(); i++) {
                    if (userMarks.get(i) > max) {
                        max = userMarks.get(i);
                    }
                    if (userMarks.get(i) < min) {
                        min = userMarks.get(i);
                    }
                }

                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                float output = (float) sum/(float) userMarks.size();
                String avg = formatter.format(output);
                float average = Float.parseFloat(avg);
                        /*Double.parseDouble(avg);
                        Integer.parseInt(avg);*/

                if (max>maxMarksInt){
                    KToast.warningToast(AddMarks.this, "Marks cannot be greater than Maximum Marks", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                }else{
                    Map<String, Object> exam = new HashMap<>();
                    exam.put("Name", examName);
                    exam.put("Max_Marks", maxMarks);
                    exam.put("Highest", max);
                    exam.put("Lowest", min);
                    exam.put("Average", average);
                    exam.put("Date", date);
                    exam.put("TimeStamp", FieldValue.serverTimestamp());
                    exam.put("Class_id",class_id);

                    DocumentReference ref = mFirestore.collection("Marks").document();
                    String myId = ref.getId();
                    mFirestore.collection("Marks").document(myId).set(exam).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (int x=0;x<uidList.size();x++){
                                Map<String, Object> data = new HashMap<>();
                                data.put("Marks",userMarks.get(x));
                                data.put("Name", nameList.get(x));
                                data.put("Username", usernameList.get(x));
                                data.put("Uid", uidList.get(x));

                                mFirestore.collection("Marks").document(myId).collection("Students").document(uidList.get(x)).set(data);
                            }
                        }
                    }).addOnCompleteListener(task -> {
                        KToast.successToast(AddMarks.this, "Marks Submitted", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        finish();
                    });
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
