package com.dareu.mobile.activity.shared;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.activity.user.DareResponseActivity;
import com.dareu.mobile.adapter.DareResponseSmallAdapter;
import com.dareu.mobile.adapter.UserSmallAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.entity.AccountProfile;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.FriendSearchDescription;
import com.dareu.web.dto.response.entity.Page;
import com.dareu.web.dto.response.entity.UserAccount;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    public static final int OWN_PROFILE = 123;
    public static final int USER_PROFILE = 321;
    public static final String USER_PROFILE_PARAM = "userProfileName";
    public static final String USER_ID = "userProfileId";
    public static final String USER_IMAGE_URL = "userProfileImageUrl";
    public static final String USER_NAME = "userProfileUserName";

    private String currentUserId;
    private String currentImageUrl;
    private int currentProfileType;
    private String currentUserName;

    @BindView(R.id.profileImage)
    ImageView profileImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.profileDate)
    TextView registrationDate;

    @BindView(R.id.profileCoins)
    TextView coins;

    @BindView(R.id.profileScore)
    TextView score;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.profileLayout)
    LinearLayout layout;

    @BindView(R.id.profileContacts)
    RecyclerView recyclerView;

    @BindView(R.id.profileContactsMessage)
    TextView contactsMessage;

    @BindView(R.id.profileUploads)
    RecyclerView uploads;

    @BindView(R.id.profileUploadsMessage)
    TextView uploadsMessage;
    private final AccountClientService clientService =
            RetroFactory.getInstance().create(AccountClientService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        getParams();
        initialize();
    }

    /**
     * initialize profile components
     */
    private void initialize() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(currentUserName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });
        switch(currentProfileType){
            case OWN_PROFILE:
                clientService.accountProfile(SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                        .enqueue(new Callback<AccountProfile>() {
                            @Override
                            public void onResponse(Call<AccountProfile> call, Response<AccountProfile> response) {
                                loadUserProfileResponse(response);
                            }

                            @Override
                            public void onFailure(Call<AccountProfile> call, Throwable t) {

                            }
                        });
                break;
            case USER_PROFILE:
                //load image profile
                SharedUtils.loadImagePicasso(profileImage, this, currentImageUrl);
                clientService.getUserAccount(currentUserId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                        .enqueue(new Callback<AccountProfile>() {
                            @Override
                            public void onResponse(Call<AccountProfile> call, Response<AccountProfile> response) {
                                loadUserProfileResponse(response);
                            }

                            @Override
                            public void onFailure(Call<AccountProfile> call, Throwable t) {

                            }
                        });
                break;
        }
    }

    private void loadUserProfileResponse(Response<AccountProfile> response){
        switch(response.code()){
            case 200:
                loadUserProfile(response.body());
                break;
            default:
                //TODO: check codes and message
                break;
        }
    }

    /**
     * Load an account profile object
     * @param profile
     */
    private void loadUserProfile(AccountProfile profile) {
        //load current image
        SharedUtils.loadImagePicasso(profileImage, this, profile.getImageUrl());
        //date
        registrationDate.setText("Registered since " + profile.getUserSinceDate());
        coins.setText(String.valueOf(profile.getCoins()));

        score.setText(String.valueOf(profile.getUscore()));

        //data section
        dataSection(profile);

        //uploads section
        uploadsSection(profile.getCreatedResponses());

        //contacts section
        contactsSection(profile.getContacts());

        progressBar.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);

    }

    private void contactsSection(Page<FriendSearchDescription> contacts) {

        if(contacts.getItems().isEmpty()){
            recyclerView.setVisibility(View.GONE);
            contactsMessage.setText("User do not have any contact yet");
            contactsMessage.setVisibility(View.VISIBLE);
        }else{
            UserSmallAdapter adapter = new UserSmallAdapter(contacts.getItems(), new UserSmallAdapter.UserSmallAdapterListener() {
                @Override
                public void onButtonClickListener(FriendSearchDescription description, int position, UserSmallAdapter.UserSmallAdapterEventType type) {

                }
            });
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.addItemDecoration(new SpaceItemDecoration(15, true));
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            contactsMessage.setVisibility(View.GONE);
        }
    }

    /**
     * Set up data section
     * @param profile
     */
    private void dataSection(AccountProfile profile) {

    }

    /**
     * Set up uploads section
     * @param descriptions
     */
    private void uploadsSection(Page<DareResponseDescription> descriptions){

        if(descriptions.getItems().isEmpty()){
            uploads.setVisibility(View.GONE);
            uploadsMessage.setText("User do not have any uploaded responses, why don't you dare him? ");
            uploadsMessage.setVisibility(View.VISIBLE);
        }else {
            DareResponseSmallAdapter adapter = new DareResponseSmallAdapter(descriptions.getItems(), new DareResponseSmallAdapter.SmallResponseDescriptionListener() {
                @Override
                public void onButtonClicked(DareResponseDescription description, int position, DareResponseSmallAdapter.EventType type) {
                    switch (type) {
                        case VIEW:
                            Intent intent = new Intent(ProfileActivity.this, DareResponseActivity.class);
                            intent.putExtra(DareResponseActivity.DARE_RESPONSE_ID, description.getId());
                            startActivity(intent);
                            break;
                    }
                }
            });

            //configuration
            uploads.setHasFixedSize(false);
            uploads.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            uploads.addItemDecoration(new SpaceItemDecoration(15, true));
            uploads.setVisibility(View.VISIBLE);
            uploadsMessage.setVisibility(View.GONE);
            uploads.setAdapter(adapter);
        }
    }

    /**
     * Get extra params from intent
     */
    private void getParams(){
        currentProfileType = getIntent().getIntExtra(USER_PROFILE_PARAM, OWN_PROFILE);
        currentUserId = currentProfileType == OWN_PROFILE
                ? SharedUtils.getCurrentProfile(this).getId() // get current profile
                : getIntent().getStringExtra(USER_ID); //get user id from extra params
        switch(currentProfileType){
            case OWN_PROFILE:
                currentImageUrl = SharedUtils.getCurrentProfile(this)
                                    .getImageUrl();
                currentUserName = SharedUtils.getCurrentProfile(this)
                        .getName();
                break;
            case USER_PROFILE:
                currentImageUrl = getIntent().getStringExtra(USER_IMAGE_URL);
                currentUserName = getIntent().getStringExtra(USER_NAME);
                break;
        }
    }

    @Override
    public void onBackPressed(){
        supportFinishAfterTransition();
    }
}
