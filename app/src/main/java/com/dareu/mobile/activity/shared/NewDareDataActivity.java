package com.dareu.mobile.activity.shared;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.LoadProfileImageTask;
import com.dareu.mobile.net.dare.DareDescriptionTask;
import com.dareu.mobile.net.dare.FlagDareTask;
import com.dareu.mobile.net.dare.NewDareConfirmationTask;
import com.dareu.web.dto.request.DareConfirmationRequest;
import com.dareu.web.dto.request.FlagDareRequest;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.DareDescription;
import com.mikhaellopez.circularimageview.CircularImageView;

public class NewDareDataActivity extends AppCompatActivity {

    public static final int NEW_DARE_DATA_REQUEST_CODE = 1234;
    public static final String DARE_ID = "dareId";

    private ProgressDialog progressDialog;
    private DareDescription currentDareDescription;
    private Button flagButton;
    private ProgressBar progressBar;
    private CircularImageView challengerImage;
    private TextView challengerName, dareName,
            dareDescription, dareCategory, dareTime, dareCreationDate;
    private LinearLayout layout;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dare_data);
        //get dare id
        String dareId = getIntent().getStringExtra(DARE_ID);

        if(dareId == null || dareId.isEmpty()){
            Toast.makeText(NewDareDataActivity.this, "No dare id was provided", Toast.LENGTH_LONG)
                    .show();
            finish();
        }else{
            getComponents();
            getDare(dareId);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void getDare(String dareId) {
        DareDescriptionTask task = new DareDescriptionTask(NewDareDataActivity.this, new AsyncTaskListener<DareDescription>() {
            @Override
            public void onTaskResponse(final DareDescription response) {
                if(response != null){
                    currentDareDescription = response;
                    setTitle(response.getName());
                    challengerName.setText(response.getChallenger().getName());
                    dareName.setText(response.getName());
                    dareDescription.setText(response.getDescription());
                    dareCategory.setText(response.getCategory());
                    dareTime.setText(response.getEstimatedDareTime());
                    dareCreationDate.setText(response.getCreationDate());
                    flagButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(NewDareDataActivity.this)
                                    .setTitle("Flag dare")
                                    .setMessage("Want to flag this dare?")
                                    .setPositiveButton("Yes, flag", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            progressDialog = new ProgressDialog(NewDareDataActivity.this);
                                            progressDialog.setMessage("Flagging this dare request");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();

                                            new FlagDareTask(NewDareDataActivity.this, new FlagDareRequest(response.getId(), ""), new AsyncTaskListener<EntityRegistrationResponse>() {
                                                @Override
                                                public void onTaskResponse(EntityRegistrationResponse response) {

                                                }

                                                @Override
                                                public void onError(String errorMessage) {

                                                }
                                            }).execute();
                                        }
                                    })
                                    .show();
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    //load image
                    LoadProfileImageTask task = new LoadProfileImageTask(NewDareDataActivity.this, response.getChallenger().getId(), new AsyncTaskListener<Bitmap>() {
                        @Override
                        public void onTaskResponse(Bitmap response) {
                            if(response != null)
                                challengerImage.setImageBitmap(response);
                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                    task.execute();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }, dareId);
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_dare_data, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.newDareDataDecline:
                new AlertDialog.Builder(NewDareDataActivity.this)
                        .setTitle("Decline dare")
                        .setMessage("Are you sure to decline this dare?")
                        .setPositiveButton("Yes, decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                confirmDare(false);
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
                break;
            case R.id.newDareDataAccept:
                new AlertDialog.Builder(NewDareDataActivity.this)
                        .setTitle("Accept dare")
                        .setMessage("Are you sure to accept this dare?")
                        .setPositiveButton("Yes, accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmDare(true);
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDare(final boolean accepted){
        progressBar.setVisibility(View.VISIBLE);
        new NewDareConfirmationTask(NewDareDataActivity.this, new DareConfirmationRequest(currentDareDescription.getId(), accepted), new AsyncTaskListener<UpdatedEntityResponse>() {
            @Override
            public void onTaskResponse(UpdatedEntityResponse response) {
                String value = accepted ? "accepted" : "declined";
                Toast.makeText(NewDareDataActivity.this, "Dare has been " + value, Toast.LENGTH_LONG)
                        .show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {

            }
        }).execute();
    }

    private void getComponents() {
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        challengerImage = (CircularImageView)findViewById(R.id.newDareDataChallengerImage);
        challengerName = (TextView)findViewById(R.id.newDareDataChallengerName);
        dareName = (TextView)findViewById(R.id.newDareDataDareName);
        dareDescription = (TextView)findViewById(R.id.newDareDataDareDescription);
        dareCategory = (TextView)findViewById(R.id.newDareDataDareCategory);
        dareTime = (TextView)findViewById(R.id.newDareDataDareTimer);
        dareCreationDate = (TextView)findViewById(R.id.newDareDataDareCreationDate);
        layout = (LinearLayout)findViewById(R.id.newDareDataLayout);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        flagButton = (Button)findViewById(R.id.newDareDataFlagButton);
    }
}
