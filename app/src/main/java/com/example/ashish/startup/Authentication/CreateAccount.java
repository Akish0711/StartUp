package com.example.ashish.startup.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ashish.startup.Activities.TakeAttendance;
import com.example.ashish.startup.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateAccount extends AppCompatActivity {

    EditText name,phoneNumber;
    Button createAccount;
    ProgressBar progressBar;
    Spinner spinner;

    private static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private String genPswd;
    private Random random;

    private FirebaseAuth mAuth1;
    private FirebaseAuth mAuth2;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        createAccount = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progressBar3);
        spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CreateAccount.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.classes));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        mAuth1 = FirebaseAuth.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://startup-ec618.firebaseio.com")
                .setApiKey("AIzaSyCRS7dY1FKZdfFmfLLTeRtQm_hyTbsERn8")
                .setApplicationId("startup-ec618").build();

        try { FirebaseApp app = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "app");
            mAuth2 = FirebaseAuth.getInstance(app);
        } catch (IllegalStateException e){
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("app"));
        }

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create New Account");
        }

        createAccount.setOnClickListener(view -> registerUser());
        random = new Random();
    }

    public String genRandomPswd(){
      StringBuilder sb = new StringBuilder(8);
      for(int i = 0; i < 8; i++){
          sb.append(DATA.charAt(random.nextInt(DATA.length())));
      }
      return sb.toString();
    }

    public void registerUser() {
        final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

        String classes = spinner.getSelectedItem().toString();
        final String display_name = name.getText().toString();
        final String phoneNo = phoneNumber.getText().toString();
        final String[] batch = new String[1];

        if (display_name.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }else if (spinner == null || spinner.getSelectedItem() ==null || classes.equals("Class")) {
            KToast.warningToast(this,"Class Required",Gravity.BOTTOM,KToast.LENGTH_LONG);
            return;
        }else if(phoneNo.isEmpty()){
            phoneNumber.setError("Contact Number required");
            phoneNumber.requestFocus();
            return;
        }else if(phoneNo.length()!=10){
            phoneNumber.setError("Enter Correct Contact Number");
            phoneNumber.requestFocus();
            return;
        }else{
            rootRef.collection("Important").document("Batch").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    batch[0] = document.getString(classes);
                }
            });
        }

        String email = mAuth1.getCurrentUser().getEmail();
        String email_red = email.substring(0, email.length() - 10);
        rootRef.collection("Users").document(email_red).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.VISIBLE);
                DocumentSnapshot document = task.getResult();

                final String Institute = document.getString("Institute");
                String code = document.getString("Code");
                long total_students = document.getLong("Total Students");
                total_students++;

                Map<String, Object> update_data = new HashMap<>();
                update_data.put("Total Students", total_students);
                rootRef.collection("Users").document(email_red).update(update_data);

                String user = batch[0]+code+total_students;
                genPswd = genRandomPswd();

                mAuth2.createUserWithEmailAndPassword(user + "@gmail.com", genPswd).addOnCompleteListener(task1 -> {
                    progressBar.setVisibility(View.GONE);
                    if (task1.isSuccessful()) {
                        KToast.successToast(CreateAccount.this, "User Registered Successfully.", Gravity.BOTTOM, KToast.LENGTH_SHORT);

                        Map<String, Object> data = new HashMap<>();
                        data.put("Name", display_name);
                        data.put("Username", user);
                        data.put("Institute_Admin", Institute + "_No");
                        data.put("Admin","No");
                        data.put("Phone",phoneNo);
                        rootRef.collection("Users").document(user).set(data);
                        Log.e("Password : ",genPswd + "");

//                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + phoneNo));
//                                intent.putExtra("sms_body","Login Details \n\nUsername : "+user+"\nPassword : " + genPswd);
//                                startActivity(intent);

//                               try {
//                                   response = Unirest.post("https://control.msg91.com/api/postsms.php")
//                                           .header("content-type", "application/xml")
//                                           .body("<MESSAGE> <AUTHKEY>232129AdMpIvsHwvv15b759ec4</AUTHKEY>" +
//                                                   " <SENDER>VISION</SENDER> " +
//                                                   "<ROUTE>4</ROUTE> " +
//                                                   "<CAMPAIGN>XML API</CAMPAIGN> " +
//                                                   "<COUNTRY>91</COUNTRY> " +
//                                                   "<SMS TEXT=\"Welcome On-Board\nHere are your login details.\nUsername : " +user + "\" \nPassword : " + genPswd+ "> " +
//                                                   "<ADDRESS TO=\"91"+phoneNo+"\">" +
//                                                   "</ADDRESS>" +
//                                                   "</SMS>" +
//                                                   "</MESSAGE>")
//                                           .asString();
//                                   Log.e("response status",response.getStatusText());
//                               }
//                               catch (Exception e){
//                                   Log.e("Exception in sms",e.getLocalizedMessage());
//                                   Log.e("response status",response.getStatusText());
//                               }

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo, null, "Welcome OnBoard !!\n\nHere are your Login Details \n\nUsername : " + user + "\nPassword : " + genPswd, null, null);

                        mAuth2.signOut();
                        finish();
                        startActivity(new Intent(CreateAccount.this, CreateAccount.class));
                    } else {
                        if (task1.getException() instanceof FirebaseAuthUserCollisionException) {
                            KToast.errorToast(CreateAccount.this, "This User is already registered.", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        } else {
                            KToast.errorToast(CreateAccount.this, task1.getException().getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


