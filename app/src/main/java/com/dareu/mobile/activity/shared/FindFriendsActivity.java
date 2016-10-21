package com.dareu.mobile.activity.shared;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.adapter.FriendSearchAdapter;
import com.dareu.mobile.utils.DummyFactory;


public class FindFriendsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 4326;
    public static final String FRIENDS_IDS__NAME = "friendsIdsArray";

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
        //searchView.setSearchableInfo(
        //        searchManager.getSearchableInfo(getComponentName()));

        return true;
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
