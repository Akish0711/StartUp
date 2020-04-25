package com.google.vision.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.vision.R;
import com.onurkaganaldemir.ktoastlib.KToast;

public class EnquiryStudents extends AppCompatActivity {

    private RadioGroup typeRadioGroup;
    private EditText enquirySubject, enquiryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_students);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Enquiry/Complaint");
        }

        typeRadioGroup = findViewById(R.id.type_radiogroup);
        enquirySubject = findViewById(R.id.enquiry_subject);
        enquiryDescription = findViewById(R.id.enquiry_description);
        Button enquirySubmit = findViewById(R.id.enquiry_submit);

        enquirySubmit.setOnClickListener(view -> {

            RadioButton selectedRadioButton = findViewById(typeRadioGroup.getCheckedRadioButtonId());
            String type = selectedRadioButton==null ? "":selectedRadioButton.getText().toString();

            final String enquiry_subject = enquirySubject.getText().toString();
            final String enquiry_description = enquiryDescription.getText().toString();

            if (type.equals("")){
                KToast.warningToast(EnquiryStudents.this,"Type Required", Gravity.BOTTOM,KToast.LENGTH_LONG);
            }else if (enquiry_subject.isEmpty()){
                enquirySubject.setError("Subject Required");
                enquirySubject.requestFocus();
            }else if (enquiry_description.isEmpty()){
                enquiryDescription.setError("Description Required");
                enquiryDescription.requestFocus();
            }else if (type.equals("Enquiry")){

            }else{

            }
        });

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
