package com.dareu.mobile.activity.shared;

import android.graphics.Bitmap;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.ConfirmConnectionTask;
import com.dareu.mobile.net.account.ConnectionDetailsTask;
import com.dareu.mobile.net.account.LoadProfileImageTask;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.ConnectionDetails;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ConnectionStatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private TextView name;
    private ImageButton decline, accept;
    private CircularImageView image;
    private ScrollView scrollView;


    public static final String FRIENDSHIP_ID = "friendshipId";
    private String friendshipId;
    private ConnectionDetails connectionDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_status);
        friendshipId = getIntent().getStringExtra(FRIENDSHIP_ID);
        getComponents();
    }

    private void getComponents() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        scrollView = (ScrollView)findViewById(R.id.connectionStatusLayout);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        name = (TextView)findViewById(R.id.connectionStatusName);
        decline = (ImageButton)findViewById(R.id.connectionStatusDecline);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //decline invitation
                ConfirmConnectionTask task = new ConfirmConnectionTask(ConnectionStatusActivity.this, connectionDetails.getUserId(), false, new AsyncTaskListener<EntityRegistrationResponse>() {
                    @Override
                    public void onTaskResponse(EntityRegistrationResponse response) {
                        Toast.makeText(ConnectionStatusActivity.this, "The connection request has been declined", Toast.LENGTH_LONG)
                                .show();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Snackbar.make(coordinatorLayout, "Could not decline connection request", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                task.execute();
            }
        });
        accept = (ImageButton)findViewById(R.id.connectionStatusAccept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accept invitation
                ConfirmConnectionTask task = new ConfirmConnectionTask(ConnectionStatusActivity.this, connectionDetails.getUserId(), true, new AsyncTaskListener<EntityRegistrationResponse>() {
                    @Override
                    public void onTaskResponse(EntityRegistrationResponse response) {
                        Toast.makeText(ConnectionStatusActivity.this, "The connection request has been accepted", Toast.LENGTH_LONG)
                                .show();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Snackbar.make(coordinatorLayout, "Could not decline connection request", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                task.execute();
            }
        });
        image = (CircularImageView)findViewById(R.id.connectionStatusImage);
        ConnectionDetailsTask task = new ConnectionDetailsTask(ConnectionStatusActivity.this, friendshipId, new AsyncTaskListener<ConnectionDetails>() {
            @Override
            public void onTaskResponse(ConnectionDetails response) {
                connectionDetails = response;
                LoadProfileImageTask imagetask = new LoadProfileImageTask(ConnectionStatusActivity.this, response.getUserId(), new AsyncTaskListener<Bitmap>() {
                    @Override
                    public void onTaskResponse(Bitmap response) {
                        if(response != null)
                            image.setImageBitmap(response);
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
                imagetask.execute();
                name.setText(response.getUserName());
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                setTitle(response.getUserName());
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        task.execute();

    }
}
