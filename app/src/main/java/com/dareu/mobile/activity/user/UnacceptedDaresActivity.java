package com.dareu.mobile.activity.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.dareu.web.dto.response.entity.UnacceptedDare;
import com.mikhaellopez.circularimageview.CircularImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnacceptedDaresActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private CardView cardView;
    private ProgressDialog progressDialog;
    private LinearLayout unacceptedDareLayout;

    //controls
    private TextView dareName, dareDescription, dareCategory, dareTimer, dareCreationDate, message, challengerName;
    private CircularImageView challengerImage;
    private Button declineButton, acceptButton;

    private UnacceptedDare currentDareDescription;
    private DareClientService dareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unaccepted_dares);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
        getComponents();
    }

    private void getComponents() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Unaccepted dares");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        unacceptedDareLayout = (LinearLayout)findViewById(R.id.unacceptedDareLayout);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        dareName = (TextView)findViewById(R.id.unacceptedDareName);
        dareDescription = (TextView)findViewById(R.id.unacceptedDareDescription);
        //dareCategory = (TextView)findViewById(R.id.unacceptedDareCategory);
        dareTimer = (TextView)findViewById(R.id.unacceptedDareTimer);
        dareCreationDate = (TextView)findViewById(R.id.unacceptedDareCreationDate);
        message = (TextView)findViewById(R.id.unacceptedDareMessage);
        challengerName = (TextView)findViewById(R.id.unacceptedDareChallengerName);
        challengerImage = (CircularImageView)findViewById(R.id.unacceptedDareChallengerImage);
        //progress dialog
        progressDialog = new ProgressDialog(UnacceptedDaresActivity.this);
        final DareConfirmationRequest request = new DareConfirmationRequest();
        declineButton = (Button)findViewById(R.id.unacceptedDareDeclineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentDareDescription == null)return;
                request.setDareId(currentDareDescription.getId());
                request.setAccepted(false);
                new AlertDialog.Builder(UnacceptedDaresActivity.this)
                        .setMessage("Decline dare?")
                        .setTitle("Dare request")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes, decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                progressDialog.setMessage("Declining dare");
                                progressDialog.setTitle("Dare request");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                dareService.confirmDare(request, SharedUtils.getStringPreference(UnacceptedDaresActivity.this, PrefName.SIGNIN_TOKEN))
                                        .enqueue(new Callback<UpdatedEntityResponse>() {
                                            @Override
                                            public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                Toast.makeText(UnacceptedDaresActivity.this, "The dare has been declined", Toast.LENGTH_LONG)
                                                        .show();
                                                //load next dare
                                                message.setVisibility(View.GONE);
                                                cardView.setVisibility(View.GONE);
                                                progressBar.setVisibility(View.VISIBLE);
                                                progressDialog.dismiss();
                                                getUnacceptedDare();
                                            }

                                            @Override
                                            public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                                            }
                                        });
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();

            }
        });
        acceptButton = (Button)findViewById(R.id.unacceptedDareAcceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentDareDescription == null)return;
                request.setDareId(currentDareDescription.getId());
                request.setAccepted(true);
                new AlertDialog.Builder(UnacceptedDaresActivity.this)
                        .setMessage("Accept dare?")
                        .setTitle("Dare request")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes, accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                progressDialog.setMessage("Accepting dare");
                                progressDialog.setTitle("Dare request");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                dareService.confirmDare(request, SharedUtils.getStringPreference(UnacceptedDaresActivity.this, PrefName.SIGNIN_TOKEN))
                                        .enqueue(new Callback<UpdatedEntityResponse>() {
                                            @Override
                                            public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                Toast.makeText(UnacceptedDaresActivity.this, "The dare has been accepted" , Toast.LENGTH_LONG)
                                                        .show();
                                                progressDialog.dismiss();
                                                //TODO:set a flag on utils to determine if there is an active dare
                                                SharedUtils.setStringPreference(UnacceptedDaresActivity.this, PrefName.CURRENT_ACTIVE_DARE, request.getDareId());
                                                Toast.makeText(UnacceptedDaresActivity.this, "The dare has been accepted", Toast.LENGTH_LONG)
                                                        .show();
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                                            }
                                        });

                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        });
        cardView = (CardView)findViewById(R.id.unacceptedDaresCardView);
        getUnacceptedDare();
    }

    private void getUnacceptedDare(){
        dareService.unacceptedDare(SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<UnacceptedDare>() {
                    @Override
                    public void onResponse(Call<UnacceptedDare> call, Response<UnacceptedDare> response) {
                        switch(response.code()){
                            case 204:
                                //no unaccepted dare available
                                unacceptedDareLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                message.setText("You do not have any unaccepted dare right now");
                                message.setVisibility(View.VISIBLE);
                                break;
                            case 200:
                                if(response == null){
                                    message.setText("You do not have any pending dare");
                                    message.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    cardView.setVisibility(View.GONE);
                                }else{
                                    //set data
                                    currentDareDescription = response.body();
                                    dareName.setText(response.body().getName());
                                    dareDescription.setText(response.body().getDescription());
                                    dareTimer.setText(response.body().getTimer() + " hrs");
                                    dareCreationDate.setText(response.body().getCreationDate());
                                    challengerName.setText(response.body().getChallenger().getName());
                                    SharedUtils.loadImagePicasso(challengerImage, UnacceptedDaresActivity.this, response.body().getChallenger().getImageUrl());

                                    message.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    cardView.setVisibility(View.VISIBLE);
                                }
                                break;
                            case 500:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<UnacceptedDare> call, Throwable t) {

                    }
                });
    }
}
