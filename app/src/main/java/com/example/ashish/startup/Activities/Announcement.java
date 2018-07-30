package com.example.ashish.startup.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Announcement extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private static final int CHOOSE_IMAGE = 101 ;
    private static final int PICK_IMAGE_CAMERA =188 ;
    private FloatingActionButton mFabSendImage;
    private DatabaseReference mRootRef;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private String class_id;
    private String email_red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        mAuth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();

        if (getIntent().hasExtra("class_id")) {
            class_id = getIntent().getStringExtra("class_id");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Announcements");
            }

            mChatSendBtn = findViewById(R.id.chat_send_btn);
            mChatMessageView = findViewById(R.id.chat_message_view);
            mFabSendImage = findViewById(R.id.fab_send_image);
            mAdapter = new MessageAdapter(messageList);
            mMessagesList = findViewById(R.id.messages_list);
            mRefreshLayout = findViewById(R.id.message_swipe_layout);
            mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);
            String email = mAuth.getCurrentUser().getEmail();
            email_red = email.substring(0, email.length() - 10);

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

            mChatSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = mChatMessageView.getText().toString();
                    if (!TextUtils.isEmpty(message)){

                        String user_ref = "Announcement/"+email_red+"/"+class_id;

                        DatabaseReference user_message_push = mRootRef.child("Announcement")
                                .child(email_red).child(class_id).push();

                        String push_id = user_message_push.getKey();

                        Map messageMap = new HashMap();
                        messageMap.put("Message", message);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(user_ref+"/"+push_id,messageMap);

                        mChatMessageView.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError!=null){

                                }
                            }
                        });
                    }
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

            mFabSendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageChooser();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null){
            // upload image to firebase and retrive

        }else if (requestCode == PICK_IMAGE_CAMERA && resultCode == Activity.RESULT_OK ) {
            // upload image to firebase and retrive

        }
    }

    private void showImageChooser() {
        final CharSequence[] options = {"Camera", "Choose From Gallery", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Select Photo From:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose From Gallery")) {
                    dialog.dismiss();
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, CHOOSE_IMAGE);
                }else if (options[item].equals("Camera")){
                    dialog.dismiss();
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager())!= null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(Announcement.this,
                                    "com.example.ashish.startup.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
                        }
                    }
                }else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
