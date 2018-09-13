package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.ashish.startup.Adapters.MessageAdapter;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NewClass2 extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private ImageView add_students,take_attendance, marks;
    private FloatingActionButton announcement;
    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private List<String> keyList;
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private DatabaseReference mRootRef;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    private boolean appBarExpanded = true;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class2);

        if (getIntent().hasExtra("class_id")){
            final String class_id = getIntent().getStringExtra("class_id");

            Toolbar toolbar = findViewById(R.id.new_class2_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            appBarLayout = findViewById(R.id.appbar_new_class2);
            collapsingToolbar = findViewById(R.id.collapsing_toolbar_activity_new_class2);
            collapsingToolbar.setTitle("Your Class");

            messageList = new ArrayList<>();
            keyList = new ArrayList<>();
            mFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mRootRef = FirebaseDatabase.getInstance().getReference();

            add_students = findViewById(R.id.add_students_image);
            take_attendance = findViewById(R.id.take_attendance_image);
            marks = findViewById(R.id.marks_image);
            announcement = findViewById(R.id.announcement);
            mAdapter = new MessageAdapter(this,messageList,class_id);
            mMessagesList = findViewById(R.id.messages_list);
            mRefreshLayout = findViewById(R.id.message_swipe_layout);
            mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            });

            String email = mAuth.getCurrentUser().getEmail();
            final String email_red = email.substring(0, email.length() - 10);
            final String[] Institute = new String[1];

            mRootRef.child("Chat").child(email_red).child(class_id).child("seen").setValue(true);
            loadmessage(class_id,email_red);

            mRefreshLayout.setOnRefreshListener(() -> {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages(class_id,email_red);
            });

            mFirestore.collection("Users").document(email_red).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Institute[0] = document.getString("Institute");
                    }
                }
            });

            add_students.setOnClickListener(view -> {
                Intent intent = new Intent(NewClass2.this,AddStudents.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",Institute[0]);
                startActivity(intent);
            });

            take_attendance.setOnClickListener(view -> {
                Intent intent = new Intent(NewClass2.this,TakeAttendance.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",Institute[0]);
                intent.putExtra("username", email_red);
                startActivity(intent);
            });

            marks.setOnClickListener(v -> {
                Intent intent = new Intent(NewClass2.this,UploadMarks.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",Institute[0]);
                startActivity(intent);
            });

            announcement.setOnClickListener(v -> {
                Intent intent = new Intent(NewClass2.this,Announcement.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("institute",Institute[0]);
                startActivity(intent);
            });
        }
    }

    private void loadMoreMessages(String class_id, String email_red) {
        DatabaseReference messageRef = mRootRef.child("Announcement").child(email_red).child(class_id);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String messageKey = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class).withID(messageKey);

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

                String messageKey = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class).withID(messageKey);

                itemPos++;
                if(itemPos == 1){
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }
                messageList.add(message);
                keyList.add(dataSnapshot.getKey());
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messageList.size() - 1);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = keyList.indexOf(dataSnapshot.getKey());
                messageList.remove(index);
                keyList.remove(index);
                mAdapter.notifyDataSetChanged();
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
