package com.example.ashish.startup.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ashish.startup.R;
import com.example.ashish.startup.Activities.MainActivity;
import com.example.ashish.startup.Activities.nonadmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.onurkaganaldemir.ktoastlib.KToast;

public class Login extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginBtn;
    ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_login);

        mEmailField = (EditText)findViewById(R.id.username);
        mPasswordField = (EditText)findViewById(R.id.password);
        mLoginBtn = (Button)findViewById(R.id.login);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(View.GONE);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSign();
            }
        });
    }

    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            mProgressBar.setVisibility(View.VISIBLE);
            String email = mAuth.getCurrentUser().getEmail();
            String email_red = email.substring(0, email.length() - 10);

            SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);

            //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
            int hasLoggedIn = settings.getInt("hasLoggedIn", 0);

            if(hasLoggedIn == 1)
            {
                //Go directly to main activity.
                KToast.successToast(Login.this, "Loggen in as admin.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                finish();
                startActivity(new Intent(Login.this, MainActivity.class));
            }
            else if (hasLoggedIn == 2){
                KToast.successToast(Login.this, "Loggen in as user.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                finish();
                startActivity(new Intent(Login.this, nonadmin.class));
            }

//            rootRef.collection("Users").document(email_red).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.getString("Admin").equals("Yes")) {
//                            mProgressBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(Login.this, "Logged in as an Admin", Toast.LENGTH_LONG).show();
//                            finish();
//                            startActivity(new Intent(Login.this, MainActivity.class));
//                        } else {
//                            Toast.makeText(Login.this, "Logged in as User", Toast.LENGTH_LONG).show();
//                            finish();
//                            startActivity(new Intent(Login.this, nonadmin.class));
//                        }
//                    }
//                }
//            });
        }
    }

    public void startSign(){
        final String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){
            Toast.makeText(Login.this,"Fields are Empty",Toast.LENGTH_LONG).show();
        }else {
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email+"@gmail.com", password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        rootRef.collection("Users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    //User has successfully logged in, save this information
                                    // We need an Editor object to make preference changes.
                                    SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = settings.edit();

                                    if (document.getString("Admin").equals("Yes")) {
                                        Toast.makeText(Login.this, "Logged In! You are Admin", Toast.LENGTH_LONG).show();

                                        //Set "hasLoggedIn" to true
                                        editor.putInt("hasLoggedIn", 1);

                                        // Commit the edits!
                                        editor.commit();

                                        finish();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(Login.this, "Logged In! You are User", Toast.LENGTH_LONG).show();

                                        //Set "hasLoggedIn" to true
                                        editor.putInt("hasLoggedIn", 2);

                                        // Commit the edits!
                                        editor.commit();

                                        finish();
                                        startActivity(new Intent(Login.this, nonadmin.class));
                                    }
                                }else {
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this,"Sign In Problem",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this,"Invalid credential",Toast.LENGTH_LONG).show();
                        Log.e("login error", "onComplete: Failed=" + task.getException().getMessage());
                    }
                }
            });
        }
    }
}
