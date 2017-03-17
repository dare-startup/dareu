package com.dareu.mobile.activity.user;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.adapter.ResponseCommentAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.ClapRequest;
import com.dareu.web.dto.request.NewCommentRequest;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.CommentDescription;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DareResponseActivity extends AppCompatActivity {

    //constraints
    public static final String DARE_RESPONSE_ID = "dareResponseId";
    private static final String TAG = "ResponseActivity";

    //views
    private CoordinatorLayout coordinatorLayout;
    private VideoView videoView;
    private ProgressBar videoProgressBar;
    private TextView thumbs;
    private TextView views;
    private ImageView shareButton;
    private ProgressBar commentsprogressBar;
    private RecyclerView commentsRecyclerView;
    private TextView comments;
    private ImageView commentSelfImageView;
    private EditText commentEditText;
    private ImageButton commentButton;
    private ImageButton dareResponseThumb;
    private TextView commentsMessage;

    //utility classes
    private String dareResponseId;
    private DareClientService dareService;
    private int currentPageNumber = 1;
    private ResponseCommentAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_response);
        dareResponseId = getIntent().getStringExtra(DARE_RESPONSE_ID);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
        initialize();
    }

    private void initialize() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        commentsMessage = (TextView)findViewById(R.id.dareResponseCommentsMessage);
        commentButton = (ImageButton)findViewById(R.id.dareResponseCommentButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new comment request
                createNewComment();
            }
        });
        dareResponseThumb = (ImageButton)findViewById(R.id.dareResponseThumb);
        commentEditText = (EditText) findViewById(R.id.dareResponseCommentEditText);
        commentSelfImageView = (ImageView)findViewById(R.id.dareResponseCommentImageView);
        shareButton = (ImageView)findViewById(R.id.dareResponseShare);
        commentsprogressBar = (ProgressBar)findViewById(R.id.dareResponseCommentsProgressBar);
        commentsRecyclerView = (RecyclerView)findViewById(R.id.dareResponseCommentsRecyclerView);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        videoProgressBar = (ProgressBar)findViewById(R.id.dareResponseVideoProgressBar);
        thumbs = (TextView)findViewById(R.id.dareResponseThumbs);
        comments = (TextView)findViewById(R.id.dareResponseComments);
        views = (TextView)findViewById(R.id.dareResponseViews);
        dareService.findDareResponseDescription(dareResponseId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<DareResponseDescription>() {
                    @Override
                    public void onResponse(Call<DareResponseDescription> call, final Response<DareResponseDescription> response) {
                        switch(response.code()){
                            case 200:
                                //load header
                                videoView = (VideoView)findViewById(R.id.dareResponseVideoView);
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
                                //set values
                                thumbs.setText(String.valueOf(response.body().getClaps()));
                                views.setText(String.valueOf(response.body().getViews()));
                                comments.setText(String.valueOf(response.body().getComments()));
                                dareResponseThumb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //ClapRequest request = new ClapRequest()
                                        //dareService.clapResponse()
                                        //TODO: create a new thumb request here....
                                        dareResponseThumb.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_grey));
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

                                }
                                commentsAdapter =
                                        new ResponseCommentAdapter(DareResponseActivity.this, response.body().getItems());
                                commentsRecyclerView.addItemDecoration(new SpaceItemDecoration(5));
                                commentsRecyclerView.setAdapter(commentsAdapter);
                                commentsRecyclerView.setLayoutManager(new LinearLayoutManager(DareResponseActivity.this));
                                commentsRecyclerView.setHasFixedSize(false);
                                commentsRecyclerView.setItemAnimator(new DefaultItemAnimator());

                                commentsprogressBar.setVisibility(View.GONE);
                                commentsRecyclerView.setVisibility(View.VISIBLE);
                                break;
                            case 404:
                                break;
                            case 500:
                                commentsMessage.setText("Something went wrong, try again");
                                commentsMessage.setVisibility(View.VISIBLE);
                                commentsRecyclerView.setVisibility(View.GONE);
                                commentsprogressBar.setVisibility(View.GONE);
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
                                    break;
                                case 500:
                                    break;
                                case 404:
                                    break;

                            }
                        }

                        @Override
                        public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {
                            //TODO: comment could not be created
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
}
