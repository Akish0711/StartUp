package com.google.vision.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.vision.Adapters.ViewSingleAnnouncementAdapter;
import com.google.vision.Models.SingleMessage;
import com.google.vision.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewSingleAnnouncement extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private List<SingleMessage> messageList;
    private ViewSingleAnnouncementAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_announcement);

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("uid")&& getIntent().hasExtra("message_id")&& getIntent().hasExtra("text_message")) {
            String class_id = getIntent().getStringExtra("class_id");
            String uid = getIntent().getStringExtra("uid");
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
            mRootRef = FirebaseDatabase.getInstance().getReference();
            TextView message = findViewById(R.id.message);
            mAdapter = new ViewSingleAnnouncementAdapter(this,messageList);
            RecyclerView mMessagesList = findViewById(R.id.messages_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            message.setText(text_message);
            loadmessage(class_id, uid, message_id);
        }
    }

    private void loadmessage(String class_id, String uid, String message_id) {
        DatabaseReference messageRef = mRootRef.child(uid).child(class_id).child(message_id);
        messageRef.keepSynced(true);

        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("Name").exists()) {
                    SingleMessage message = dataSnapshot.getValue(SingleMessage.class);
                    messageList.add(message);
                    mAdapter.notifyDataSetChanged();
                }
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
