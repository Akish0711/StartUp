package com.example.ashish.startup.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;

import com.example.ashish.startup.R;
import com.example.ashish.startup.activities.SettingsAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onurkaganaldemir.ktoastlib.KToast;

public class ChangePassword extends AppCompatActivity {

    private EditText change_pass;
    private EditText new_pass;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        change_pass = findViewById(R.id.change_pass);
        new_pass = findViewById(R.id.new_pass);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Change Password");
        }
    }

    public void change(View v){

        String pass = change_pass.getText().toString();
        String pass_new = new_pass.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(TextUtils.isEmpty(pass)){
            KToast.errorToast(ChangePassword.this,"Fill in the Fields", Gravity.BOTTOM,KToast.LENGTH_AUTO);
        }
        else if (!pass.equals(pass_new)){
            KToast.errorToast(ChangePassword.this,"Password should match",Gravity.BOTTOM,KToast.LENGTH_AUTO);
        }
        else if (pass.length()<6){
            KToast.errorToast(ChangePassword.this,"Password should have more than 5 characters",Gravity.BOTTOM,KToast.LENGTH_AUTO);
        }
        else if(user!=null)
        {
            dialog.setMessage("Changing Password, please wait!!!");
            dialog.show();
            user.updatePassword(change_pass.getText().toString())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            KToast.successToast(ChangePassword.this,"Your Password has been Changed",Gravity.BOTTOM,KToast.LENGTH_AUTO);
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(ChangePassword.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            sendBroadcast(intent);
                            finish();
                        }
                        else
                        {
                            dialog.dismiss();
                            KToast.errorToast(ChangePassword.this,"Password Could not be Changed. Logout and Try Again.",Gravity.BOTTOM,KToast.LENGTH_LONG);
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
