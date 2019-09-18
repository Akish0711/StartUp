package com.google.vision.activities;

import android.Manifest;
import android.app.Activity;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.vision.Adapters.MakeAnnouncementAdapter;
import com.google.vision.Models.SingleMessage;
import com.google.vision.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditAnnouncement extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;
    private static final int PICK_IMAGE_CAMERA =188 ;
    private static final int PICK_ATTACHMENT = 102;
    private static final int REQUEST_CODE = 1;
    private DatabaseReference mRootRef;
    private String class_id;
    private String uid;
    private String message_id;
    private String text_time;
    private String text_message;
    private String profileImageUrl;
    private EditText mChatMessageView;
    Uri uriProfileImage;
    private List<Uri> listUri;
    private List<Uri> listUriPdf;
    private List<String> listStringUri;
    private List<String> listStringUriPdf;
    private List<String> listpos;
    private List<String> fileNameList;
    private MakeAnnouncementAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_announcement);

        if (getIntent().hasExtra("uid")
                && getIntent().hasExtra("class_id")
                && getIntent().hasExtra("message_id")
                && getIntent().hasExtra("text_message")
                && getIntent().hasExtra("text_time")) {
            uid = getIntent().getStringExtra("uid");
            class_id = getIntent().getStringExtra("class_id");
            message_id = getIntent().getStringExtra("message_id");
            text_message = getIntent().getStringExtra("text_message");
            text_time = getIntent().getStringExtra("text_time");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Edit");
            }

            fileNameList = new ArrayList<>();
            listUri = new ArrayList<>();
            listUriPdf = new ArrayList<>();
            listStringUri = new ArrayList<>();
            listStringUriPdf = new ArrayList<>();
            listpos = new ArrayList<>();
            mChatMessageView = findViewById(R.id.chat_message_view);

            mRootRef = FirebaseDatabase.getInstance().getReference();
            mAdapter = new MakeAnnouncementAdapter(fileNameList,listUri,listUriPdf,listpos,listStringUri,listStringUriPdf,this);
            RecyclerView mMessagesList = findViewById(R.id.messages_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            mChatMessageView.setText(text_message);
            loadmessage(class_id, uid, message_id);
        }
    }

    private void loadmessage(String class_id, String uid, String message_id) {
        DatabaseReference messageRef = mRootRef.child(uid).child(class_id).child(message_id);
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("Name").exists()) {
                    SingleMessage message = dataSnapshot.getValue(SingleMessage.class);
                    String url = message.getURL();
                    Uri uri_url = Uri.parse(url);

                    if (message.getType()==1){
                        listUri.add(uri_url);
                        fileNameList.add(message.getName());
                        listpos.add("image");
                        listUriPdf.add(null);
                        listStringUri.add(url);
                        listStringUriPdf.add(null);
                        mAdapter.notifyDataSetChanged();
                    }
                    else{
                        listUriPdf.add(uri_url);
                        fileNameList.add(message.getName());
                        listpos.add("pdf");
                        listUri.add(null);
                        listStringUri.add(null);
                        listStringUriPdf.add(url);
                        mAdapter.notifyDataSetChanged();
                    }
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

    private void uploadAnnouncement() {
        String message = mChatMessageView.getText().toString();
        DateFormat df1=new SimpleDateFormat("MMM dd (hh:mm a)", Locale.ENGLISH);
        final String time=df1.format(Calendar.getInstance().getTime());
        if (!TextUtils.isEmpty(message)){
            String message_ref = uid+"/"+class_id;
            String attachment_ref = uid + "/" + class_id + "/" + message_id;

            Map messageMap = new HashMap();
            messageMap.put("Message", message);
            messageMap.put("Time",text_time);
            messageMap.put("Edited","Edited: "+time);

            Map messageUserMap = new HashMap();
            messageUserMap.put(message_ref+"/"+message_id,messageMap);

            mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                if (databaseError!=null){

                }
            });

            for (int x=0; x<listStringUri.size();x++){
                if (listStringUri.get(x)!=null) {
                    DatabaseReference user_message_push = mRootRef.child(uid).child(class_id).child(message_id).push();
                    String push_id = user_message_push.getKey();

                    Map StringImageMap = new HashMap();
                    StringImageMap.put("URL", listStringUri.get(x));
                    StringImageMap.put("Name", fileNameList.get(x));
                    StringImageMap.put("Type", 1);

                    Map attachmentImageMap = new HashMap();
                    attachmentImageMap.put(attachment_ref + "/" + push_id, StringImageMap);

                    mRootRef.updateChildren(attachmentImageMap, (databaseError, databaseReference) -> {
                        if (databaseError != null) {

                        }
                    });
                }
            }

            for (int x=0; x<listStringUriPdf.size();x++){
                if (listStringUriPdf.get(x)!=null) {
                    DatabaseReference user_message_push = mRootRef.child(uid).child(class_id).child(message_id).push();
                    String push_id = user_message_push.getKey();

                    Map StringPdfMap = new HashMap();
                    StringPdfMap.put("URL", listStringUriPdf.get(x));
                    StringPdfMap.put("Name", fileNameList.get(x));
                    StringPdfMap.put("Type", 2);

                    Map attachmentPdfMap = new HashMap();
                    attachmentPdfMap.put(attachment_ref + "/" + push_id, StringPdfMap);

                    mRootRef.updateChildren(attachmentPdfMap, (databaseError, databaseReference) -> {
                        if (databaseError != null) {

                        }
                    });
                }
            }

            if (listUri.size()!= 0){
                for (int x=listStringUri.size();x<listUri.size();x++) {
                    if (listUri.get(x)!=null) {
                        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("message/" + uid + "/"+ fileNameList.get(x));
                        int finalX = x;
                        profileImageRef.putFile(listUri.get(x)).addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            profileImageUrl = uri.toString();

                            DatabaseReference user_image_push = mRootRef.child(uid).child(class_id).child(message_id).push();
                            String push_id = user_image_push.getKey();

                            Map imageMap = new HashMap();
                            imageMap.put("URL", profileImageUrl);
                            imageMap.put("Name", fileNameList.get(finalX));
                            imageMap.put("Type", 1);

                            Map imageUserMap = new HashMap();
                            imageUserMap.put(attachment_ref + "/" + push_id, imageMap);

                            mRootRef.updateChildren(imageUserMap, (databaseError, databaseReference) -> {
                                if (databaseError != null) {

                                }
                            });
                        }))
                                .addOnFailureListener(e -> KToast.errorToast(EditAnnouncement.this, e.getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT));
                    }
                }
            }

            if (listUriPdf.size()!=0){
                for (int y=0;y<listUriPdf.size();y++){
                    if (listUriPdf.get(y)!=null) {
                        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("message/" + uid + "/" + fileNameList.get(y));
                        int finalY = y;
                        profileImageRef.putFile(listUriPdf.get(y)).addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Uri downloadUrl = uri;
                            profileImageUrl = downloadUrl.toString();

                            DatabaseReference user_image_push = mRootRef.child(uid).child(class_id).push();
                            String image_push_id = user_image_push.getKey();

                            Map pdfMap = new HashMap();
                            pdfMap.put("URL", profileImageUrl);
                            pdfMap.put("Name", fileNameList.get(finalY));
                            pdfMap.put("Type", 2);

                            Map pdfUserMap = new HashMap();
                            pdfUserMap.put(attachment_ref + "/" + image_push_id, pdfMap);

                            mRootRef.updateChildren(pdfUserMap, (databaseError, databaseReference) -> {
                                if (databaseError != null) {

                                }
                            });
                        }))
                                .addOnFailureListener(e -> KToast.errorToast(EditAnnouncement.this, e.getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT));
                    }
                }
            }
            finish();
        }
        else {
            mChatMessageView.setError("Message cannot be empty");
            mChatMessageView.requestFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK){

            if (data.getClipData()!=null){
                int totalItemsSelected = data.getClipData().getItemCount();
                for (int i=0;i<totalItemsSelected;i++){
                    uriProfileImage = data.getClipData().getItemAt(i).getUri();
                    String filename = getFilename(uriProfileImage);
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(uriProfileImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    selectedImage = getResizedBitmap(selectedImage, 1500);// 400 is for example, replace with desired size
                    uriProfileImage = getImageUri(getApplicationContext(), selectedImage);
                    fileNameList.add(filename);
                    listpos.add("image");
                    listUri.add(uriProfileImage);
                    listUriPdf.add(null);
                    mAdapter.notifyDataSetChanged();
                }
            }
            else if (data.getData()!=null){
                uriProfileImage = data.getData();
                String filename = getFilename(uriProfileImage);
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(uriProfileImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 1500);// 400 is for example, replace with desired size
                uriProfileImage = getImageUri(getApplicationContext(), selectedImage);
                fileNameList.add(filename);
                listpos.add("image");
                listUri.add(uriProfileImage);
                listUriPdf.add(null);
                mAdapter.notifyDataSetChanged();
            }
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
            String filename = getFilename(uriProfileImage);
            fileNameList.add(filename);
            listpos.add("image");
            listUri.add(uriProfileImage);
            listUriPdf.add(null);
            mAdapter.notifyDataSetChanged();

        }else if (requestCode == PICK_ATTACHMENT && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriProfileImage = data.getData();
            String filename = getFilename(uriProfileImage);
            fileNameList.add(filename);
            listpos.add("pdf");
            listUriPdf.add(uriProfileImage);
            listUri.add(null);
            mAdapter.notifyDataSetChanged();
        }
    }


    private void showImageChooser() {
        final CharSequence[] options = {"Camera", "Choose From Gallery", "Documents", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo From:");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Choose From Gallery")) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),CHOOSE_IMAGE);
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
                        Uri photoURI = FileProvider.getUriForFile(EditAnnouncement.this,
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

        if (id == R.id.attachments) {
            verifyPermissions();
        }

        if(id==R.id.make_announcement){
            String message = mChatMessageView.getText().toString();
            if (!message.equals(text_message)) {
                uploadAnnouncement();
            }else{
                mChatMessageView.setError("No Changes Made");
                mChatMessageView.requestFocus();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.announcement_menu, menu);
        return true;
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
            ActivityCompat.requestPermissions(EditAnnouncement.this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
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

    public String getFilename(Uri uri){
        String result =null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try{
                if (cursor!=null&&cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut!=-1){
                result = result.substring(cut+1);
            }
        }
        return result;
    }
}
