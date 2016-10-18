package com.dareu.mobile.activity.shared;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.ActivityListener;
import com.dareu.mobile.adapter.CategoriesAdapter;
import com.dareu.mobile.data.Category;
import com.dareu.mobile.task.AsyncTaskListener;
import com.dareu.mobile.task.CategoriesTask;
import com.dareu.mobile.task.CreateDareTask;
import com.dareu.mobile.task.request.NewDareRequest;
import com.dareu.mobile.task.response.ListResponse;
import com.dareu.mobile.utils.DummyFactory;
import com.dareu.mobile.utils.SharedUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class NewDareActivity extends AppCompatActivity implements ActivityListener{

    private NewDareRequest dareRequest;
    private ProgressDialog progressDialog;

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
                NavUtils.navigateUpFromSameTask(NewDareActivity.this);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void initialize() {
        dareRequest = new NewDareRequest();

        //set find friends listener
        Button findFriendsButton = (Button)findViewById(R.id.newDareFindFriendsButton);
        findFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dareRequest.getFriendsIds() != null && dareRequest.getFriendsIds().length < 6){
                    Intent intent = new Intent(NewDareActivity.this, FindFriendsActivity.class);
                    startActivityForResult(intent, FindFriendsActivity.REQUEST_CODE);
                }
            }
        });

        //execute categories task
        /**CategoriesTask task = new CategoriesTask(NewDareActivity.this, new AsyncTaskListener() {
            @Override
            public void onSuccess(String jsonText) {
                Type type = new TypeToken<ListResponse<Category>>(){}.getType();
                //parse to a list of categories
                ListResponse<Category> response = new Gson().fromJson(jsonText, type);
                if(response != null){
                    //get list of categories
                    final List<Category> categories = response.getList();
                    //create spinner adapter
                    CategoriesAdapter adapter = new CategoriesAdapter(NewDareActivity.this, categories);
                    //set to spinner
                    Spinner categoriesSpinner = (Spinner)findViewById(R.id.newDareCategorySpinner);
                    categoriesSpinner.setAdapter(adapter);
                    categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position >= 0)
                                dareRequest.setCategoryId(categories.get(position).getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            dareRequest.setCategoryId(null);
                        }
                    });
                }else{
                    //TODO: set error message or try again
                }
            }

            @Override
            public void onStatusCode(String jsonText, int statusCode) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

                if(statusCode == 401){
                    //TODO: go to signin activity and [WHAT THE HECK ARE YOU DOING HERE?]
                }
                Snackbar.make(coordinatorLayout, "Could not get categories from server", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Try again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO: try again here
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.colorAccent))
                        .show();
            }
        });
        task.execute();**/ //UNCOMMENT THIS TO TEST WITH SERVER
        CategoriesAdapter adapter = new CategoriesAdapter(NewDareActivity.this, DummyFactory.getCategories());
        //set to spinner
        Spinner categoriesSpinner = (Spinner)findViewById(R.id.newDareCategorySpinner);
        categoriesSpinner.setAdapter(adapter);
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position >= 0)
                    dareRequest.setCategoryId(DummyFactory.getCategories().get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dareRequest.setCategoryId(null);
            }
        });
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
        if(dareRequest.getFriendsIds().length == 0){
            Snackbar.make(coordinatorLayout, "You must select at least one friend", Snackbar.LENGTH_LONG)
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
        progressDialog.setMessage("Creating your new dare...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        CreateDareTask task = new CreateDareTask(NewDareActivity.this, dareRequest, new AsyncTaskListener() {
            @Override
            public void onSuccess(String jsonText) {
                progressDialog.dismiss();

            }

            @Override
            public void onStatusCode(String jsonText, int statusCode) {
                progressDialog.dismiss();
            }
        });
        task.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == FindFriendsActivity.REQUEST_CODE && resultCode == RESULT_OK){
            //get array of strings
            String[] ids = data.getStringArrayExtra(FindFriendsActivity.FRIENDS_IDS__NAME);
            //TODO: save here the array on the newDareRequest object
            ids = new String[]{}; //TODO: work here
            //TODO: CHANGE TEXTVIEW ABOVE BUTTON WITH SELECTED FRIENDS NAME
        }
    }
}
