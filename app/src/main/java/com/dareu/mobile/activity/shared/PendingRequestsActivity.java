package com.dareu.mobile.activity.shared;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dareu.mobile.R;
import com.dareu.mobile.adapter.PendingRequestsPagerAdapter;

public class PendingRequestsActivity extends AppCompatActivity {

    public static final String FRIENDSHIP_ID = "friendshipId";

    //views
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    //variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);
        getComponents();

    }

    private void getComponents() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("Pending requests");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        PendingRequestsPagerAdapter adapter = new PendingRequestsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //creates a tab layout
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
