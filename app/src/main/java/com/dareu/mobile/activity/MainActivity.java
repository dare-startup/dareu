package com.dareu.mobile.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.shared.NewDareActivity;
import com.dareu.mobile.activity.shared.NewDareDataActivity;
import com.dareu.mobile.activity.shared.PendingRequestsActivity;
import com.dareu.mobile.activity.shared.SettingsActivity;
import com.dareu.mobile.activity.shared.UploadDareResponseActivity;
import com.dareu.mobile.activity.user.UnacceptedDaresActivity;
import com.dareu.mobile.activity.user.UserResponsesActivity;
import com.dareu.mobile.adapter.MainContentPagerAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.AccountProfile;
import com.dareu.web.dto.response.entity.ActiveDare;
import com.dareu.web.dto.response.entity.DareDescription;
import com.dareu.web.dto.response.entity.UnacceptedDare;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    private static final int NEW_DARE_REQUEST_CODE = 432;
    private static final int UPLOAD_DARE_RESPONSE_REQUEST_CODE = 632;


    public static final String ACTION_NEW_DARE = "com.dareu.mobile.intent.action.NEW_DARE";
    public static final String NEW_DARE_ID = "dareId";

    public static final int NEW_DARE_NOTIFICATION = 123;
    public static final int PENDING_DARE_NOTIFICATION = 321;

    private boolean activeDare;
    private boolean snackbarAvailable;

    private AccountClientService accountService;
    private DareClientService dareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountService = RetroFactory.getInstance()
                .create(AccountClientService.class);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
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
        SharedUtils.checkFirebaseRegistrationId(MainActivity.this);
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
        dareService.getActiveDare(SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<ActiveDare>() {
                    @Override
                    public void onResponse(Call<ActiveDare> call, Response<ActiveDare> response) {
                        //create snackbar countdown
                        switch(response.code()){
                            case 200:
                                createActiveDareCountdown(response.body());
                                break;
                            case 204:
                                //no dare found, check pending dare
                                checkPendingDare();
                                break;
                            default:
                                try{
                                    Log.e(TAG, response.errorBody().string());
                                }catch(IOException ex){
                                    Log.e(TAG, ex.getMessage());
                                }

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ActiveDare> call, Throwable t) {
                        //no active dare, do nothing, just log message
                        Log.i(TAG, t.getMessage());
                    }
                });
    }

    private void createActiveDareCountdown(final ActiveDare dare){
        //create snackbar
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);

        //create two dates
        try{
            Date acceptedDate = SharedUtils.DETAILS_DATE_FORMAT.parse(dare.getAcceptedDate());
            Date now = new Date();

            //get total ms from timer
            Long timerMs = dare.getTimer() * 3600000L;
            Long diff = now.getTime() - acceptedDate.getTime();
            if(diff > timerMs){
                dareService.setDareExpiration(dare.getId(), SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                        .enqueue(new Callback<UpdatedEntityResponse>() {
                            @Override
                            public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                snackbar
                                        .setText("You have an expired dare")
                                        .setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //check if there is a pending dare
                                                checkPendingDare();
                                            }
                                        }).show();
                            }

                            @Override
                            public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {
                                Log.e(TAG, "Failed to set dare expiration: " + t.getMessage());
                            }
                        });

            }else{
                Long timeLeft = timerMs - diff;
                activeDare = true;
                snackbarAvailable = false;
                snackbar.setAction("I'm ready!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, UploadDareResponseActivity.class);
                        intent.putExtra(UploadDareResponseActivity.DARE_ID, dare.getId());
                        startActivityForResult(intent, UPLOAD_DARE_RESPONSE_REQUEST_CODE);
                    }
                });
                snackbar.show();
                new CountDownTimer(timeLeft, 1000){
                    @Override
                    public void onTick(long millis) {
                        int seconds = (int) (millis / 1000) % 60 ;
                        int minutes = (int) ((millis / (1000*60)) % 60);
                        int hours   = (int) ((millis / (1000*60*60)) % 24);
                        String text = String.format("Active dare expiration timer: <font color=#F05B19>%02d hr, %02d min, %02d sec</font>",hours,minutes,seconds);
                        snackbar.setText(Html.fromHtml(text));
                    }

                    @Override
                    public void onFinish() {
                        snackbar.setText("The dare " + dare.getName() + " has expired");
                        snackbar.setAction("Dismiss", null);
                    }
                }.start();
            }

        }catch(ParseException ex){

        }
    }

    private void checkPendingDare() {
        //if there is an active dare running, do not execute this method
        if(activeDare)return;
        dareService.unacceptedDare(SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<UnacceptedDare>() {
                    @Override
                    public void onResponse(Call<UnacceptedDare> call, Response<UnacceptedDare> response) {
                        switch(response.code()){
                            case 200:
                                //pending dare
                                Intent intent = new Intent();
                                intent.putExtra(NEW_DARE_ID, response.body().getId());
                                addNotificationLayout(MainActivity.this, intent, PENDING_DARE_NOTIFICATION);
                                break;
                            case 204:
                                //TODO:no content, do nothing?
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<UnacceptedDare> call, Throwable t) {

                    }
                });
    }

    private void addNotificationLayout(final Context context, Intent intent, final int notificationType) {
        //get dare id
        String dareId = intent.getStringExtra(NEW_DARE_ID);

        final CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //load dare
        dareService.dareDescription(dareId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<DareDescription>() {
                    @Override
                    public void onResponse(Call<DareDescription> call, final Response<DareDescription> response) {
                        Spanned text;
                        if(notificationType == PENDING_DARE_NOTIFICATION)
                            text = Html.fromHtml(String.format("<font color=#FFFFFF>You have a pending dare from </font><font color=#F05B19>%s</font> <font color=#FFFFFF>called</font> <font color=#F05B19>%s</font>",
                                    response.body().getChallenger().getName(), response.body().getName()));
                        else
                            text = Html.fromHtml(String.format("<font color=#F05B19>%s</font> <font color=#FFFFFF> just dared you, want to take a look?</font>",
                                    response.body().getChallenger().getName()));
                        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Details", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent activity = new Intent(context, NewDareDataActivity.class);
                                        activity.putExtra(NewDareDataActivity.DARE_ID, response.body().getId());
                                        startActivityForResult(activity, NewDareDataActivity.PENDING_DARE_REQUEST_CODE);
                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.white));
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.darkBackground));
                        snackbar.show();
                    }

                    @Override
                    public void onFailure(Call<DareDescription> call, Throwable t) {

                    }
                });
    }


    private void setupViewPager() {
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainContentPagerAdapter(getSupportFragmentManager()));
        TabLayout layout = (TabLayout)findViewById(R.id.tabLayout);
        layout.setupWithViewPager(viewPager);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //first tab
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
                        subtitle = "Hot";
                        break;
                    case 3:
                        subtitle = "Anchored";
                        break;
                }
                viewPager.setCurrentItem(tab.getPosition());
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
                    layout.getTabAt(i).setIcon(R.drawable.ic_explore_white_24dp);
                    break;
                case 1:
                    layout.getTabAt(i).setIcon(R.drawable.ic_ondemand_video_white_24dp);
                    break;
                case 2:
                    layout.getTabAt(i).setIcon(R.drawable.ic_whatshot_white_24dp);
                    break;
                case 3:
                    layout.getTabAt(i).setIcon(R.drawable.ic_star_white_24dp);
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
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.searchDareu));

        //searchview suggestions adapter
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
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
            case R.id.navFriendshipRequests:
                intent = new Intent(this, PendingRequestsActivity.class);
                startActivity(intent);
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

            case R.id.navDareResponsesUploads:
                intent = new Intent(this, UserResponsesActivity.class);
                startActivity(intent);
                break;
            /**case R.id.navHowTo:
                Toast.makeText(this, "Hold on, this is still on development >:C", Toast.LENGTH_LONG)
                        .show();
                break;**/

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
        final ImageView image = (ImageView)headerView.findViewById(R.id.navigationViewHeaderImageView);
        final TextView subtitle = (TextView)headerView.findViewById(R.id.navigationViewHeaderSubtitleView);
        final TextView name = (TextView)headerView.findViewById(R.id.navigationViewHeaderNameView);

        accountService.accountProfile(SharedUtils.getStringPreference(MainActivity.this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<AccountProfile>() {
                    @Override
                    public void onResponse(Call<AccountProfile> call, Response<AccountProfile> response) {
                        //update current profile
                        AccountProfile profile = response.body();
                        //save
                        SharedUtils.saveCurrentProfile(profile, MainActivity.this);
                        //load image profile
                        SharedUtils.loadImagePicasso(image, MainActivity.this, profile.getImageUrl());
                        //load name
                        name.setText(profile.getName());
                        //load email
                        subtitle.setText(profile.getEmail());
                    }

                    @Override
                    public void onFailure(Call<AccountProfile> call, Throwable t) {

                    }
                });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == NEW_DARE_REQUEST_CODE && resultCode == RESULT_OK){
            //TODO: what to do after a user creates a dare
            //....
        }else if(requestCode == NewDareDataActivity.PENDING_DARE_REQUEST_CODE && resultCode == RESULT_OK){
            Boolean accepted = data.getBooleanExtra(NewDareDataActivity.ACCEPTED, false);
            if(accepted){
                //check active dare
                checkActiveDare();
            }else
                checkPendingDare();
        }else if(requestCode == UPLOAD_DARE_RESPONSE_REQUEST_CODE && resultCode == RESULT_OK){
            checkPendingDare();
        }
    }
}
