package com.dareu.mobile.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.shared.NewDareActivity;
import com.dareu.mobile.adapter.MainContentPagerAdapter;
import com.dareu.mobile.adapter.WelcomeDialogAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int NEW_DARE_REQUEST_CODE = 432;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setup drawer
        setupDrawer(toolbar);
        //setup view pager
        setupViewPager();
        //setup first visit dialog
        //setupFirstVisitDialog();
        FloatingActionButton newDareButton = (FloatingActionButton)findViewById(R.id.newDareButton);
        newDareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start a new dare activity
                Intent intent = new Intent(MainActivity.this, NewDareActivity.class);
                startActivityForResult(intent, NEW_DARE_REQUEST_CODE);
            }
        });
    }

    private void setupFirstVisitDialog() {
        //check if user is for the first time here
        if(SharedUtils.getBooleanPreference(MainActivity.this, PrefName.FIRST_TIME)){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            //create view
            View welcomeDialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.welcome_dialog, null);

            //get view pager
            ViewPager pager = (ViewPager)welcomeDialogView.findViewById(R.id.welcomeDialogViewPager);

            //create adapter
            pager.setAdapter(new WelcomeDialogAdapter(getSupportFragmentManager()));

            //set listener for close label
            TextView closeView = (TextView)welcomeDialogView.findViewById(R.id.welcomeDialogCloseView);

            //set view
            builder.setView(welcomeDialogView);
            //create dialog
            final AlertDialog dialog = builder.create();
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.dimAmount = 0.0f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedUtils.setBooleanPreference(MainActivity.this, PrefName.FIRST_TIME, Boolean.FALSE);
                    //close dialog
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainContentPagerAdapter(getSupportFragmentManager()));
        TabLayout layout = (TabLayout)findViewById(R.id.tabLayout);
        layout.setupWithViewPager(viewPager);
        layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String subtitle = "";
                //change title
                switch(tab.getPosition()){
                    case 0:
                        subtitle = "Discover";
                        break;
                    case 1:
                        subtitle = "Channel";
                        break;
                    case 2:
                        subtitle = "Hottest";
                        break;
                    case 3:
                        subtitle = "Anchored";
                        break;
                }

                Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
                toolbar.setSubtitle(subtitle);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for(int i = 0; i < layout.getTabCount(); i ++){
            switch(i){
                case 0:
                    layout.getTabAt(i).setIcon(R.drawable.ic_action_discover);
                    break;
                case 1:
                    layout.getTabAt(i).setIcon(R.drawable.ic_action_channel);
                    break;
                case 2:
                    layout.getTabAt(i).setIcon(R.drawable.ic_action_fire);
                    break;
                case 3:
                    layout.getTabAt(i).setIcon(R.drawable.ic_action_anchor);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        /**SearchView searchView =
                (SearchView) menu.findItem(R.id.searchDareu).getActionView();

        //searchview suggestions adapter
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));**/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            //TODO: cases here
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
