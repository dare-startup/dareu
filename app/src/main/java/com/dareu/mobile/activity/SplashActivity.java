package com.dareu.mobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;

public class SplashActivity extends AppCompatActivity implements ActivityListener{

    private ProgressBar progressBar;
    private TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getComponents();
        initialize();
    }

    @Override
    public void getComponents() {
        this.progressBar = (ProgressBar)findViewById(R.id.splashActivityProgressBar);
        this.messageView = (TextView)findViewById(R.id.splashMessageView);
    }

    @Override
    public void initialize() {
        //set message view and load data from here...
        this.messageView.setText("Loading some important stuff here...");

        //check if user is already logged in
        Intent intent = null;
        if(!SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN).isEmpty()){
            //go to main activity here
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else {
            //go to signin activity
            intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
