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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.adapter.CategoriesAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.CreateDareRequest;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.CategoryDescription;
import com.dareu.web.dto.response.entity.Page;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDareActivity extends AppCompatActivity {

    private CreateDareRequest dareRequest;
    private ProgressDialog progressDialog;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.newDareSelectedUser)
    TextView selectedUserView;

    @BindView(R.id.newDareFindFriendsButton)
    Button findFriendsButton;

    @BindView(R.id.newDareCategorySpinner)
    Spinner categoriesSpinner;

    @BindView(R.id.newDareTimerSpinner)
    Spinner timerSpinner;

    @BindView(R.id.newDareNameView)
    EditText nameView;

    @BindView(R.id.newDareDescriptionView)
    EditText descView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static final String DARE_CREATION_STATUS = "dareCreationStatus";

    private DareClientService dareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dare);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
        ButterKnife.bind(this);
        initialize();

    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Do you want to cancel new dare creation?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        supportFinishAfterTransition();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void initialize() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(NewDareActivity.this)
                        .setTitle("Exit")
                        .setMessage("Do you want to cancel new dare creation?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                supportFinishAfterTransition();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        dareRequest = new CreateDareRequest();
        //set find friends listener
        findFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDareActivity.this, FindFriendsActivity.class);
                startActivityForResult(intent, FindFriendsActivity.REQUEST_CODE);
            }
        });

        dareService.getCategories(1, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<Page<CategoryDescription>>() {
                    @Override
                    public void onResponse(Call<Page<CategoryDescription>> call, final Response<Page<CategoryDescription>> response) {
                        //create spinner adapter
                        CategoriesAdapter adapter = new CategoriesAdapter(NewDareActivity.this, response.body().getItems());
                        //set to spinner
                        categoriesSpinner.setAdapter(adapter);
                        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position >= 0)
                                    dareRequest.setCategoryId(response.body().getItems().get(position).getId());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                dareRequest.setCategoryId(null);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Page<CategoryDescription>> call, Throwable t) {

                    }
                });
        //set timer spinner
        timerSpinner.setAdapter(new ArrayAdapter<String>(NewDareActivity.this,
                R.layout.support_simple_spinner_dropdown_item, SharedUtils.TIMERS));
        timerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] array = SharedUtils.TIMERS[position].split(" ");
                dareRequest.setTimer(Integer.parseInt(array[0]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dareRequest.setTimer(0);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.new_dare, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.saveNewDare:
                saveDare();
                break;
        }
        return true;
    }

    private void saveDare() {
        dareRequest.setName(nameView.getText().toString());
        dareRequest.setDescription(descView.getText().toString());
        //validate request
        if(dareRequest.getFriendId() == null){
            Snackbar.make(coordinatorLayout, "You must select a friend", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }else if(dareRequest.getCategoryId() == null || dareRequest.getCategoryId().isEmpty()){
            Snackbar.make(coordinatorLayout, "You must set a category", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }else if(dareRequest.getDescription() == null || dareRequest.getDescription().isEmpty()){
            Snackbar.make(coordinatorLayout, "You must provide a description", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }else if(dareRequest.getName() == null || dareRequest.getName().isEmpty()){
            Snackbar.make(coordinatorLayout, "Please, name your new dare", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }else if(dareRequest.getTimer() == 0){
            Snackbar.make(coordinatorLayout, "Select a timer", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        progressDialog = new ProgressDialog(NewDareActivity.this, R.style.AppAlertDialog);
        progressDialog.setMessage("Creating new dare...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        dareService.createDare(dareRequest, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<EntityRegistrationResponse>() {
                    @Override
                    public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                        Toast.makeText(NewDareActivity.this, "Your dare has been created", Toast.LENGTH_LONG)
                                .show();
                        Intent intent = new Intent();
                        intent.putExtra(DARE_CREATION_STATUS, Boolean.TRUE.toString());
                        setResult(RESULT_OK, intent);
                        progressDialog.dismiss();
                        supportFinishAfterTransition();
                    }

                    @Override
                    public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(NewDareActivity.this, "Could not create dare, please try again", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == FindFriendsActivity.REQUEST_CODE && resultCode == RESULT_OK){
            //get array of strings
            String[] userData = data.getStringArrayExtra(FindFriendsActivity.SELECTED_USER_ID);
            //id
            String id = userData[0];
            String name = userData[1];

            dareRequest.setFriendId(id);
            selectedUserView.setText("I want to dare " + name);
        }
    }
}
