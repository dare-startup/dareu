package com.dareu.mobile.activity.shared;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.FlagDareRequest;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.DareDescription;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlagDareActivity extends AppCompatActivity {

    public static final String DARE_ID = "dareId";

    @BindView(R.id.flagDareName)
    TextView dareName;

    @BindView(R.id.flagDareDescription)
    TextView dareDescription;

    @BindView(R.id.flagDareComment)
    EditText flagComment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.flagDareLayout)
    LinearLayout layout;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;
    private String dareId;
    private DareClientService dareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_dare);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
        ButterKnife.bind(this);
        getComponents();
    }

    private void getComponents(){
        //get dare id
        dareId = getIntent().getStringExtra(DARE_ID);
        setSupportActionBar(toolbar);
        setTitle("Flag dare");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask if really want to cancel flag dare action
                new AlertDialog.Builder(FlagDareActivity.this)
                        .setTitle("Cancel dare flag")
                        .setMessage("Want to cancel dare flag?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        dareService.dareDescription(dareId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
            .enqueue(new Callback<DareDescription>() {
                @Override
                public void onResponse(Call<DareDescription> call, Response<DareDescription> response) {
                    switch(response.code()){
                        case 200:
                            dareName.setText(response.body().getName());
                            dareDescription.setText(response.body().getDescription());
                            layout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onFailure(Call<DareDescription> call, Throwable t) {

                }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.flag_dare, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.flagDareMenuItem:
                flagDare();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void flagDare() {
        new AlertDialog.Builder(FlagDareActivity.this)
                .setTitle("Flag dare")
                .setMessage("Flag this dare?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressDialog = new ProgressDialog(FlagDareActivity.this, R.style.AppAlertDialog);
                        progressDialog.setMessage("Flagging dare");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        String comment = flagComment.getText().toString();
                        //creates the task
                        dareService.flagDare(new FlagDareRequest(dareId, comment), SharedUtils.getStringPreference(FlagDareActivity.this, PrefName.SIGNIN_TOKEN))
                                .enqueue(new Callback<EntityRegistrationResponse>() {
                                    @Override
                                    public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(FlagDareActivity.this, "The dare has been flagged", Toast.LENGTH_LONG)
                                                .show();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {

                                    }
                                });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
