package com.dareu.mobile.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dareu.mobile.R;
import com.dareu.mobile.adapter.ScreenSlidePagerAdapter;

public class WelcomeActivity extends AppCompatActivity implements ActivityListener{
    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    @Override
    public void getComponents() {
        this.viewPager = (ViewPager)findViewById(R.id.welcomeViewPager);
        this.pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    }

    @Override
    public void initialize() {
        viewPager.setAdapter(pagerAdapter);
    }
}
