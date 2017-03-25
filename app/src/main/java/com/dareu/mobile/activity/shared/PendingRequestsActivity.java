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

import butterknife.BindView;
import butterknife.ButterKnife;

public class PendingRequestsActivity extends AppCompatActivity {

    public static final String FRIENDSHIP_ID = "friendshipId";

    //views

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;


    //variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);
        ButterKnife.bind(this);
        initialize();

    }

    private void initialize() {
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
        PendingRequestsPagerAdapter adapter = new PendingRequestsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //creates a tab layout
        tabLayout.setupWithViewPager(viewPager);
    }
}
