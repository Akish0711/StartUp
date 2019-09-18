package com.google.vision.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.vision.R;
import com.google.vision.others.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;
    private static final int PICK_IMAGE_CAMERA =188 ;
    private static final int REQUEST_CODE = 1;
    private TextView txtName, txtEmail;
    private ImageView changeImage;
    private EditText editText, editEmail, editPhone;
    private Uri uriProfileImage;
    ProgressBar progressbar;
    private FirebaseFirestore rootRef;
    private FirebaseUser user;
    String uid;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            editEmail = findViewById(R.id.editEmail);
            editPhone = findViewById(R.id.contact_number);
            editText = findViewById(R.id.editTextDisplayName);
            progressbar = findViewById(R.id.progressbar);
            changeImage = findViewById(R.id.changeImage);
            txtName = findViewById(R.id.edit_name);
            txtEmail = findViewById(R.id.edit_email);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            progressbar.setVisibility(View.INVISIBLE);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Edit Profile");
            }

            rootRef = FirebaseFirestore.getInstance();
            user = mAuth.getCurrentUser();

            changeImage.setOnClickListener(view -> verifyPermissions());

            loadUserInformation();

            findViewById(R.id.buttonSave).setOnClickListener(view -> saveUserInformation());
        }
    }

    private void loadUserInformation() {
        rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                txtName.setText(document.getString("Name"));
                txtEmail.setText(document.getString("Username").toLowerCase());
                editPhone.setText(document.getString("Phone"));
                editEmail.setText(document.getString("Email"));
                editText.setText(document.getString("Name"));

                if (user.getPhotoUrl()!=null) {
                    Glide.with(getApplicationContext())
                            .load(user.getPhotoUrl())
                            .error(R.drawable.teacher_icon_male)
                            .crossFade()
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(EditProfile.this))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(changeImage);
                }else if (document.getString("Gender").equals("Male")){
                    Glide.with(getApplicationContext())
                            .load(R.drawable.teacher_icon_male)
                            .crossFade()
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(changeImage);
                }else {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.teacher_icon_female)
                            .crossFade()
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(changeImage);
                }
            }
        });
    }

    private void saveUserInformation() {
        String displayName = editText.getText().toString();
        final String contact = editPhone.getText().toString();
        final String real_email = editEmail.getText().toString();
        if (displayName.isEmpty()){
            editText.setError("Name required");
            editText.requestFocus();
        }else if(user != null && uriProfileImage==null){
            rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = new HashMap<>();
                    data.put("Name", displayName);
                    data.put("Phone",contact);
                    data.put("Email",real_email);
                    document.getReference().update(data).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            KToast.successToast(EditProfile.this, "Profile Updated", Gravity.BOTTOM,KToast.LENGTH_SHORT);
                            finish();
                            startActivity(new Intent(EditProfile.this,MainActivity.class));
                        }
                    });
                }
            });
        }else{
            rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = new HashMap<>();
                    data.put("Name", displayName);
                    data.put("Phone",contact);
                    data.put("Email",real_email);
                    document.getReference().update(data);
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    KToast.successToast(EditProfile.this,"Profile Updated",Gravity.BOTTOM,KToast.LENGTH_SHORT);
                    finish();
                    startActivity(new Intent(EditProfile.this,MainActivity.class));
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null) {
                uriProfileImage = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(uriProfileImage);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 400);// 400 is for example, replace with desired size
                uriProfileImage = getImageUri(getApplicationContext(), selectedImage);
                Glide.with(this)
                        .load(uriProfileImage)
                        .crossFade()
                        .bitmapTransform(new CircleTransform(EditProfile.this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(changeImage);
                uploadImageToFirebaseStorage();
            }else if (requestCode == PICK_IMAGE_CAMERA && resultCode == Activity.RESULT_OK ) {
                int targetW = changeImage.getWidth();
                int targetH = changeImage.getHeight();

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
                Glide.with(this)
                        .load(uriProfileImage)
                        .crossFade()
                        .bitmapTransform(new CircleTransform(EditProfile.this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(changeImage);
                uploadImageToFirebaseStorage();
            }else {
                KToast.warningToast(this, "You haven't picked Image",Gravity.BOTTOM,KToast.LENGTH_AUTO);
            }
        } catch (Exception e) {
            KToast.errorToast(this, "Something went wrong.Please try again.", Gravity.BOTTOM,KToast.LENGTH_LONG);
        }
    }

    private void uploadImageToFirebaseStorage() {
        progressbar.setVisibility(View.VISIBLE);
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/"+uid+"/profile.jpg");
        if (uriProfileImage!=null){
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(taskSnapshot -> {
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Uri downloadUrl = uri;
                    profileImageUrl = downloadUrl.toString();
                }).addOnCompleteListener(task -> {
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(profileImageUrl))
                            .build();
                    user.updateProfile(profile);
                    progressbar.setVisibility(View.GONE);
                });
            }).addOnFailureListener(e -> {
                        KToast.errorToast(EditProfile.this,e.getMessage(),Gravity.BOTTOM,KToast.LENGTH_SHORT);
                    });
        }
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void showImageChooser() {
        final CharSequence[] options = {"Camera", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        Uri photoURI = FileProvider.getUriForFile(EditProfile.this,
                                "com.example.ashish.startup.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
                    }
                }
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
        if (item.getItemId()==android.R.id.home)
            finish();
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
            ActivityCompat.requestPermissions(EditProfile.this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}

