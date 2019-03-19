package com.example.ashish.startup.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.example.ashish.startup.Adapters.UserMessageAdapter;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnouncementStudents extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private String phone, email, uid, Teacher_Name;
    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private UserMessageAdapter mAdapter;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_students);

        if (getIntent().hasExtra("subject_id") &&
                getIntent().hasExtra("subject_name") &&
                getIntent().hasExtra("Teacher_id")) {
            String class_id = getIntent().getStringExtra("subject_id");
            uid = getIntent().getStringExtra("uid");
            final String subject_name = getIntent().getStringExtra("subject_name");
            Teacher_Name = getIntent().getStringExtra("Teacher_Name");
            String TeacherID = getIntent().getStringExtra("Teacher_id");

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            if (uid==null){
                FirebaseUser user;
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                user = firebaseAuth.getCurrentUser();
                uid = user.getUid();
            }

            if (Teacher_Name==null){
                mFirestore.collection("Users").document(TeacherID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        email = document.getString("Email");
                        phone = document.getString("Phone");
                        Teacher_Name = document.getString("Name");
                    }
                });
            }else{
                mFirestore.collection("Users").document(TeacherID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        email = document.getString("Email");
                        phone = document.getString("Phone");
                    }
                });
            }

            messageList = new ArrayList<>();
            mAdapter = new UserMessageAdapter(getApplicationContext(),messageList,class_id,TeacherID);
            mMessagesList = findViewById(R.id.messages_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mLinearLayout.setReverseLayout(true);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            RelativeLayout attendance = findViewById(R.id.attendance);
            RelativeLayout exams = findViewById(R.id.marks);
            RelativeLayout contact = findViewById(R.id.contact);

            Toolbar toolbar = findViewById(R.id.user_new_class_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            AppBarLayout appBarLayout1 = findViewById(R.id.appbar_user_new_class);
            CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar_activity_user_new_class);
            collapsingToolbar.setTitle(subject_name);

            appBarLayout1.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    invalidateOptionsMenu();
                } else {
                    invalidateOptionsMenu();
                }
            });

            attendance.setOnClickListener(view -> {
                Intent intent = new Intent(AnnouncementStudents.this, UserAttendance.class);
                intent.putExtra("subject_id", class_id);
                intent.putExtra("subject_name", subject_name);
                intent.putExtra("Teacher_Name", Teacher_Name);
                startActivity(intent);
            });

            exams.setOnClickListener(view -> {
                Intent intent = new Intent(AnnouncementStudents.this, ExamStudent.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });

            contact.setOnClickListener(v -> {
                new FancyGifDialog.Builder(this)
                        .setTitle("Contact " + capitalize(Teacher_Name))
                        .setMessage(phone + "\n\n" + email)
                        .setNegativeBtnText("Mail")
                        .setPositiveBtnBackground("#55acee")
                        .setPositiveBtnText("Call")
                        .setNegativeBtnBackground("#dd4b39")
                        .setGifResource(R.drawable.gif10)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(this::verifyPermissions)
                        .OnNegativeClicked(() -> {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:"+email));
                            startActivity(emailIntent);
                        })
                        .build();
            });

            mRootRef = FirebaseDatabase.getInstance().getReference();

            loadmessage(class_id,TeacherID);
        }
    }

    private void loadmessage(String class_id, String email_red) {
        DatabaseReference messageRef = mRootRef.child(email_red).child(class_id);
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String messageKey = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class).withID(messageKey);
                messageList.add(message);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    private void verifyPermissions() {
        String[] permissions = {Manifest.permission.CALL_PHONE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phone));
            startActivity(callIntent);

        }else{
            ActivityCompat.requestPermissions(AnnouncementStudents.this, permissions, REQUEST_CODE);
        }
    }
}