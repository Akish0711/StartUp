package com.example.ashish.startup.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ashish.startup.Adapters.AdminMessageAdapter;
import com.example.ashish.startup.Models.SingleMessage;
import com.example.ashish.startup.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewSingleAnnouncement extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private RecyclerView mMessagesList;
    private List<SingleMessage> messageList;
    private LinearLayoutManager mLinearLayout;
    private AdminMessageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_announcement_students);

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("Teacher_Name")&& getIntent().hasExtra("message_id")&& getIntent().hasExtra("text_message")) {
            String class_id = getIntent().getStringExtra("subject_id");
            String email_red = getIntent().getStringExtra("Teacher_Name");
            String message_id = getIntent().getStringExtra("message_id");
            String text_message = getIntent().getStringExtra("text_message");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Announcements");
            }

            messageList = new ArrayList<>();
            TextView message = findViewById(R.id.message);
            mMessagesList = findViewById(R.id.messages_list);
            mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mRootRef = FirebaseDatabase.getInstance().getReference();
            message.setText(text_message);
        }
    }

    private void loadmessage(String class_id, String email_red, String message_id) {
        DatabaseReference messageRef = mRootRef.child("Announcement").child(email_red).child(class_id).child(message_id);
        messageRef.keepSynced(true);

        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
