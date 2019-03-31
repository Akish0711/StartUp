package com.google.vision.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.vision.Adapters.SubjectsListAdapter;
import com.google.vision.Models.Subject;
import com.google.vision.R;
import com.google.vision.authentication.Login;
import com.google.vision.others.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class MainActivityStudent extends AppCompatActivity {

    private SubjectsListAdapter subjectsListAdapter;
    private List<Subject> subjectList;
    private List<String> keyList;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    String uid, admin_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

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

        RecyclerView recyclerView = findViewById(R.id.main_list);
        keyList = new ArrayList<>();
        subjectList = new ArrayList<>();
        subjectsListAdapter = new SubjectsListAdapter(this,subjectList, uid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(subjectsListAdapter);
        TextView name = findViewById(R.id.student_name);
        TextView id = findViewById(R.id.student_id);
        ImageView imgProfile = findViewById(R.id.img_profile);
        LinearLayout institute = findViewById(R.id.institute_student);

        rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                admin_uid = document.getString("Admin_Uid");
            }
        });

        rootRef.collection("Users").document(uid).addSnapshotListener((documentSnapshot, e) -> {
            name.setText(documentSnapshot.getString("Name"));
            id.setText(documentSnapshot.getString("Username").toLowerCase());

            if (user.getPhotoUrl()!=null) {
                Glide.with(getApplicationContext())
                        .load(user.getPhotoUrl())
                        .error(R.drawable.student_male)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(MainActivityStudent.this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);
            }else if (documentSnapshot.getString("Gender").equals("Male")){
                Glide.with(getApplicationContext())
                        .load(R.drawable.student_male)
                        .crossFade()
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);
            }else {
                Glide.with(getApplicationContext())
                        .load(R.drawable.student_female)
                        .crossFade()
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    rootRef.collection("Users").document(uid).update("token", token);

                });

        rootRef.collection("Users").document(uid).collection("Subjects").addSnapshotListener((documentSnapshots, e) -> {
            for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                switch (doc.getType()) {
                    case ADDED:
                        String subjectID = doc.getDocument().getId();
                        Subject subjects = doc.getDocument().toObject(Subject.class).withID(subjectID);
                        subjectList.add(subjects);
                        subjectsListAdapter.notifyDataSetChanged();
                        keyList.add(subjectID);
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        subjectID = doc.getDocument().getId();
                        int index = keyList.indexOf(subjectID);
                        subjectList.remove(index);
                        keyList.remove(index);
                        subjectsListAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        institute.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityStudent.this,InstituteOthers.class);
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
            Intent intent = new Intent(MainActivityStudent.this,SettingsAdmin.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
