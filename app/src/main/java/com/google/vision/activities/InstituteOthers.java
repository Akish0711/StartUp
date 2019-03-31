package com.google.vision.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.vision.Adapters.UserMessageAdapter;
import com.google.vision.Models.Message;
import com.google.vision.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InstituteOthers extends AppCompatActivity {

    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private UserMessageAdapter mAdapter;
    private DatabaseReference mRootRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_others);
        if (getIntent().hasExtra("admin_uid")){
            uid = getIntent().getStringExtra("uid");
            String admin_uid = getIntent().getStringExtra("admin_uid");

            if (uid==null){
                FirebaseUser user;
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                user = firebaseAuth.getCurrentUser();
                uid = user.getUid();
            }

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Institute");
            }

            messageList = new ArrayList<>();
            mRootRef = FirebaseDatabase.getInstance().getReference();
            String class_id = "Institute";
            messageList = new ArrayList<>();
            mAdapter = new UserMessageAdapter(getApplicationContext(),messageList,class_id,admin_uid);
            mMessagesList = findViewById(R.id.main_list);
            LinearLayout birthdays = findViewById(R.id.birthdays);
            LinearLayout enquiries = findViewById(R.id.enquiries);
            LinearLayout payments = findViewById(R.id.payments);

            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mLinearLayout.setReverseLayout(true);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            loadmessage(class_id,admin_uid);

            birthdays.setOnClickListener(v -> {
                Intent intent = new Intent(this, FeedbackAdmin.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });

            payments.setOnClickListener(v -> {
                Intent intent = new Intent(this, PaymentAdmin.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });

            enquiries.setOnClickListener(v -> {
                Intent intent = new Intent(this, EnquiryAdmin.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            });
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
