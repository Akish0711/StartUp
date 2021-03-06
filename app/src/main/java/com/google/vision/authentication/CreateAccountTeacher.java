package com.google.vision.authentication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.vision.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateAccountTeacher extends AppCompatActivity {

    EditText name,phoneNumber, email;
    Button createAccount;
    ProgressBar progressBar;
    String uid;
    private static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private String genPswd;
    private Random random;
    private FirebaseAuth mAuth2;
    View parentLayout;
    private static final int REQUEST_CODE = 1;
    private RadioGroup genderRadioGroup;
    private DatePickerDialog datePickerDialog;
    private EditText birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_teacher);

        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            name = findViewById(R.id.name);
            email = findViewById(R.id.email);
            parentLayout = findViewById(R.id.create_teacher_parent);
            phoneNumber = findViewById(R.id.phoneNumber);
            createAccount = findViewById(R.id.createAccount);
            progressBar = findViewById(R.id.progressBar3);
            progressBar.setVisibility(View.INVISIBLE);
            genderRadioGroup = findViewById(R.id.gender_radiogroup);
            ImageView calender = findViewById(R.id.calenderdob);
            birthday = findViewById(R.id.birthday);

            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://startup-ec618.firebaseio.com")
                    .setApiKey("AIzaSyCRS7dY1FKZdfFmfLLTeRtQm_hyTbsERn8")
                    .setApplicationId("startup-ec618").build();

            try {
                FirebaseApp app = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "app");
                mAuth2 = FirebaseAuth.getInstance(app);
            } catch (IllegalStateException e) {
                mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("app"));
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Register Teachers");
            }

            prepareDatePickerDialog();

            calender.setOnClickListener(v -> {
                datePickerDialog.show();
            });

            createAccount.setOnClickListener(v -> verifyPermissions());
            random = new Random();
        }
    }

    private void prepareDatePickerDialog() {
        //Get current date
        Calendar calendar=Calendar.getInstance();

        //Create datePickerDialog with initial date which is current and decide what happens when a date is selected.
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //When a date is selected, it comes here.
                //Change birthdayEdittext's text and dismiss dialog.
                calendar.set(year,monthOfYear,dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String formatedDate = sdf.format(calendar.getTime());
                birthday.setText(formatedDate);
                datePickerDialog.dismiss();
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    public String genRandomPswd(){
        StringBuilder sb = new StringBuilder(8);
        for(int i = 0; i < 8; i++){
            sb.append(DATA.charAt(random.nextInt(DATA.length())));
        }
        return sb.toString();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public Date getDateFromString(String datetoSaved){

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return format.parse(datetoSaved);
        } catch (ParseException e){
            return null ;
        }

    }

    public static boolean isDateValid(String date){

        String DATE_FORMAT = "dd/MM/yyyy";
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void registerUser() {
        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final String user_name = name.getText().toString();
        final String user_number = phoneNumber.getText().toString();
        final String user_email = email.getText().toString();
        RadioButton selectedRadioButton = findViewById(genderRadioGroup.getCheckedRadioButtonId());
        String gender = selectedRadioButton==null ? "":selectedRadioButton.getText().toString();

        final String birthdate = birthday.getText().toString();

        if (user_name.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
        }else if (gender.equals("")) {
            KToast.warningToast(this,"Gender Required",Gravity.BOTTOM,KToast.LENGTH_LONG);
        }else if(user_number.isEmpty()){
            phoneNumber.setError("Contact Number required");
            phoneNumber.requestFocus();
        }else if(user_number.length()!=10){
            phoneNumber.setError("Enter Correct Contact Number");
            phoneNumber.requestFocus();
        }else if(user_email.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
        }else if (!isEmailValid(user_email)){
            email.setError("Email not properly formatted");
            email.requestFocus();
        }else if (!birthdate.equals("") && !isDateValid(birthdate)){
            birthday.setError("Birthday not properly formatted");
            birthday.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            rootRef.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    String code = document.getString("Code");
                    final String Institute = document.getString("Institute");
                    long overall_teachers = document.getLong("Overall_Teachers");
                    overall_teachers++;

                    Map<String, Object> update_data = new HashMap<>();
                    update_data.put("Overall_Teachers", overall_teachers);

                    String new_username = (code+overall_teachers);
                    genPswd = genRandomPswd();

                    mAuth2.createUserWithEmailAndPassword(user_email, genPswd).addOnCompleteListener(task1 -> {
                        progressBar.setVisibility(View.GONE);
                        if (task1.isSuccessful()) {
                            FirebaseUser user2 = mAuth2.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(new_username).build();
                            user2.updateProfile(profileUpdates);

                            Map<String, Object> data = new HashMap<>();
                            data.put("Name", user_name);
                            data.put("Username", new_username);
                            data.put("Code", code);
                            data.put("Teacher","Yes");
                            data.put("Admin", "No");
                            data.put("Email", user_email);
                            data.put("Phone",user_number);
                            data.put("Code_Admin", code+"_No");
                            data.put("Institute", Institute);
                            data.put("Admin_Uid", uid);
                            data.put("BirthDate", getDateFromString(birthdate));
                            data.put("Gender", gender);
                            data.put("FTP", genPswd);

                            rootRef.collection("Users").document(user2.getUid()).set(data);
                            rootRef.collection("Users").document(uid).update(update_data);

                            /*SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(user_number, null, "Welcome OnBoard "+user_name+"!!\n\nHere are your Login Details \n\nUsername : " + new_username + "\nPassword : " + genPswd, null, null);
                            */mAuth2.signOut();
                            finish();
                            KToast.successToast(CreateAccountTeacher.this, "Teacher Registered", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                        } else {
                            if (task1.getException() instanceof FirebaseAuthUserCollisionException) {
                                KToast.errorToast(CreateAccountTeacher.this, "This User is already registered.", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                            } else {
                                KToast.errorToast(CreateAccountTeacher.this, task1.getException().getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            });
        }
    }

    /*private void verifyPermissions() {
        String[] permissions = {Manifest.permission.SEND_SMS};
        if(ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            if (isInternetAvailable()){
                registerUser();
            }else {
                Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }*/

    private void verifyPermissions() {
        if (isInternetAvailable()){
            registerUser();
        }else {
            Snackbar.make(parentLayout, "This action requires Internet Connection", Snackbar.LENGTH_LONG).show();
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }*/

    public boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = process.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // finish the activity
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}


