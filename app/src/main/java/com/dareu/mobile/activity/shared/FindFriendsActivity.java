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
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.FindFriendsTask;
import com.dareu.web.dto.response.entity.FriendSearchDescription;
import com.dareu.web.dto.response.entity.Page;

import java.util.ArrayList;


public class FindFriendsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 4326;
    public static final String SELECTED_USER_ID = "selectedUserIdArray";
    private int pageNumber = 1;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

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
        /**int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textview = (TextView)searchView.findViewById(id);
        textview.setTextColor(getResources().getColor(R.color.colorAccent1));**/

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
                    TextView message = (TextView)findViewById(R.id.findFriendsMessage);
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
        FindFriendsTask task = new FindFriendsTask(FindFriendsActivity.this, query, new AsyncTaskListener<Page<FriendSearchDescription>>() {
            @Override
            public void onTaskResponse(Page<FriendSearchDescription> response) {
                //creates a new adapter
                FriendSearchAdapter adapter = new FriendSearchAdapter(response.getItems(), new RecyclerViewOnItemClickListener<FriendSearchDescription>() {
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
                TextView message = (TextView)findViewById(R.id.findFriendsMessage);
                message.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG)
                        .show();
            }
        }, pageNumber);
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    private void createInvalidSizeDialog() {
        new AlertDialog.Builder(FindFriendsActivity.this)
                .setMessage("You can select up to 6 users")
                .setNeutralButton("OK", null)
                .create()
                .show();
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
