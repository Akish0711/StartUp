package com.example.ashish.startup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.example.ashish.startup.Adapters.AdminMessageAdapter;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementAdmin extends AppCompatActivity {

    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private List<String> keyList;
    private AdminMessageAdapter mAdapter;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_admin);

        if (getIntent().hasExtra("class_id")
                &&getIntent().hasExtra("class_name")
                &&getIntent().hasExtra("uid")
                &&getIntent().hasExtra("institute")
                &&getIntent().hasExtra("batch")
                &&getIntent().hasExtra("Student_Count")){
            final String class_id = getIntent().getStringExtra("class_id");
            final String class_name = getIntent().getStringExtra("class_name");
            String uid = getIntent().getStringExtra("uid");
            String batch = getIntent().getStringExtra("batch");
            String institute = getIntent().getStringExtra("institute");
            String total_students = getIntent().getStringExtra("Student_Count");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar_activity_new_class2);
            collapsingToolbar.setTitle(class_name);

            messageList = new ArrayList<>();
            keyList = new ArrayList<>();
            mRootRef = FirebaseDatabase.getInstance().getReference();

            RelativeLayout add_students = findViewById(R.id.add_students);
            RelativeLayout take_attendance = findViewById(R.id.take_attendance);
            RelativeLayout marks = findViewById(R.id.marks);
            RelativeLayout remove = findViewById(R.id.remove);
            android.support.design.widget.FloatingActionButton announcement = findViewById(R.id.announcement);
            mAdapter = new AdminMessageAdapter(this,messageList,class_id,uid);
            mMessagesList = findViewById(R.id.messages_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mLinearLayout.setReverseLayout(true);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            loadmessage(class_id,uid);

            add_students.setOnClickListener(view -> {
                Intent intent = new Intent(AnnouncementAdmin.this,AddStudents.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",institute);
                intent.putExtra("uid", uid);
                intent.putExtra("batch", batch);
                intent.putExtra("class_name",class_name);
                intent.putExtra("Student_Count", total_students);
                startActivity(intent);
            });

            take_attendance.setOnClickListener(view -> {
                Intent intent = new Intent(AnnouncementAdmin.this,TakeAttendance.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });

            marks.setOnClickListener(v -> {
                Intent intent = new Intent(AnnouncementAdmin.this, ExamsAdmin.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",institute);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });

            remove.setOnClickListener(v -> {
                Intent intent = new Intent(AnnouncementAdmin.this,RemoveStudents.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("uid", uid);
                intent.putExtra("institute",institute);
                startActivity(intent);
            });

            announcement.setOnClickListener(v -> {
                Intent intent = new Intent(AnnouncementAdmin.this,MakeAnnouncement.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });
        }
    }

    private void loadmessage(String class_id, String username) {
        DatabaseReference messageRef = mRootRef.child(username).child(class_id);
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String messageKey = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class).withID(messageKey);
                messageList.add(message);
                keyList.add(dataSnapshot.getKey());
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                String messageKey = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class).withID(messageKey);
                int index = keyList.indexOf(messageKey);
                messageList.set(index, message);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = keyList.indexOf(dataSnapshot.getKey());
                messageList.remove(index);
                keyList.remove(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
