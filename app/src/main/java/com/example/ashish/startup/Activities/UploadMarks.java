package com.example.ashish.startup.Activities;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.example.ashish.startup.Adapters.MarksListAdapter;
import com.example.ashish.startup.Adapters.SubjectsListAdapter;
import com.example.ashish.startup.Fragments.TestFragment;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.Models.Subject;
import com.example.ashish.startup.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UploadMarks extends AppCompatActivity {

    FloatingActionMenu floatingActionMenu;
    com.github.clans.fab.FloatingActionButton createTest;
    private String class_id;

    public static int navItemIndex = 0;

    private static final String TAG_HOME = "home";
    public static String CURRENT_TAG = TAG_HOME;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_marks);

        floatingActionMenu = findViewById(R.id.floatingActionMarksMenu);
        createTest = findViewById(R.id.new_test);
        floatingActionMenu.setClosedOnTouchOutside(true);

        if (getIntent().hasExtra("class_id")&& getIntent().hasExtra("institute")) {
            class_id = getIntent().getStringExtra("class_id");
            final String Institute = getIntent().getStringExtra("institute");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
            mHandler = new Handler();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Upload Marks");
            }

            createTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UploadMarks.this,CreateNewTest.class);
                    intent.putExtra("class_id",class_id);
                    startActivity(intent);
                }
            });
        }

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    public String getClass_id() {
        return class_id;
    }

    private void loadHomeFragment() {
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                TestFragment test = new TestFragment();
                Bundle bundle = new Bundle();
                bundle.putString("class_id", class_id);
                test.setArguments(bundle);

                Fragment fragment = test;
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.fragment_frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
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
