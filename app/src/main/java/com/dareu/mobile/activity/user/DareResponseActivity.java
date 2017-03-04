package com.dareu.mobile.activity.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.dareu.mobile.R;

public class DareResponseActivity extends AppCompatActivity {

    public static final String DARE_RESPONSE_ID = "dareResponseId";

    private String dareResponseId;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_response);
        dareResponseId = getIntent().getStringExtra(DARE_RESPONSE_ID);
        textView = (TextView)findViewById(R.id.dareResponseTextView);
        textView.setText(dareResponseId);
    }
}
