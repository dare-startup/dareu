package com.dareu.mobile.activity.shared;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
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

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.adapter.FriendSearchAdapter;
import com.dareu.mobile.data.FriendSearch;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.FindFriendsTask;
import com.dareu.mobile.utils.DummyFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class FindFriendsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 4326;
    public static final String FRIENDS_IDS_NAME = "friendsIdsArray";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(FindFriendsActivity.this);
            }
        });**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //load all friends
        //TODO: THIS IS DUMMY LAYOUT, NEED TO EXECUTE TASK FROM HERE TO GET AL FRIENDS AND THEN FILTER THEM
        this.recyclerView = (RecyclerView)findViewById(R.id.findFriendsList);
        LinearLayoutManager manager = new LinearLayoutManager(FindFriendsActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpaceItemDecoration(8));
        //TODO: change this by a real task please
        recyclerView.setAdapter(new FriendSearchAdapter(FindFriendsActivity.this, DummyFactory.getFriendSearch()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                if(newText.isEmpty())
                    return false;
                createRequest(newText);
                return false;
            }
        });
        return true;
    }

    private void createRequest(String query){

        FindFriendsTask task = new FindFriendsTask(FindFriendsActivity.this, query, new AsyncTaskListener() {
            @Override
            public void onStatusCode(String jsonText, int statusCode) {
                //get a list
                if(statusCode == 200){
                    //parse
                    Type type = new TypeToken<List<FriendSearch>>(){}.getType();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.searchFriendsReadyItem:
                //get selected users
                FriendSearchAdapter adapter = (FriendSearchAdapter)recyclerView.getAdapter();
                ArrayList<String> users = (ArrayList)adapter.getSelectedUsers();
                if(users.isEmpty()){
                    //shows a confirmation dialog
                    createExitConfirmDialog();
                }else if(users.size() > 6){
                    createInvalidSizeDialog();
                }else
                    createConfirmDialog(users);

                break;
        }
        return true;
    }

    private void createInvalidSizeDialog() {
        new AlertDialog.Builder(FindFriendsActivity.this)
                .setMessage("You can select up to 6 users")
                .setNeutralButton("OK", null)
                .create()
                .show();
    }

    public void createConfirmDialog(final ArrayList<String> selectedUsers){
        new AlertDialog.Builder(FindFriendsActivity.this)
                .setMessage("Select " + selectedUsers.size() + " users?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //set users as result
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra(FRIENDS_IDS_NAME, selectedUsers);
                        setResult(REQUEST_CODE, intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
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
