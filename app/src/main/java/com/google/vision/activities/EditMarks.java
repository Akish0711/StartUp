package com.google.vision.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.vision.Adapters.EditMarksAdapter;
import com.google.vision.Models.Marks;
import com.google.vision.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditMarks extends AppCompatActivity {

    private String class_id, exam_id;
    EditText name_exam, max_marks;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    List<Marks> marksList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();
    List<String> usernameList = new ArrayList<>();
    List<Double> userMarks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        if (getIntent().hasExtra("class_id")
                && getIntent().hasExtra("exam_id")
                && getIntent().hasExtra("exam_name")
                && getIntent().hasExtra("max_marks")) {
            class_id = getIntent().getStringExtra("class_id");
            exam_id = getIntent().getStringExtra("exam_id");
            String exam_name = getIntent().getStringExtra("exam_name");
            String maxMarks = getIntent().getStringExtra("max_marks");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Edit Exam");
            }

            name_exam = findViewById(R.id.exam_name);
            max_marks = findViewById(R.id.max_marks);

            name_exam.setText(exam_name);
            max_marks.setText(maxMarks);

            EditMarksAdapter mAdapter = new EditMarksAdapter(this, marksList, userMarks, nameList, usernameList);
            RecyclerView mMainList = findViewById(R.id.student_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(mAdapter);

            mFirestore.collection("Marks").document(class_id).collection("Exams").document(exam_id).collection("Students").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (final DocumentSnapshot document : task.getResult()) {
                        Marks marks = document.toObject(Marks.class);
                        marksList.add(marks);
                        Collections.sort(marksList, Marks.BY_NAME_ALPHABETICAL);
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
            final String examName = name_exam.getText().toString();
            final String maxMarks = max_marks.getText().toString();
            if (examName.isEmpty()){
                KToast.warningToast(EditMarks.this, "Exam Name Required", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                name_exam.requestFocus();
            }else if (maxMarks.isEmpty()){
                KToast.warningToast(EditMarks.this, "Max Marks Required", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                max_marks.requestFocus();
            }else if (userMarks.contains(null)){
                KToast.warningToast(EditMarks.this, "Marks cannot be empty", Gravity.BOTTOM, KToast.LENGTH_SHORT);
            }else{

                DateFormat buttonTextFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                String date = buttonTextFormat.format(Calendar.getInstance().getTime());
                final int maxMarksInt = Integer.parseInt(maxMarks);
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
                    KToast.warningToast(EditMarks.this, "Marks cannot be greater than Maximum Marks", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                }else{
                    final Map<String, Object> exam = new HashMap<>();
                    exam.put("Name", examName);
                    exam.put("Max_Marks", maxMarks);
                    exam.put("Highest", max);
                    exam.put("Lowest", min);
                    exam.put("Average", average);
                    exam.put("Date", date);
                    exam.put("TimeStamp", FieldValue.serverTimestamp());

                    mFirestore.collection("Marks").document(class_id).collection("Exams").document(exam_id).set(exam).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (int x=0;x<usernameList.size();x++){
                                final Map<String, Object> data = new HashMap<>();
                                data.put("Marks",userMarks.get(x));
                                data.put("Name", nameList.get(x));
                                data.put("Username", usernameList.get(x));
                                mFirestore.collection("Marks").document(class_id).collection("Exams")
                                        .document(exam_id).collection("Students").document(usernameList.get(x)).set(data);
                            }
                        }
                    }).addOnCompleteListener(task -> {
                        KToast.successToast(EditMarks.this, "Marks Submitted", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        finish();
                    });
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}