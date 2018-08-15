package com.example.ashish.startup.Activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.ashish.startup.Adapters.MessageAdapter;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementStudents extends AppCompatActivity {

    private String class_id;
    private String email_red;
    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private DatabaseReference mRootRef;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_students);

        messageList = new ArrayList<>();

        if (getIntent().hasExtra("subject_id")&& getIntent().hasExtra("Teacher_Name")) {
            class_id = getIntent().getStringExtra("subject_id");
            email_red = getIntent().getStringExtra("Teacher_Name");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Announcements");
            }

            mAdapter = new MessageAdapter(getApplicationContext(),messageList);
            mMessagesList = findViewById(R.id.messages_list);
            mRefreshLayout = findViewById(R.id.message_swipe_layout);
            mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            mMessagesList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        mMessagesList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (messageList.size() > 0) {
                                    mMessagesList.smoothScrollToPosition(messageList.size() - 1);
                                }else {
                                    mMessagesList.smoothScrollToPosition(0);
                                }
                            }
                        }, 100);
                    }
                }
            });

            mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.child("Chat").child(email_red).child(class_id).child("seen").setValue(true);

            loadmessage(class_id,email_red);

            mRootRef.child("Chat").child(email_red).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(class_id)){

                        Map chatAddMap = new HashMap();
                        chatAddMap.put("seen", false);
                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                        Map chatUserMap = new HashMap();
                        chatUserMap.put("Chat/"+email_red+"/"+class_id,chatAddMap);

                        mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError!=null){

                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mCurrentPage++;
                    itemPos = 0;
                    loadMoreMessages(class_id,email_red);
                }
            });
        }
    }

    private void loadMoreMessages(String class_id, String email_red) {
        DatabaseReference messageRef = mRootRef.child("Announcement").child(email_red).child(class_id);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){
                    messageList.add(itemPos++, message);
                } else {
                    mPrevKey = mLastKey;
                }

                if(itemPos == 1) {
                    mLastKey = messageKey;
                }
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10, 0);
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

    private void loadmessage(String class_id, String email_red) {
        DatabaseReference messageRef = mRootRef.child("Announcement").child(email_red).child(class_id);
        messageRef.keepSynced(true);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);
                itemPos++;
                if(itemPos == 1){
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }

                messageList.add(message);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messageList.size() - 1);
                mRefreshLayout.setRefreshing(false);
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
