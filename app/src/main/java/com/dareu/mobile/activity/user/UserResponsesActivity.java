package com.dareu.mobile.activity.user;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.adapter.ResponseDescriptionAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserResponsesActivity extends AppCompatActivity {


    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.message)
    TextView message;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private DareClientService clientService;
    private int currentPageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_responses);
        ButterKnife.bind(this);
        getComponents();
    }

    private void getComponents() {
        clientService = RetroFactory.getInstance()
                .create(DareClientService.class);
        //get toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setTitle("Uploaded responses");
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO: do refresh here
            }
        });
        clientService.findUserResponses(currentPageNumber, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
        .enqueue(new Callback<Page<DareResponseDescription>>() {
            @Override
            public void onResponse(Call<Page<DareResponseDescription>> call, Response<Page<DareResponseDescription>> response) {
                //check response
                switch (response.code()){
                    case 200:
                        if(response.body().getItems().isEmpty()){
                            //hide progress bar and show message
                            progressBar.setVisibility(View.GONE);
                            message.setText("You haven't uploaded any response");
                            message.setVisibility(View.VISIBLE);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            //creates adapter here
                            ResponseDescriptionAdapter adapter = new ResponseDescriptionAdapter(UserResponsesActivity.this, response.body().getItems(),
                                    new ResponseDescriptionAdapter.ResponseDescriptionCallbacks() {
                                        @Override
                                        public void onButtonClicked(DareResponseDescription description, int position, ResponseDescriptionAdapter.ResponseDescriptionCallbackType type, View view) {
                                            switch (type){
                                                case CONTACT:

                                                    break;
                                                case MENU:
                                                    break;
                                                case PLAY:
                                                    break;
                                                case SHARE:
                                                    break;
                                            }
                                        }
                                    }, ResponseDescriptionAdapter.ResponseType.DEFAULT);
                            recyclerView.setHasFixedSize(false);
                            recyclerView.setLayoutManager(new LinearLayoutManager(UserResponsesActivity.this));
                            recyclerView.addItemDecoration(new SpaceItemDecoration(5));
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 500:
                        message.setText("There has been an error");
                        message.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        break;
                    case 204:
                        break;
                }
            }

            @Override
            public void onFailure(Call<Page<DareResponseDescription>> call, Throwable t) {

            }
        });
    }
}
