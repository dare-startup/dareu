package com.dareu.mobile.activity.shared;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.DareConfirmationRequest;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.DareDescription;
import com.mikhaellopez.circularimageview.CircularImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDareDataActivity extends AppCompatActivity {

    public static final int PENDING_DARE_REQUEST_CODE = 532;
    public static final int NEW_DARE_DATA_REQUEST_CODE = 1234;
    public static final String DARE_ID = "dareId";
    public static final String ACCEPTED = "acceptedDare";

    private ProgressDialog progressDialog;
    private DareDescription currentDareDescription;
    private ProgressBar progressBar;
    private CircularImageView challengerImage;
    private TextView challengerName, dareName,
            dareDescription, dareCategory, dareTime;
    private LinearLayout layout;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    private String dareId;
    private DareClientService dareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dare_data);
        //get dare id
        dareId = getIntent().getStringExtra(DARE_ID);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
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
        dareService.dareDescription(dareId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<DareDescription>() {
                    @Override
                    public void onResponse(Call<DareDescription> call, Response<DareDescription> response) {
                        if(response != null){
                            currentDareDescription = response.body();
                            setTitle(response.body().getName());
                            challengerName.setText(response.body().getChallenger().getName());
                            dareName.setText(response.body().getName());
                            dareDescription.setText(response.body().getDescription());
                            dareCategory.setText(response.body().getCategory());
                            dareTime.setText(response.body().getEstimatedDareTime());
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            //load image
                            SharedUtils.loadImagePicasso(challengerImage, NewDareDataActivity.this,
                                    response.body().getChallenger().getImageUrl());

                        }
                    }

                    @Override
                    public void onFailure(Call<DareDescription> call, Throwable t) {
                        Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
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
            case R.id.newDareDataFlag:
                new AlertDialog.Builder(NewDareDataActivity.this)
                        .setTitle("Flag dare")
                        .setMessage("Want to flag this dare?")
                        .setPositiveButton("Yes, flag", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(NewDareDataActivity.this, FlagDareActivity.class);
                                intent.putExtra(FlagDareActivity.DARE_ID, dareId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDare(final boolean accepted){
        progressBar.setVisibility(View.VISIBLE);
        dareService.confirmDare(new DareConfirmationRequest(currentDareDescription.getId(), accepted), SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<UpdatedEntityResponse>() {
                    @Override
                    public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                        String value = accepted ? "accepted" : "declined";
                        Toast.makeText(NewDareDataActivity.this, "Dare has been " + value, Toast.LENGTH_LONG)
                                .show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(ACCEPTED, accepted);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                    }
                });
    }

    private void getComponents() {
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        challengerImage = (CircularImageView)findViewById(R.id.newDareDataChallengerImage);
        challengerName = (TextView)findViewById(R.id.newDareDataChallengerName);
        dareName = (TextView)findViewById(R.id.newDareDataDareName);
        dareDescription = (TextView)findViewById(R.id.newDareDataDareDescription);
        dareCategory = (TextView)findViewById(R.id.newDareDataDareCategory);
        dareTime = (TextView)findViewById(R.id.newDareDataDareTimer);
        layout = (LinearLayout)findViewById(R.id.newDareDataLayout);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
    }
}
