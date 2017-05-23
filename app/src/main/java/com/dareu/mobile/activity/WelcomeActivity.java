package com.dareu.mobile.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.adapter.ScreenSlidePagerAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity{

    private PagerAdapter pagerAdapter;

    @BindView(R.id.welcomeSignupButton)
    Button signupButton;

    @BindView(R.id.welcomeSigninTextView)
    TextView signinTextView;

    @BindView(R.id.welcomeMessage)
    LinearLayout messageLayout;

    @BindView(R.id.welcomeButtonsLayout)
    LinearLayout buttonsLayout;

    @BindView(R.id.welcomeLogo)
    ImageView logo;

    private int exitCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        initialize();
    }

    private void showWelcomeAnimation() {
        ViewCompat.animate(logo)
            .translationY(-600f)
            .setStartDelay(500)
            .setDuration(1100)
            .setInterpolator(new DecelerateInterpolator(1.2f))
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                        ViewCompat.postOnAnimationDelayed(messageLayout, new Runnable() {
                            @Override
                            public void run() {
                                messageLayout.setAnimation(animFadeIn);
                                messageLayout.setVisibility(View.VISIBLE);
                                ViewCompat.postOnAnimationDelayed(buttonsLayout, new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonsLayout.setAnimation(animFadeIn);
                                        buttonsLayout.setVisibility(View.VISIBLE);
                                    }
                                }, 100);
                            }
                        }, 450);


                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                })
        .start();
    }

    public void initialize() {
        //check if user is already logged in
        Intent intent = null;
        if(!SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN).isEmpty()){
            //go to main activity here
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {
            showWelcomeAnimation();
        }

        this.pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        //set listeners
        this.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_down, R.anim.fade_out);
            }
        });
        this.signinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SigninActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_down, R.anim.fade_out);
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
