package com.dareu.mobile.activity.shared;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.adapter.FriendSearchAdapter;
import com.dareu.mobile.adapter.RecyclerViewOnItemClickListener;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.entity.FriendSearchDescription;
import com.dareu.web.dto.response.entity.Page;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FindFriendsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 4326;
    public static final String SELECTED_USER_ID = "selectedUserIdArray";
    private int pageNumber = 1;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;
    private AccountClientService accountService;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        accountService = RetroFactory.getInstance()
                .create(AccountClientService.class);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FindFriendsActivity.this)
                        .setTitle("Cancel friend search")
                        .setMessage("Are you sure to cancel friend search?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //load all friends
        this.recyclerView = (RecyclerView)findViewById(R.id.findFriendsList);
        LinearLayoutManager manager = new LinearLayoutManager(FindFriendsActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpaceItemDecoration());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        message = (TextView)findViewById(R.id.message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.find_friends, menu);
        // Associate find_friends_searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.searchFriendsMenuItem).getActionView();
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.white));
        //searchview suggestions adapter
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.isEmpty())
                    return false;
                createRequest(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    FriendSearchAdapter adapter = new FriendSearchAdapter(new ArrayList<FriendSearchDescription>(), null);
                    recyclerView.setAdapter(adapter);
                    //show message
                    TextView message = (TextView)findViewById(R.id.message);
                    message.setText("Empty result");
                    message.setVisibility(View.VISIBLE);
                    return false;
                }else{
                    createRequest(newText);
                    return false;
                }
            }
        });
        return true;
    }

    private void createRequest(String query){
        progressBar.setVisibility(View.VISIBLE);
        switch(SharedUtils.checkInternetConnection(this)){
            case NOT_CONNECTED:
                progressBar.setVisibility(View.GONE);
                message.setText(getResources().getString(R.string.no_internet_connection));
                message.setVisibility(View.VISIBLE);
                break;
            default:
                accountService.findFriends(query, pageNumber, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                        .enqueue(new Callback<Page<FriendSearchDescription>>() {
                            @Override
                            public void onResponse(Call<Page<FriendSearchDescription>> call, Response<Page<FriendSearchDescription>> response) {
                               switch(response.code()){
                                   case 200:
                                       if(response.body().getItems().isEmpty()){
                                           recyclerView.setVisibility(View.GONE);
                                           message.setText("No results to display");
                                           message.setVisibility(View.VISIBLE);
                                       }else{
                                           //creates a new adapter
                                           FriendSearchAdapter adapter = new FriendSearchAdapter(response.body().getItems(), new RecyclerViewOnItemClickListener<FriendSearchDescription>() {
                                               @Override
                                               public void onItemClickListener(int position, final FriendSearchDescription object) {
                                                   new AlertDialog.Builder(FindFriendsActivity.this)
                                                           .setTitle("Select user")
                                                           .setMessage("Do you want to select " + object.getName() + "?")
                                                           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {
                                                                   Intent intent = new Intent();
                                                                   //create array
                                                                   String[] array = new String[2];
                                                                   array[0] = object.getId();//id
                                                                   array[1] = object.getName();

                                                                   intent.putExtra(SELECTED_USER_ID, array);
                                                                   setResult(RESULT_OK, intent);
                                                                   finish();
                                                               }
                                                           })
                                                           .setNegativeButton("No", null)
                                                           .create()
                                                           .show();
                                               }
                                           });
                                           progressBar.setVisibility(View.GONE);
                                           recyclerView.setAdapter(adapter);
                                           recyclerView.setVisibility(View.VISIBLE);
                                           TextView message = (TextView)findViewById(R.id.message);
                                           message.setVisibility(View.GONE);
                                       }
                                       break;
                                   default:
                                       break;
                               }
                            }

                            @Override
                            public void onFailure(Call<Page<FriendSearchDescription>> call, Throwable t) {
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public void onBackPressed(){
        createExitConfirmDialog();
    }



    public void createExitConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FindFriendsActivity.this);
        builder.setMessage("No users has been selected, do you want to exit?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish current activity
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }


}
