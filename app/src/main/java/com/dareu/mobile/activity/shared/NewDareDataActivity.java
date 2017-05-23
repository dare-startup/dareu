package com.dareu.mobile.activity.shared;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.dareu.web.dto.response.entity.UnacceptedDare;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDareDataActivity extends AppCompatActivity {

    public static final int PENDING_DARE_REQUEST_CODE = 532;
    public static final int NEW_DARE_DATA_REQUEST_CODE = 1234;
    public static final String DARE_ID = "dareId";
    public static final String ACCEPTED = "acceptedDare";


    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.newDareDataChallengerImage)
    ImageView challengerImage;

    @BindView(R.id.newDareDataDareName)
    TextView dareName;

    @BindView(R.id.newDareDataDareDescription)
    TextView dareDescription;

    //@BindView(R.id.newDareDataDareCategory)
    //TextView dareCategory;

    @BindView(R.id.newDareDataDareTimer)
    TextView dareTime;

    @BindView(R.id.newDareDataLayout)
    LinearLayout layout;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout toolbarLayout;

    private ProgressDialog progressDialog;
    private DareDescription currentDareDescription;
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
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        toolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        if(dareId == null || dareId.isEmpty()){
            //just get a pending dare from here
            getPendingDare();
        }else{
            getDare(dareId);
        }

    }

    private void getPendingDare(){
        dareService.unacceptedDare(SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<UnacceptedDare>() {
                    @Override
                    public void onResponse(Call<UnacceptedDare> call, Response<UnacceptedDare> response) {
                        switch(response.code()){
                            case 200:
                                createDareDescription(response.body());
                                updateDareValues();
                                break;
                            case 204:
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<UnacceptedDare> call, Throwable t) {

                    }
                });
    }

    private void createDareDescription(UnacceptedDare dare){
        currentDareDescription = new DareDescription(dare.getId(), dare.getName(), dare.getDescription(),
                "", String.valueOf(dare.getTimer()), dare.getCreationDate());
        currentDareDescription.setChallenger(dare.getChallenger());
    }

    private void getDare(String dareId) {
        dareService.dareDescription(dareId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<DareDescription>() {
                    @Override
                    public void onResponse(Call<DareDescription> call, Response<DareDescription> response) {
                        switch(response.code()){
                            case 200:
                                currentDareDescription = response.body();
                                updateDareValues();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<DareDescription> call, Throwable t) {
                        Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG)
                        .show();

                        //
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void updateDareValues(){
        setTitle(currentDareDescription.getChallenger().getName());
        dareName.setText(currentDareDescription.getName());
        dareDescription.setText(currentDareDescription.getDescription());
        //dareCategory.setText(Html.fromHtml(response.body().getCategory()));
        dareTime.setText(currentDareDescription.getEstimatedDareTime() + " to complete this dare");
        progressBar.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        //load image
        SharedUtils.loadImagePicasso(challengerImage, NewDareDataActivity.this,
                currentDareDescription.getChallenger().getImageUrl());
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
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(NewDareDataActivity.this, FlagDareActivity.class);
                                intent.putExtra(FlagDareActivity.DARE_ID, dareId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDare(final boolean accepted){
        final String value = accepted ? "accepted" : "declined";
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(value + " dare");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        dareService.confirmDare(new DareConfirmationRequest(currentDareDescription.getId(), accepted), SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<UpdatedEntityResponse>() {
                    @Override
                    public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                        progressDialog.dismiss();
                        Toast.makeText(NewDareDataActivity.this, "Dare has been " + value, Toast.LENGTH_LONG)
                                .show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(ACCEPTED, accepted);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {
                        Toast.makeText(NewDareDataActivity.this, "", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }


}
