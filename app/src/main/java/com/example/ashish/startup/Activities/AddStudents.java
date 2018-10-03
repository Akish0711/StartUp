package com.example.ashish.startup.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashish.startup.Adapters.UsersListAdapter;
import com.example.ashish.startup.Models.Users;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStudents extends AppCompatActivity implements UsersListAdapter.UsersAdapterListener {

    private FirebaseFirestore mFirestore;
    private UsersListAdapter usersListAdapter;
    private List<Users> usersList;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView n_record_text;
    private ImageView n_record_image;
    private boolean ascending = true;
    final boolean[] alreadyExecuted = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);

        final String[] subject = {""};

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("institute")) {
            final String class_id = getIntent().getStringExtra("class_id");
            String Institute = getIntent().getStringExtra("institute");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Add Students");
            }

            usersList = new ArrayList<>();
            usersListAdapter = new UsersListAdapter(this, usersList, this);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            RecyclerView mMainList = findViewById(R.id.student_list);
            Button selected = findViewById(R.id.selected);
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            n_record_image = findViewById(R.id.no_record_image);
            n_record_text = findViewById(R.id.no_record_text);

            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setItemAnimator(new DefaultItemAnimator());
            mMainList.setAdapter(usersListAdapter);
            mFirestore = FirebaseFirestore.getInstance();

            String email = mAuth.getCurrentUser().getEmail();

            mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute+"_No").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (final DocumentSnapshot document : task.getResult()) {
                        document.getReference().collection("Subjects").document(class_id).get().addOnCompleteListener(task12 -> {
                            if (task12.isSuccessful()){
                                DocumentSnapshot doc = task12.getResult();
                                if (doc == null || !doc.exists()) {
                                    document.getReference().get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            progressBar.setVisibility(View.GONE);
                                            DocumentSnapshot doc1 = task1.getResult();
                                            Users users = doc1.toObject(Users.class);
                                            usersList.add(users);
                                            Collections.sort(usersList, Users.BY_NAME_ALPHABETICAL);
                                            usersListAdapter.notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(e -> notifyUser(e.getLocalizedMessage()));
                                }
                                else{
                                    final Handler[] handler = {new Handler()};
                                    handler[0].postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            if(usersList.size() == 0){
                                                if(!alreadyExecuted[0]) {
                                                    alreadyExecuted[0] = true;
                                                    progressBar.setVisibility(View.GONE);
                                                    n_record_image.setVisibility(View.VISIBLE);
                                                    n_record_text.setVisibility(View.VISIBLE);

                                                    //                notifyUser("All the registered users have already been added.");
                                                }
                                            }
                                        }
                                    }, 1000);
                                    Log.e("Position : ","inside else");
                                }
                            }

                        }).addOnFailureListener(e -> notifyUser(e.getLocalizedMessage()));
                    }
                }
            }).addOnFailureListener(e -> notifyUser(e.getLocalizedMessage()));

            final String email_red = email.substring(0, email.length() - 10);
            mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).get()
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    subject[0] = document.getString("Name");
                }
            });

            selected.setOnClickListener(view -> {
                List list = usersListAdapter.getSelectedItem();
                if (list.size() > 0) {
                    int index = 0;
                    for (index = 0; index < list.size(); index++) {
                        final Users model = (Users) list.get(index);
                        mFirestore.collection("Users").whereEqualTo("Username", model.getUsername()).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("Teacher_Name", email_red);
                                    data.put("Subject_Name", subject[0]);
                                    data.put("Total_Present",0);
                                    data.put("Total_Class",0);
                                    data.put("Percentage",0);
                                    data.put("Name", model.getName());
                                    data.put("Username", model.getUsername());
                                    document.getReference().collection("Subjects").document(class_id).set(data);
                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // notifyUser(e.getLocalizedMessage());
                            }
                        });

                        Map<String, Object> student = new HashMap<>();
                        student.put("Name", model.getName());
                        student.put("Username", model.getUsername());
                        mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id)
                                .collection("Students").document().set(student);
                    }
                    if (index == 1) {
                        KToast.successToast(AddStudents.this, index + " Student Added",Gravity.BOTTOM ,KToast.LENGTH_AUTO);
                    }
                    if (index > 1) {
                        KToast.successToast(AddStudents.this, index + " Students Added",Gravity.BOTTOM ,KToast.LENGTH_AUTO);
                    }
                    finish();
                    startActivity(new Intent(AddStudents.this, MainActivity.class));
                } else {
                    notifyUser("Please select a Student");
                }
            });

        }
    }

    private void notifyUser(String exception) {
        progressBar.setVisibility(View.GONE);
        KToast.errorToast(AddStudents.this,exception,Gravity.CENTER,KToast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                usersListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                usersListAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            onBackPressed();
            return true;
        }
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onUsersSelected(Users users) {
        Toast.makeText(getApplicationContext(), "Selected: " + users.getName() + ", " + users.getUsername(), Toast.LENGTH_SHORT).show();
    }
}

