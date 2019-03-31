package com.google.vision.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.vision.R;
import com.google.vision.activities.EnquiryAdmin;
import com.google.vision.activities.FeedbackAdmin;
import com.google.vision.activities.PaymentAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivitySlider2 extends Fragment {

    private String uid;

    public MainActivitySlider2() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Context context) {
        return new MainActivitySlider2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.main_activity_2, null);
        LinearLayout feedback = view.findViewById(R.id.feedback);
        LinearLayout enquiries = view.findViewById(R.id.enquiries);
        LinearLayout payments = view.findViewById(R.id.payments);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        feedback.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FeedbackAdmin.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        payments.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PaymentAdmin.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        enquiries.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EnquiryAdmin.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        return view;
    }
}

