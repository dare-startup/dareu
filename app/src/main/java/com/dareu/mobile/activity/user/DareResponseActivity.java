package com.dareu.mobile.activity.user;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.activity.shared.ProfileActivity;
import com.dareu.mobile.adapter.ResponseCommentAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.ClapRequest;
import com.dareu.web.dto.request.NewCommentRequest;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.CommentDescription;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DareResponseActivity extends AppCompatActivity {

    //constraints
    public static final String DARE_RESPONSE_ID = "dareResponseId";
    private static final String TAG = "ResponseActivity";

    //views
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.dareResponseVideoView)
    VideoView videoView;

    @BindView(R.id.dareResponseVideoProgressBar)
    ProgressBar videoProgressBar;

    @BindView(R.id.dareResponseThumbs)
    TextView thumbs;

    @BindView(R.id.dareResponseViews)
    TextView views;

    @BindView(R.id.dareResponseShare)
    ImageView shareButton;

    @BindView(R.id.dareResponseCommentsProgressBar)
    ProgressBar commentsProgressBar;

    @BindView(R.id.dareResponseCommentsRecyclerView)
    RecyclerView commentsRecyclerView;

    @BindView(R.id.dareResponseComments)
    TextView comments;

    @BindView(R.id.dareResponseCommentImageView)
    ImageView commentSelfImageView;

    @BindView(R.id.dareResponseCommentEditText)
    EditText commentEditText;

    @BindView(R.id.dareResponseCommentButton)
    ImageButton commentButton;

    @BindView(R.id.dareResponseThumb)
    ImageButton dareResponseThumb;

    @BindView(R.id.dareResponseCommentsMessage)
    TextView commentsMessage;

    @BindView(R.id.dareResponseAnchor)
    ImageView dareResponseAnchor;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //utility classes
    private String dareResponseId;
    private DareClientService dareService;
    private int currentPageNumber = 1;
    private ResponseCommentAdapter commentsAdapter;
    private DareResponseDescription currentResponseDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_response);
        dareResponseId = getIntent().getStringExtra(DARE_RESPONSE_ID);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new comment request
                createNewComment();
            }
        });
        commentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){

                }
                return false;
            }
        });
        dareService.findDareResponseDescription(dareResponseId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<DareResponseDescription>() {
                    @Override
                    public void onResponse(Call<DareResponseDescription> call, final Response<DareResponseDescription> response) {
                        switch(response.code()){
                            case 200:
                                currentResponseDescription = response.body();
                                //load header
                                videoView.setVideoPath(response.body().getVideoUrl());
                                //creates a media controller
                                MediaController mediaController = new MediaController(DareResponseActivity.this);
                                mediaController.setAnchorView(videoView);
                                mediaController.setBackgroundColor(getResources().getColor(R.color.primaryBackgroundTransparent));
                                videoView.setMediaController(mediaController);
                                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        videoView.requestFocus();
                                        videoProgressBar.setVisibility(View.GONE);
                                        videoView.start();
                                    }
                                });
                                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        dareService.viewedResponse(currentResponseDescription.getId(),
                                                SharedUtils.getStringPreference(DareResponseActivity.this, PrefName.SIGNIN_TOKEN))
                                                .enqueue(new Callback<UpdatedEntityResponse>() {
                                                    @Override
                                                    public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                        switch(response.code()){
                                                            case 200:
                                                                //increment views text
                                                                currentResponseDescription.setViews(currentResponseDescription.getViews() + 1);
                                                                views.setText(String.valueOf(currentResponseDescription.getViews()));
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                                                    }
                                                });
                                    }
                                });
                                //set values
                                thumbs.setText(String.valueOf(response.body().getClaps()));
                                if(currentResponseDescription.isClapped())
                                    dareResponseThumb.setColorFilter(getResources().getColor(R.color.colorPrimary));

                                views.setText(String.valueOf(response.body().getViews()));
                                comments.setText(String.valueOf(response.body().getComments()));
                                if(currentResponseDescription.isAnchored()){
                                    dareResponseAnchor.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_white_24dp));
                                    dareResponseAnchor.setColorFilter(getResources().getColor(R.color.colorPrimary));
                                }


                                dareResponseAnchor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(currentResponseDescription.isAnchored()){
                                            dareService.unpinAnchorContent(currentResponseDescription.getId(),
                                                    SharedUtils.getStringPreference(DareResponseActivity.this, PrefName.SIGNIN_TOKEN))
                                                    .enqueue(new Callback<EntityRegistrationResponse>() {
                                                        @Override
                                                        public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                                            switch (response.code()){
                                                                case 200:
                                                                    Toast.makeText(DareResponseActivity.this, "This dare response has been deleted from your anchored content", Toast.LENGTH_LONG)
                                                                            .show();
                                                                    dareResponseAnchor.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_white_24dp));
                                                                    dareResponseAnchor.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                                                                    currentResponseDescription.setAnchored(false);
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {

                                                        }
                                                    });
                                        }

                                        else{
                                            dareService.anchorContent(currentResponseDescription.getId(),
                                                    SharedUtils.getStringPreference(DareResponseActivity.this, PrefName.SIGNIN_TOKEN))
                                                    .enqueue(new Callback<EntityRegistrationResponse>() {
                                                        @Override
                                                        public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                                            switch(response.code()){
                                                                case 200:
                                                                    Toast.makeText(DareResponseActivity.this, "This dare response has been added to your anchored content", Toast.LENGTH_LONG)
                                                                            .show();
                                                                    dareResponseAnchor.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_white_24dp));
                                                                    dareResponseAnchor.setColorFilter(getResources().getColor(R.color.colorPrimary));
                                                                    currentResponseDescription.setAnchored(true);
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {

                                                        }
                                                    });
                                        }

                                    }
                                });
                                dareResponseThumb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ClapRequest request = new ClapRequest();
                                        request.setResponseId(currentResponseDescription.getId());
                                        if(currentResponseDescription.isClapped()){
                                            //un-clap
                                            request.setClapped(false);
                                            currentResponseDescription.setClapped(false);
                                        }else{
                                            //clap
                                            request.setClapped(true);
                                            currentResponseDescription.setClapped(true);
                                        }
                                        dareService.clapResponse(request, SharedUtils.getStringPreference(DareResponseActivity.this, PrefName.SIGNIN_TOKEN))
                                        .enqueue(new Callback<UpdatedEntityResponse>() {
                                            @Override
                                            public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                switch(response.code()){
                                                    case 200:
                                                        Integer clapsNumber = Integer.parseInt(thumbs.getText().toString());
                                                        if(currentResponseDescription.isClapped()){
                                                            dareResponseThumb.setColorFilter(getResources().getColor(R.color.colorPrimary));
                                                            //increment clap number
                                                            clapsNumber ++;
                                                            thumbs.setText(String.valueOf(clapsNumber));
                                                        }
                                                        else{
                                                            dareResponseThumb.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                                                            //decrement claps number
                                                            if(clapsNumber < 1)return;
                                                            clapsNumber --;
                                                            thumbs.setText(String.valueOf(clapsNumber));
                                                        }

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
                                            public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                                            }
                                        });
                                    }
                                });
                                break;
                            case 404:
                                break;
                            case 500:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<DareResponseDescription> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });
        dareService.getResponseComments(currentPageNumber, dareResponseId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<Page<CommentDescription>>() {
                    @Override
                    public void onResponse(Call<Page<CommentDescription>> call, Response<Page<CommentDescription>> response) {
                        switch(response.code()){
                            case 200:
                                if(response.body().getItems().isEmpty()){
                                    commentsRecyclerView.setVisibility(View.GONE);
                                    commentsProgressBar.setVisibility(View.GONE);
                                    commentsMessage.setVisibility(View.VISIBLE);
                                    commentsMessage.setText("Be the first to comment this dare");
                                }else{
                                    commentsAdapter =
                                            new ResponseCommentAdapter(DareResponseActivity.this, response.body().getItems(), commentsListener);
                                    commentsRecyclerView.addItemDecoration(new SpaceItemDecoration(15));
                                    commentsRecyclerView.setAdapter(commentsAdapter);
                                    commentsRecyclerView.setLayoutManager(new LinearLayoutManager(DareResponseActivity.this));
                                    commentsRecyclerView.setHasFixedSize(false);
                                    commentsRecyclerView.setItemAnimator(new DefaultItemAnimator());

                                    commentsProgressBar.setVisibility(View.GONE);
                                    commentsRecyclerView.setVisibility(View.VISIBLE);
                                }
                                break;
                            case 404:
                                break;
                            case 500:
                                commentsMessage.setText("Something went wrong, try again");
                                commentsMessage.setVisibility(View.VISIBLE);
                                commentsRecyclerView.setVisibility(View.GONE);
                                commentsProgressBar.setVisibility(View.GONE);
                                break;
                        }


                    }

                    @Override
                    public void onFailure(Call<Page<CommentDescription>> call, Throwable t) {

                    }
                });
        //load comment self image
        SharedUtils.loadImagePicasso(commentSelfImageView, this, SharedUtils.getCurrentProfile(this).getImageUrl());
    }

    private void createNewComment() {
        final NewCommentRequest request = new NewCommentRequest();
        //get comment content
        String comment = commentEditText.getText().toString();
        if(comment.isEmpty()){
            Toast.makeText(this, "You must write a comment to post it", Toast.LENGTH_LONG)
                    .show();
        }else{
            request.setComment(comment);
            request.setResponseId(dareResponseId);

            dareService.createResponseComment(request, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                    .enqueue(new Callback<EntityRegistrationResponse>() {
                        @Override
                        public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                            //TODO: the comment has been created, add to recycler view
                            switch(response.code()){
                                case 200:
                                    addComment(response.body().getId());
                                    View view = getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                    break;
                                case 500:
                                    break;
                                case 404:
                                    break;

                            }
                        }

                        @Override
                        public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {
                            Log.e(TAG, t.getMessage());
                            Toast.makeText(DareResponseActivity.this, "Could not create comment, try asgain", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
        }
    }

    private void addComment(String newCommentId) {
        dareService.findCommentDescription(newCommentId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<CommentDescription>() {
                    @Override
                    public void onResponse(Call<CommentDescription> call, Response<CommentDescription> response) {
                        switch(response.code()){
                            case 200:
                                if(commentsAdapter == null){
                                    List<CommentDescription> descs = new ArrayList<CommentDescription>();
                                    commentsAdapter = new ResponseCommentAdapter(DareResponseActivity.this, descs, commentsListener);
                                    commentsRecyclerView.setAdapter(commentsAdapter);
                                }

                                commentsAdapter.add(response.body());
                                commentEditText.setText("");
                                break;
                            case 500:
                                break;
                            case 204:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentDescription> call, Throwable t) {

                    }
                });
    }

    private ResponseCommentAdapter.CommentButtonClickListener commentsListener = new ResponseCommentAdapter.CommentButtonClickListener() {
        @Override
        public void onCommentButtonClicked(final CommentDescription desc, final int position, ResponseCommentAdapter.CommentEventType type) {
            switch(type){
                case CLAP:
                    dareService.clapResponseComment(desc.getId(), SharedUtils.getStringPreference(DareResponseActivity.this, PrefName.SIGNIN_TOKEN))
                            .enqueue(new Callback<EntityRegistrationResponse>() {
                                @Override
                                public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                    switch(response.code()){
                                        case 200:
                                            if(desc.isClapped())
                                                commentsAdapter.clapComment(false, position);
                                            else
                                                commentsAdapter.clapComment(true, position);
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
                                public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {

                                }
                            });
                    break;
                case CONTACT:
                    Intent intent = new Intent(DareResponseActivity.this, ProfileActivity.class);
                    intent.putExtra(ProfileActivity.USER_PROFILE_PARAM, ProfileActivity.USER_PROFILE);
                    intent.putExtra(ProfileActivity.USER_ID, desc.getUser().getId());
                    intent.putExtra(ProfileActivity.USER_NAME, desc.getUser().getName());
                    intent.putExtra(ProfileActivity.USER_IMAGE_URL, desc.getUser().getImageUrl());
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed(){
        supportFinishAfterTransition();
    }
}
