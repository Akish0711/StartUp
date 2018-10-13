package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.Map;

public class NewClass extends AppCompatActivity {

    private EditText name_class;
    private FirebaseAuth mAuth;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        name_class = findViewById(R.id.name_class);
        Button new_class = findViewById(R.id.new_class);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Create New Class");
        }

        spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(NewClass.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.classes));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        new_class.setOnClickListener(view -> createNewClass());
    }

    private void createNewClass() {
        final String className = name_class.getText().toString();
        String section = spinner.getSelectedItem().toString();

        if (className.isEmpty()){
            name_class.setError("Name required");
            name_class.requestFocus();
            return;
        }else if (spinner == null || spinner.getSelectedItem() ==null || section.equals("Class")) {
            KToast.warningToast(this,"Class Required",Gravity.BOTTOM,KToast.LENGTH_LONG);
            return;
        }else {
            FirebaseUser user = mAuth.getCurrentUser();
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            String email=user.getEmail();
            String email_red = email.substring(0, email.length() - 10);
            final Map<String, Object> subject = new HashMap<>();
            subject.put("Name", className);
            subject.put("Section",section);
            subject.put("Total_Students","0");
            rootRef.collection("Users").document(email_red).collection("Subjects").document().set(subject).addOnCompleteListener(task -> {
                KToast.successToast(NewClass.this,"New Class Created", Gravity.BOTTOM,KToast.LENGTH_SHORT);
                finish();
                startActivity(new Intent(NewClass.this,MainActivity.class));
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
