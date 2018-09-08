package com.example.ashish.startup.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.ashish.startup.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Announcement extends AppCompatActivity {

    private EditText mChatMessageView;
    private static final int CHOOSE_IMAGE = 101 ;
    private static final int PICK_IMAGE_CAMERA =188 ;
    private static final int PICK_ATTACHMENT = 102;
    private DatabaseReference mRootRef;
    private String class_id;
    private String email_red;
    private String profileImageUrl;
    private static final int REQUEST_CODE = 1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("class_id")) {
            class_id = getIntent().getStringExtra("class_id");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Announcements");
            }

            ImageButton mChatSendBtn = findViewById(R.id.chat_send_btn);
            mChatMessageView = findViewById(R.id.chat_message_view);
            FloatingActionButton mFabSendAttachment = findViewById(R.id.fab_send_attachment);
            String email = mAuth.getCurrentUser().getEmail();
            email_red = email.substring(0, email.length() - 10);

            mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.child("Chat").child(email_red).child(class_id).child("seen").setValue(true);

            mChatSendBtn.setOnClickListener(v -> {
                String message = mChatMessageView.getText().toString();
                DateFormat df1=new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                final String time=df1.format(Calendar.getInstance().getTime());
                if (!TextUtils.isEmpty(message)){

                    String user_ref = "Announcement/"+email_red+"/"+class_id;

                    DatabaseReference user_message_push = mRootRef.child("Announcement")
                            .child(email_red).child(class_id).push();

                    String push_id = user_message_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("Message", message);
                    messageMap.put("Time",time);
                    messageMap.put("Type", 1);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(user_ref+"/"+push_id,messageMap);

                    mChatMessageView.setText("");

                    mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                        if (databaseError!=null){

                        }
                    });
                    finish();
                }
            });
            mFabSendAttachment.setOnClickListener(view -> verifyPermissions());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setCancelable(false);

        Uri uriProfileImage;
        if(requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null){
            uriProfileImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(uriProfileImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            selectedImage = getResizedBitmap(selectedImage, 1500);// 400 is for example, replace with desired size
            uriProfileImage = getImageUri(getApplicationContext(), selectedImage);

            DateFormat df1=new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            final String time=df1.format(Calendar.getInstance().getTime());

            final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("message/"+email_red+"/"+System.currentTimeMillis()+"jpg");

            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Uri downloadUrl = uri;
                profileImageUrl = downloadUrl.toString();
                String user_ref = "Announcement/"+email_red+"/"+class_id;

                DatabaseReference user_message_push = mRootRef.child("Announcement")
                        .child(email_red).child(class_id).push();

                String push_id = user_message_push.getKey();
                Map messageMap = new HashMap();
                messageMap.put("Message", profileImageUrl);
                messageMap.put("Type", 2);
                messageMap.put("Time",time);

                Map messageUserMap = new HashMap();
                messageUserMap.put(user_ref+"/"+push_id,messageMap);

                mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                    if (databaseError!=null){

                    }
                });
                progressDialog.dismiss();
            }))
                    .addOnFailureListener(e -> KToast.errorToast(Announcement.this,e.getMessage(), Gravity.BOTTOM,KToast.LENGTH_SHORT)).addOnProgressListener(taskSnapshot -> {
                        progressDialog.show();
                        int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setProgress(currentProgress);
                    });

        }else if (requestCode == PICK_IMAGE_CAMERA && resultCode == Activity.RESULT_OK ) {
            int targetW = 590;
            int targetH = 590;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            uriProfileImage = getImageUri(getApplicationContext(), bitmap);

            DateFormat df1=new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            final String time=df1.format(Calendar.getInstance().getTime());

            final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("message/"+email_red+"/"+System.currentTimeMillis()+"jpg");

            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Uri downloadUrl = uri;
                profileImageUrl = downloadUrl.toString();
                String user_ref = "Announcement/"+email_red+"/"+class_id;

                DatabaseReference user_message_push = mRootRef.child("Announcement")
                        .child(email_red).child(class_id).push();

                String push_id = user_message_push.getKey();
                Map messageMap = new HashMap();
                messageMap.put("Message", profileImageUrl);
                messageMap.put("Type", 2);
                messageMap.put("Time",time);

                Map messageUserMap = new HashMap();
                messageUserMap.put(user_ref+"/"+push_id,messageMap);

                mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                    if (databaseError!=null){

                    }
                });
                progressDialog.dismiss();
            }))
                    .addOnFailureListener(e -> KToast.errorToast(Announcement.this,e.getMessage(), Gravity.BOTTOM,KToast.LENGTH_SHORT)).addOnProgressListener(taskSnapshot -> {
                        progressDialog.show();
                        int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setProgress(currentProgress);
                    });
        }else if (requestCode == PICK_ATTACHMENT && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri_pdf = data.getData();
            String uriString = uri_pdf.toString();
            File myFile = new File(uriString);
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getApplicationContext().getContentResolver().query(uri_pdf, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("myPDFfile", displayName)
                    .build();

            DateFormat df1=new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            final String time=df1.format(Calendar.getInstance().getTime());

            final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("message/"+email_red+"/"+System.currentTimeMillis()+displayName);

            final String finalDisplayName = displayName;
            profileImageRef.putFile(uri_pdf,metadata).addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Uri downloadUrl = uri;
                profileImageUrl = downloadUrl.toString();
                String user_ref = "Announcement/"+email_red+"/"+class_id;

                DatabaseReference user_message_push = mRootRef.child("Announcement")
                        .child(email_red).child(class_id).push();

                String push_id = user_message_push.getKey();
                Map messageMap = new HashMap();
                messageMap.put("Message", profileImageUrl);
                messageMap.put("Name", finalDisplayName);
                messageMap.put("Type", 3);
                messageMap.put("Time",time);

                Map messageUserMap = new HashMap();
                messageUserMap.put(user_ref+"/"+push_id,messageMap);

                mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                    if (databaseError!=null){

                    }
                });
                progressDialog.dismiss();

            })).addOnFailureListener(e -> KToast.errorToast(Announcement.this,e.getMessage(), Gravity.BOTTOM,KToast.LENGTH_SHORT)).addOnProgressListener(taskSnapshot -> {
                progressDialog.show();
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void showImageChooser() {
        final CharSequence[] options = {"Camera", "Choose From Gallery", "Documents", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Select Photo From:");
        builder.setItems(options, (dialog, item) -> {
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
            }else if (options[item].equals("Documents")){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, PICK_ATTACHMENT);
            }else if (options[item].equals("Cancel")) {
                dialog.dismiss();
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

    private void verifyPermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED){
            showImageChooser();
        }else{
            ActivityCompat.requestPermissions(Announcement.this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}
