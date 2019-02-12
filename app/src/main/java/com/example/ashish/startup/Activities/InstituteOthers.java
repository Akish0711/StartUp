package com.example.ashish.startup.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ashish.startup.Adapters.AdminMessageAdapter;
import com.example.ashish.startup.Adapters.UserMessageAdapter;
import com.example.ashish.startup.Fragments.MainActivitySlider1;
import com.example.ashish.startup.Fragments.MainActivitySlider2;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InstituteOthers extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private int[] layouts;
    private RecyclerView mMessagesList;
    private List<Message> messageList;
    private UserMessageAdapter mAdapter;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_others);
        if (getIntent().hasExtra("uid") && getIntent().hasExtra("admin_uid")){
            String uid = getIntent().getStringExtra("uid");
            String admin_uid = getIntent().getStringExtra("admin_uid");

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Institute");
            }

            messageList = new ArrayList<>();
            mRootRef = FirebaseDatabase.getInstance().getReference();
            String class_id = "Institute";
            messageList = new ArrayList<>();
            mAdapter = new UserMessageAdapter(getApplicationContext(),messageList,class_id,admin_uid);
            mMessagesList = findViewById(R.id.main_list);
            LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
            mLinearLayout.setReverseLayout(true);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);

            loadmessage(class_id,admin_uid);

            ViewPager mPager = findViewById(R.id.view_pager);
            PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
            mPager.addOnPageChangeListener(viewPagerPageChangeListener);
            dotsLayout = findViewById(R.id.layoutDots);

            layouts = new int[]{
                    R.layout.main_activity_1,
                    R.layout.main_activity_2,
            };

            addBottomDots(0);
        }
    }

    private void loadmessage(String class_id, String email_red) {
        DatabaseReference messageRef = mRootRef.child(email_red).child(class_id);
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String messageKey = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class).withID(messageKey);
                messageList.add(message);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new Fragment();
            switch (position) {

                case 0:
                    f = MainActivitySlider1.newInstance(getApplicationContext());
                    break;
                case 1:
                    f = MainActivitySlider2.newInstance(getApplicationContext());
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void addBottomDots(int currentPage) {
        ImageView[] dots = new ImageView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            if (i == currentPage) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));

            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_dots));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            dotsLayout.addView(dots[i], params);
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
