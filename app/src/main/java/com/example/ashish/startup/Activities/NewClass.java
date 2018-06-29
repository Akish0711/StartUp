package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewClass extends AppCompatActivity {

    private EditText name_class;
    private Button new_class;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        name_class = findViewById(R.id.name_class);
        new_class = findViewById(R.id.new_class);
        mAuth = FirebaseAuth.getInstance();


        new_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewClass();
            }
        });
    }

    private void createNewClass() {
        final String className = name_class.getText().toString();
        if (className.isEmpty()){
            name_class.setError("Name required");
            name_class.requestFocus();
            return;
        }else {
            FirebaseUser user = mAuth.getCurrentUser();

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            String email=user.getEmail();
            String email_red = email.substring(0, email.length() - 10);
            final Map<String, Object> subject = new HashMap<>();
            subject.put("Name", className);
            rootRef.collection("Users").document(email_red).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        document.getReference().collection("Subjects").document().set(subject);
                        Toast.makeText(NewClass.this,"New Class Created",Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(NewClass.this,MainActivity.class));
                    }
                }
            });
        }
    }
}
