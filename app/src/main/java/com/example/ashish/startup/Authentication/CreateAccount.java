package com.example.ashish.startup.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//import org.apache.http.HttpResponse;

public class CreateAccount extends AppCompatActivity {

    EditText username, name,phoneNumber;
    Button createAccount;
    ProgressBar progressBar;

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

        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        createAccount = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progressBar3);

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

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
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

        final String user = username.getText().toString();
        final String display_name = name.getText().toString();
        final String phoneNo = phoneNumber.getText().toString();
        if (user.isEmpty()) {
            username.setError("Username is required");
            username.requestFocus();
            return;
        }

        if (display_name.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }
        if(phoneNo.isEmpty()){
            phoneNumber.setError("Phone Number is required");
            phoneNumber.requestFocus();
            return;
        }

        String email = mAuth1.getCurrentUser().getEmail();
        String email_red = email.substring(0, email.length() - 10);
        rootRef.collection("Users").document(email_red).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    final String Institute = document.getString("Institute");
                    progressBar.setVisibility(View.VISIBLE);

                    genPswd = genRandomPswd();

                    mAuth2.createUserWithEmailAndPassword(user + "@gmail.com", genPswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                KToast.successToast(CreateAccount.this, "User Registered Successfully.", Gravity.BOTTOM, KToast.LENGTH_SHORT);

                                Map<String, Object> data = new HashMap<>();
                                data.put("Name", display_name);
                                data.put("Username", user + "@gmail.com");
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
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    KToast.errorToast(CreateAccount.this, "This User is already registered.", Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                } else {
                                    KToast.errorToast(CreateAccount.this, task.getException().getMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                                }
                            }
                        }
                    });
                }
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
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


