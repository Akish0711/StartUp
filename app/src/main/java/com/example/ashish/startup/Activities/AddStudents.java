package com.example.ashish.startup.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.ashish.startup.Adapters.AddStudentsAdapter;
import com.example.ashish.startup.Models.Users;
import com.example.ashish.startup.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;
import java.util.List;

public class AddStudents extends AppCompatActivity {

    private ProgressBar progressBar;
    private List<String> selectedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);

        if (getIntent().hasExtra("class_id") && getIntent().hasExtra("institute") && getIntent().hasExtra("username")) {
            final String class_id = getIntent().getStringExtra("class_id");
            String Institute = getIntent().getStringExtra("institute");
            String email_red = getIntent().getStringExtra("username");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Add Students");
            }

            Button selected = findViewById(R.id.selected);
            progressBar = findViewById(R.id.progressBar);
            List<Users> usersList = new ArrayList<>();
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            AddStudentsAdapter mAdapter = new AddStudentsAdapter(this, usersList, selectedList);
            RecyclerView mMessagesList = findViewById(R.id.student_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

        }
    }

    private void notifyUser(String exception) {
        progressBar.setVisibility(View.GONE);
        KToast.errorToast(AddStudents.this,exception,Gravity.CENTER,KToast.LENGTH_SHORT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
