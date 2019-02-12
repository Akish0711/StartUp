package com.example.ashish.startup.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.ashish.startup.R;
import com.example.ashish.startup.activities.DeleteAccount;
import com.example.ashish.startup.authentication.CreateAccount;
import com.example.ashish.startup.authentication.CreateAccountTeacher;
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
        LinearLayout add_students = view.findViewById(R.id.add_students);
        LinearLayout add_teacher = view.findViewById(R.id.add_teacher);
        LinearLayout delete_user = view.findViewById(R.id.delete_user);

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

        return view;
    }
}

