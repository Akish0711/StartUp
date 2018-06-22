package com.example.ashish.startup.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText change_pass;
    private EditText new_pass;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        change_pass = (EditText)findViewById(R.id.change_pass);
        new_pass = (EditText)findViewById(R.id.new_pass);

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
            Toast.makeText(getApplicationContext(),"Fill in the Fields",Toast.LENGTH_LONG).show();
        }
        else if (!pass.equals(pass_new)){
            Toast.makeText(getApplicationContext(),"Password should match",Toast.LENGTH_LONG).show();
        }
        else if (pass.length()<6){
            Toast.makeText(getApplicationContext(),"Password should have more than 5 characters",Toast.LENGTH_LONG).show();
        }
        else if(user!=null)
        {
            dialog.setMessage("Changing Password, please wait!!!");
            dialog.show();
            user.updatePassword(change_pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Your Password has been Changed",Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent("finish");
                                sendBroadcast(intent);
                                finish();
                                Intent i = new Intent(ChangePassword.this,Login.class);
                                startActivity(i);
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Password Could not be Changed. Logout and Try Again.",Toast.LENGTH_LONG).show();
                            }

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
