package com.dareu.mobile.activity.shared;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import com.dareu.mobile.activity.ActivityListener;
import com.dareu.mobile.adapter.CategoriesAdapter;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.dare.CategoriesTask;
import com.dareu.mobile.net.dare.CreateDareTask;
import com.dareu.web.dto.request.CreateDareRequest;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.CategoryDescription;
import com.dareu.web.dto.response.entity.Page;

public class NewDareActivity extends AppCompatActivity implements ActivityListener{

    private CreateDareRequest dareRequest;
    private ProgressDialog progressDialog;
    private TextView selectedUserView;

    public static final String DARE_CREATION_STATUS = "dareCreationStatus";

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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        selectedUserView = (TextView)findViewById(R.id.newDareSelectedUser);
    }

    @Override
    public void initialize() {
        dareRequest = new CreateDareRequest();
        //set find friends listener
        Button findFriendsButton = (Button)findViewById(R.id.newDareFindFriendsButton);
        findFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDareActivity.this, FindFriendsActivity.class);
                startActivityForResult(intent, FindFriendsActivity.REQUEST_CODE);
            }
        });


        CategoriesTask task = new CategoriesTask(NewDareActivity.this, 1, new AsyncTaskListener<Page<CategoryDescription>>() {
            @Override
            public void onTaskResponse(final Page<CategoryDescription> response) {
                //create spinner adapter
                CategoriesAdapter adapter = new CategoriesAdapter(NewDareActivity.this, response.getItems());
                //set to spinner
                Spinner categoriesSpinner = (Spinner)findViewById(R.id.newDareCategorySpinner);
                categoriesSpinner.setAdapter(adapter);
                categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position >= 0)
                            dareRequest.setCategoryId(response.getItems().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        dareRequest.setCategoryId(null);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        task.execute();
        //set timer spinner
        Spinner timerSpinner = (Spinner)findViewById(R.id.newDareTimerSpinner);
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

        //get name
        EditText nameView = (EditText)findViewById(R.id.newDareNameView);
        EditText descView = (EditText)findViewById(R.id.newDareDescriptionView);

        dareRequest.setName(nameView.getText().toString());
        dareRequest.setDescription(descView.getText().toString());

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
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

        progressDialog = new ProgressDialog(NewDareActivity.this);
        progressDialog.setMessage("Creating new dare...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        CreateDareTask task = new CreateDareTask(NewDareActivity.this, dareRequest, new AsyncTaskListener<EntityRegistrationResponse>() {
            @Override
            public void onTaskResponse(EntityRegistrationResponse response) {
                Toast.makeText(NewDareActivity.this, "Your dare has been created", Toast.LENGTH_LONG)
                    .show();
                Intent intent = new Intent();
                intent.putExtra(DARE_CREATION_STATUS, Boolean.TRUE.toString());
                setResult(RESULT_OK, intent);
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(NewDareActivity.this, "Could not create dare, please try again", Toast.LENGTH_LONG)
                        .show();
            }
        });
        task.execute();
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
