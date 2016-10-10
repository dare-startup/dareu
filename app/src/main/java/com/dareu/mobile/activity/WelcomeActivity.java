package com.dareu.mobile.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.adapter.ScreenSlidePagerAdapter;

public class WelcomeActivity extends AppCompatActivity implements ActivityListener{
    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    private Button signupButton;
    private TextView signinTextView;

    private int exitCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getComponents();
        initialize();
    }

    @Override
    public void getComponents() {
        this.viewPager = (ViewPager)findViewById(R.id.welcomeViewPager);
        this.pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        this.signupButton = (Button)findViewById(R.id.welcomeSignupButton);
        this.signinTextView = (TextView)findViewById(R.id.welcomeSigninTextView);
    }

    @Override
    public void initialize() {
        viewPager.setAdapter(pagerAdapter);

        //set listeners
        this.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        this.signinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed(){
        if(exitCount == 0){
            Toast.makeText(this, "Press back one more time to exit", Toast.LENGTH_LONG)
                    .show();
            return;
        }else if(exitCount == 1){
            finish();
        }
    }
}
