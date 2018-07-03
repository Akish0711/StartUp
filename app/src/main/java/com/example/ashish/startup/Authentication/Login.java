package com.example.ashish.startup.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
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

import com.example.ashish.startup.R;
import com.example.ashish.startup.Activities.MainActivity;
import com.example.ashish.startup.Activities.nonadmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onurkaganaldemir.ktoastlib.KToast;

public class Login extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginBtn;
    ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    public static final String PREFS_NAME = "MyPrefsFile";
    public String regToken;

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

        mEmailField = findViewById(R.id.username);
        mPasswordField = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.login);
        mProgressBar =  findViewById(R.id.progressBar2);
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

            SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);

            //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
            int hasLoggedIn = settings.getInt("hasLoggedIn", 0);

            if(hasLoggedIn == 1)
            {
                //Go directly to main activity.
                KToast.successToast(Login.this, "Logged in as admin.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                finish();
                startActivity(new Intent(Login.this, MainActivity.class));
            }
            else if (hasLoggedIn == 2){
                KToast.successToast(Login.this, "Logged in as user.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
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

    public static boolean CheckInternet(Context context)
    {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi.isConnected() || mobile.isConnected();
    }

    public void startSign(){
        final String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        regToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("token in login","token" + regToken);

        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){
            KToast.warningToast(Login.this, "Fields are Empty.", Gravity.BOTTOM, KToast.LENGTH_AUTO);

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
                                    editor.putString("regId",regToken);
                                    editor.apply();

                                    if (document.getString("Admin").equals("Yes")) {
                                        KToast.successToast(Login.this, "Logged in as admin.", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                                        //Set "hasLoggedIn" to true
                                        editor.putInt("hasLoggedIn", 1);

                                        // Commit the edits!
                                        editor.apply();

                                        finish();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    } else {
                                        KToast.successToast(Login.this, "Logged in as user.", Gravity.BOTTOM, KToast.LENGTH_AUTO);

                                        //Set "hasLoggedIn" to true
                                        editor.putInt("hasLoggedIn", 2);

                                        // Commit the edits!
                                        editor.apply();

                                        finish();
                                        startActivity(new Intent(Login.this, nonadmin.class));
                                    }
                                }else {
                                    mProgressBar.setVisibility(View.GONE);
                                    notifyUser( "Sign in problem.");

                                }
                            }
                        });
                    }else{
                        onFailure(task.getException());
                    }
                }
            });
        }
    }

    public void onFailure(@NonNull Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            notifyUser("Invalid password");
        }
        else if (!CheckInternet(Login.this)){
            notifyUser("No internet connection.");
        }
        else if (e instanceof FirebaseAuthInvalidUserException) {

            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                notifyUser("No matching account found");
            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                notifyUser("User account has been disabled");
            } else {
                notifyUser(e.getLocalizedMessage());
            }
        }
        else{
            notifyUser("Sign in error.Please close the app and try again.");
        }
    }

    private void notifyUser(String error) {
        mProgressBar.setVisibility(View.GONE);
        KToast.errorToast(Login.this,error,Gravity.BOTTOM,KToast.LENGTH_AUTO);
    }
}
