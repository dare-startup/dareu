package com.dareu.mobile.activity.shared;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Spinner;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.ActivityListener;
import com.dareu.mobile.task.request.NewDareRequest;

public class NewDareActivity extends AppCompatActivity implements ActivityListener{

    private NewDareRequest dareRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dare);
        getComponents();
        initialize();
    }

    @Override
    public void getComponents() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void initialize() {
        dareRequest = new NewDareRequest();


    }
}
