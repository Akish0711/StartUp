package com.example.ashish.startup.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ashish.startup.Adapters.ClassesListAdapter;
import com.example.ashish.startup.Models.Classes;
import com.example.ashish.startup.R;
import com.example.ashish.startup.authentication.Login;
import com.example.ashish.startup.others.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivityTeacher extends AppCompatActivity {

    private ClassesListAdapter classesListAdapter;
    private List<Classes> classesList;
    private List<String> keyList;
    String name, uid,admin_uid;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_teacher);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        FirebaseUser user;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()== null){
            finish();
            startActivity(new Intent(this, Login.class));
        }

        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        TextView name_field = findViewById(R.id.student_name);
        parentLayout = findViewById(R.id.teacher_main_parent);
        TextView id = findViewById(R.id.student_id);
        ImageView imgProfile = findViewById(R.id.img_profile);

        rootRef.collection("Users").document(uid).addSnapshotListener((documentSnapshot, e) -> {
            name = documentSnapshot.getString("Name");
            name_field.setText(name);
            id.setText(documentSnapshot.getString("Username").toLowerCase());

            if (user.getPhotoUrl()!=null) {
                Glide.with(getApplicationContext())
                        .load(user.getPhotoUrl())
                        .error(R.drawable.teacher_icon_male)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(MainActivityTeacher.this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);
            }else if (documentSnapshot.getString("Gender").equals("Male")){
                Glide.with(getApplicationContext())
                        .load(R.drawable.teacher_icon_male)
                        .crossFade()
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);
            }else {
                Glide.with(getApplicationContext())
                        .load(R.drawable.teacher_icon_female)
                        .crossFade()
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);
            }
        });

        FloatingActionButton new_class = findViewById(R.id.new_class);
        RecyclerView recyclerView = findViewById(R.id.main_list);
        classesList = new ArrayList<>();
        keyList = new ArrayList<>();
        classesListAdapter = new ClassesListAdapter(this,classesList,uid,parentLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(classesListAdapter);
        LinearLayout institute = findViewById(R.id.institute_teacher);

        rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                admin_uid = document.getString("Admin_Uid");
            }
        });

        new_class.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityTeacher.this,NewClass.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        rootRef.collection("Users").document(uid).collection("Subjects").addSnapshotListener((documentSnapshots, e) -> {
            for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                switch (doc.getType()) {
                    case ADDED:
                        String class_id = doc.getDocument().getId();
                        Classes classes = doc.getDocument().toObject(Classes.class).withID(class_id);
                        classesList.add(classes);
                        keyList.add(class_id);
                        classesListAdapter.notifyDataSetChanged();
                        break;
                    case MODIFIED:
                        class_id = doc.getDocument().getId();
                        int index = keyList.indexOf(class_id);
                        Classes classes2 = doc.getDocument().toObject(Classes.class).withID(class_id);
                        classesList.set(index, classes2);
                        classesListAdapter.notifyDataSetChanged();
                        break;
                    case REMOVED:
                        class_id = doc.getDocument().getId();
                        int index2 = keyList.indexOf(class_id);
                        classesList.remove(index2);
                        keyList.remove(index2);
                        classesListAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        institute.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityTeacher.this,InstituteOthers.class);
            intent.putExtra("uid", uid);
            intent.putExtra("admin_uid",admin_uid);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings_main) {
            Intent intent = new Intent(MainActivityTeacher.this,SettingsAdmin.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
