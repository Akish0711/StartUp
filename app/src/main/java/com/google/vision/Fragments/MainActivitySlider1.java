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
import com.google.vision.activities.DeleteAccount;
import com.google.vision.activities.Performance;
import com.google.vision.authentication.CreateAccount;
import com.google.vision.authentication.CreateAccountTeacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivitySlider1 extends Fragment {

    private String uid;

    public MainActivitySlider1() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Context context) {
        return new MainActivitySlider1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.main_activity_1, null);
        LinearLayout add_students = view.findViewById(R.id.add_students);
        LinearLayout add_teacher = view.findViewById(R.id.add_teacher);
        LinearLayout delete_user = view.findViewById(R.id.delete_user);
        LinearLayout performance = view.findViewById(R.id.performance);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        add_students.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),CreateAccount.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        delete_user.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DeleteAccount.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        add_teacher.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),CreateAccountTeacher.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        performance.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Performance.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        return view;
    }
}