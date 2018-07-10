package com.example.ashish.startup.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.ashish.startup.Fragments.UserShowMarksFragment;
import com.example.ashish.startup.R;
import com.github.clans.fab.FloatingActionMenu;

public class UserMarks extends AppCompatActivity {

    public static int navItemIndex = 0;

    private static final String TAG_HOME = "home";
    public static String CURRENT_TAG = TAG_HOME;
    private Handler mHandler;
    String subject_id,subject_name,Teacher_Name;
    FloatingActionMenu floatingActionMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_marks);

        floatingActionMenu = findViewById(R.id.floatingActionMarksMenu);
        floatingActionMenu.setVisibility(View.INVISIBLE);

        if(getIntent().hasExtra("subject_id")&& getIntent().hasExtra("subject_name") && getIntent().hasExtra("Teacher_Name")){
            subject_id = getIntent().getStringExtra("subject_id");
            subject_name = getIntent().getStringExtra("subject_name");
            Teacher_Name = getIntent().getStringExtra("Teacher_Name");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
            mHandler = new Handler();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Marks Card");
            }
        }

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void loadHomeFragment() {
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                UserShowMarksFragment marksCard = new UserShowMarksFragment();
                Bundle bundle = new Bundle();
                bundle.putString("subject_id", subject_id);
                bundle.putString("subject_name",subject_name);
                bundle.putString("Teacher_Name",Teacher_Name);
                marksCard.setArguments(bundle);

                Fragment fragment = marksCard;
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
