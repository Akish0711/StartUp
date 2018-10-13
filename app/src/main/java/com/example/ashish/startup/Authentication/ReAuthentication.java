package com.example.ashish.startup.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.ashish.startup.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onurkaganaldemir.ktoastlib.KToast;

public class ReAuthentication extends AppCompatActivity {

    private EditText current_pass;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_authentication);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        current_pass = findViewById(R.id.current_pass);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Change Password");
        }
    }

    public void next(View v) {
        String pass = current_pass.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (TextUtils.isEmpty(pass)) {
            KToast.warningToast(ReAuthentication.this, "Fill in the Fields.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
        } else if (user != null) {
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email,pass);
            dialog.setMessage("Authenticating");
            dialog.show();
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            KToast.successToast(ReAuthentication.this, "Authenticated.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                            Intent i = new Intent(ReAuthentication.this, ChangePassword.class);
                            startActivity(i);
                        }
                        else
                        {
                            dialog.dismiss();
                            KToast.errorToast(ReAuthentication.this, "Invalid Credentials.", Gravity.BOTTOM, KToast.LENGTH_AUTO);
                        }
                    });

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
