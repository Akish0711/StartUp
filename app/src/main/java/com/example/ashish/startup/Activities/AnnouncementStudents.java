package com.example.ashish.startup.Activities;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.ashish.startup.Adapters.UserMessageAdapter;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnouncementStudents extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private String phone, email;
    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private LinearLayoutManager mLinearLayout;
    private UserMessageAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private DatabaseReference mRootRef;
    private ImageView view_atttendance,view_marks,contact;
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
        setContentView(R.layout.activity_user_new_class);

        if (getIntent().hasExtra("subject_id") && getIntent().hasExtra("subject_name") && getIntent().hasExtra("Teacher_Name")) {
            String class_id = getIntent().getStringExtra("subject_id");
            final String subject_name = getIntent().getStringExtra("subject_name");
            String Teacher_Name = getIntent().getStringExtra("Teacher_Name");

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("Users").document(Teacher_Name).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    email = document.getString("Email");
                    phone = document.getString("Phone");
                }
            });

            messageList = new ArrayList<>();
            mAdapter = new UserMessageAdapter(getApplicationContext(),messageList,class_id,Teacher_Name);
            mMessagesList = findViewById(R.id.messages_list);
            mRefreshLayout = findViewById(R.id.message_swipe_layout);
            mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            view_atttendance = findViewById(R.id.view_attendance_image);
            view_marks = findViewById(R.id.view_marks_image);
            contact = findViewById(R.id.contact_image);

            Toolbar toolbar = findViewById(R.id.user_new_class_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            appBarLayout = findViewById(R.id.appbar_user_new_class);
            collapsingToolbar = findViewById(R.id.collapsing_toolbar_activity_user_new_class);
            collapsingToolbar.setTitle(subject_name);

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

            view_atttendance.setOnClickListener(view -> {
                Intent intent = new Intent(AnnouncementStudents.this, UserAttendance.class);
                intent.putExtra("subject_id", class_id);
                intent.putExtra("subject_name", subject_name);
                intent.putExtra("Teacher_Name", Teacher_Name);
                startActivity(intent);
            });

            view_marks.setOnClickListener(view -> {
                Intent intent = new Intent(AnnouncementStudents.this, UserMarks.class);
                intent.putExtra("subject_id", class_id);
                intent.putExtra("subject_name", subject_name);
                intent.putExtra("Teacher_Name", Teacher_Name);
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
                        .OnPositiveClicked(() -> verifyPermissions())
                        .OnNegativeClicked(() -> {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:"+email));
                            startActivity(emailIntent);
                        })
                        .build();
            });

            mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.child("Chat").child(Teacher_Name).child(class_id).child("seen").setValue(true);

            loadmessage(class_id,Teacher_Name);

            mRefreshLayout.setOnRefreshListener(() -> {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages(class_id,Teacher_Name);
            });
        }
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