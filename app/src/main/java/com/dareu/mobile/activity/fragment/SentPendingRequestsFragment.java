package com.dareu.mobile.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.activity.shared.ProfileActivity;
import com.dareu.mobile.adapter.PendingRequestsAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.ConnectionRequest;
import com.dareu.web.dto.response.entity.Page;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentPendingRequestsFragment extends Fragment {


    private View currentView;

    @BindView(R.id.sentPendingRequestsRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.message)
    TextView message;

    private PendingRequestsAdapter adapter;
    private AccountClientService clientService;
    private int currentPageNumber = 1;

    public SentPendingRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientService = RetroFactory.getInstance()
                .create(AccountClientService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_sent_pending_requests, container, false);
        ButterKnife.bind(this, currentView);


        switch(SharedUtils.checkInternetConnection(getActivity())){
            case NOT_CONNECTED:
                message.setText(getResources().getString(R.string.no_internet_connection));
                message.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            default:
                clientService.getSentPendingRequests(currentPageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                        .enqueue(new Callback<Page<ConnectionRequest>>() {
                            @Override
                            public void onResponse(Call<Page<ConnectionRequest>> call, final Response<Page<ConnectionRequest>> response) {
                                switch(response.code()){
                                    case 200:
                                        //get items
                                        adapter = new PendingRequestsAdapter(getActivity().getApplicationContext(), response.body().getItems(), new PendingRequestsAdapter.ViewClickListener() {
                                            @Override
                                            public void onViewClick(ConnectionRequest request, final int position, PendingRequestsAdapter.ButtonType type, View view) {
                                                Intent intent;
                                                ActivityOptionsCompat options;
                                                switch (type){
                                                    case CANCEL:
                                                        clientService.cancelFriendshipRequest(request.getConnectionId(), SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                                .enqueue(new Callback<UpdatedEntityResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                                        switch (response.code()){
                                                                            case 200:
                                                                                Toast.makeText(getActivity(), "Request has been canceled", Toast.LENGTH_LONG)
                                                                                        .show();
                                                                                adapter.remove(position);
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                                                                    }
                                                                });
                                                        break;
                                                    case IMAGE:
                                                        intent = new Intent(getActivity(), ProfileActivity.class);
                                                        intent.putExtra(ProfileActivity.USER_IMAGE_URL, request.getUser().getImageUrl());
                                                        intent.putExtra(ProfileActivity.USER_ID, request.getUser().getId());
                                                        intent.putExtra(ProfileActivity.USER_NAME, request.getUser().getName());
                                                        intent.putExtra(ProfileActivity.USER_PROFILE_PARAM, ProfileActivity.USER_PROFILE);
                                                        options = ActivityOptionsCompat
                                                                .makeSceneTransitionAnimation(getActivity(), view, "dareResponseUserImage");
                                                        startActivity(intent, options.toBundle());
                                                        break;
                                                    case NAME:
                                                        //TODO: start activity
                                                        break;
                                                }
                                            }
                                        }, true);

                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        recyclerView.addItemDecoration(new SpaceItemDecoration(5));
                                        recyclerView.setHasFixedSize(false);
                                        recyclerView.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        break;
                                    case 404:
                                        break;
                                    case 500:
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<Page<ConnectionRequest>> call, Throwable t) {

                            }
                        });
                break;
        }
        return currentView;
    }

}
