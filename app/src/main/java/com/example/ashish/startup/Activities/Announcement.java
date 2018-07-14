package com.example.ashish.startup.Activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.ashish.startup.Adapters.AttendanceListAdapter;
import com.example.ashish.startup.Adapters.MessageAdapter;
import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Announcement extends AppCompatActivity {

    private static final String TAG = "abc" ;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        mAuth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("institute")) {
            final String class_id = getIntent().getStringExtra("class_id");
            final String Institute = getIntent().getStringExtra("institute");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Announcements");
            }

            mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
            mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
            mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
            mAdapter = new MessageAdapter(messageList);
            mMessagesList = (RecyclerView) findViewById(R.id.messages_list);


            mLinearLayout = new LinearLayoutManager(this);

            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);

            mMessagesList.setAdapter(mAdapter);
            mFirestore = FirebaseFirestore.getInstance();
            String email = mAuth.getCurrentUser().getEmail();
            final String email_red = email.substring(0, email.length() - 10);

            mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Announcements").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("TAG", "New Msg: " + dc.getDocument().toObject(Message.class));
                                Message message = dc.getDocument().toObject(Message.class);
                                messageList.add(message);
                                mAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                Log.d("TAG", "Modified Msg: " + dc.getDocument().toObject(Message.class));
                                break;
                            case REMOVED:
                                Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(Message.class));
                                break;
                        }

                    }
                }
            });

            mChatSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String message = mChatMessageView.getText().toString();
                    if (!TextUtils.isEmpty(message)){
                        mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Announcements").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if (task.isSuccessful()){
                                   DocumentSnapshot document = task.getResult();
                                   Map<String, Object> data = new HashMap<>();
                                   data.put("Message", message);
                                   document.getReference().set(data);
                                   mChatMessageView.setText("");
                               }
                            }
                        });
                    }
                }
            });
        }
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
