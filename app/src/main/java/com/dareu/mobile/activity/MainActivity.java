package com.dareu.mobile.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.shared.NewDareActivity;
import com.dareu.mobile.activity.shared.NewDareDataActivity;
import com.dareu.mobile.activity.shared.SettingsActivity;
import com.dareu.mobile.adapter.MainContentPagerAdapter;
import com.dareu.mobile.adapter.WelcomeDialogAdapter;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.LoadProfileImageTask;
import com.dareu.mobile.net.account.UpdateRegIdTask;
import com.dareu.mobile.net.dare.DareDescriptionTask;
import com.dareu.mobile.net.dare.UnacceptedDareTask;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.DareDescription;
import com.dareu.web.dto.response.entity.UnacceptedDare;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    private static final int NEW_DARE_REQUEST_CODE = 432;

    public static final String ACTION_NEW_DARE = "com.dareu.mobile.intent.action.NEW_DARE";
    public static final String NEW_DARE_ID = "dareId";

    public static final int NEW_DARE_NOTIFICATION = 123;
    public static final int PENDING_DARE_NOTIFICATION = 321;

    private boolean activeDare;

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
        //check registration id availability
        checkFirebaseRegistrationId();
        //check if there is a pending dare
        checkPendingDare();
        //check if there is an active dare
        checkActiveDare();
        //register receiver
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                addNotificationLayout(context, intent, NEW_DARE_NOTIFICATION);
            }
        }, new IntentFilter(MainActivity.ACTION_NEW_DARE));
    }

    private void checkActiveDare() {
        //TODO: CHECK THIS OPERATION
    }

    private void checkPendingDare() {
        //if there is an active dare running, do not execute this method
        //TODO: CHECK THIS OPERATION
        if(activeDare)return;
        new UnacceptedDareTask(MainActivity.this, new AsyncTaskListener<UnacceptedDare>() {
            @Override
            public void onTaskResponse(UnacceptedDare response) {
                if(response != null){
                    //pending dare
                    Intent intent = new Intent();
                    intent.putExtra(NEW_DARE_ID, response.getId());
                    addNotificationLayout(MainActivity.this, intent, PENDING_DARE_NOTIFICATION);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        }).execute();
    }

    private void addNotificationLayout(final Context context, Intent intent, final int notificationType) {
        //get dare id
        String dareId = intent.getStringExtra(NEW_DARE_ID);

        final CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //load dare
        new DareDescriptionTask(context, new AsyncTaskListener<DareDescription>() {
            @Override
            public void onTaskResponse(final DareDescription response) {
                Spanned text;
                if(notificationType == PENDING_DARE_NOTIFICATION)
                    text = Html.fromHtml(String.format("<font color=#F05B19>You have a pending dare from </font><font color=#FFFFFF>%s</font> <font color=#F05B19>called</font> <font color=#FFFFFF>%s</font>",
                            response.getChallenger().getName(), response.getName()));
                else
                    text = Html.fromHtml(String.format("<font color=#FFFFFF>%s</font> <font color=#F05B19> just dared you, want to take a look?</font>",
                            response.getChallenger().getName()));
                Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Details", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent activity = new Intent(context, NewDareDataActivity.class);
                                activity.putExtra(NewDareDataActivity.DARE_ID, response.getId());
                                startActivity(activity);
                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.white));
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.darkBackground));
                snackbar.show();
            }

            @Override
            public void onError(String errorMessage) {

            }
        }, dareId).execute();
    }

    private void checkFirebaseRegistrationId() {
        String value = SharedUtils.getStringPreference(MainActivity.this, PrefName.ALREADY_REGISTERED_GCM_TOKEN);
        if(value != null && ! value.isEmpty()){
            Boolean updated = Boolean.parseBoolean(value);
            if(! updated){
                //get reg id
                String regId = SharedUtils.getStringPreference(MainActivity.this, PrefName.GCM_TOKEN);
                if(regId != null && ! regId.isEmpty()){
                    //update it
                    UpdateRegIdTask task = new UpdateRegIdTask(MainActivity.this, new AsyncTaskListener<UpdatedEntityResponse>() {
                        @Override
                        public void onTaskResponse(UpdatedEntityResponse response) {
                            if(response != null && response.isSuccess()){
                                SharedUtils.setStringPreference(MainActivity.this, PrefName.ALREADY_REGISTERED_GCM_TOKEN, Boolean.TRUE.toString());
                                Log.i(TAG, response.getMessage());
                            }
                            else{
                                Log.i(TAG, "Something bad just happened :(");
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                    task.execute();
                }
            }
        }



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
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setSubtitle("Discover");
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

        switch(id){
            case R.id.navSignout:
                //confirm dialog
                signout();
                break;
            case R.id.navPendingDares:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signout(){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Do you want to logout from " + getString(R.string.app_name) + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logout
                        SharedUtils.signout(MainActivity.this);
                        //go back to welcome activity
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(false)
                .create()
            .show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.navCreatedDares:
                Toast.makeText(MainActivity.this, "Hold on, this is still on development >:C", Toast.LENGTH_LONG)
                        .show();
                break;
            case R.id.navPendingDares:
                intent = new Intent(MainActivity.this, UnacceptedDaresActivity.class);
                startActivity(intent);
                break;
            case R.id.navSettings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.navSignout:
                signout();
                break;

            case R.id.navCurrentActiveDare:
                Toast.makeText(MainActivity.this, "Hold on, this is still on development >:C", Toast.LENGTH_LONG)
                        .show();
                break;
            case R.id.navDareResponsesUploads:
                Toast.makeText(MainActivity.this, "Hold on, this is still on development >:C", Toast.LENGTH_LONG)
                        .show();
                break;
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

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView image = (ImageView)headerView.findViewById(R.id.navigationViewHeaderImageView);
        TextView subtitle = (TextView)headerView.findViewById(R.id.navigationViewHeaderSubtitleView);
        TextView name = (TextView)headerView.findViewById(R.id.navigationViewHeaderNameView);

        //load profile image and name
        new LoadProfileImageTask(MainActivity.this, null, new AsyncTaskListener<Bitmap>() {
            @Override
            public void onTaskResponse(Bitmap response) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        }).execute();
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
