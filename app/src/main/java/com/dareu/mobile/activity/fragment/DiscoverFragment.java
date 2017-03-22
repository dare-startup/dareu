package com.dareu.mobile.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.activity.shared.ProfileActivity;
import com.dareu.mobile.adapter.DiscoverUsersAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.DiscoverUserAccount;
import com.dareu.web.dto.response.entity.Page;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    private static final String TAG = "DiscoverFragment";
    private RecyclerView recyclerView;
    private DiscoverUsersAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private int pageNumber = 1;
    private TextView centeredMessage;

    private AccountClientService accountService;

    public DiscoverFragment() {
        // Required empty public constructor

    }

    /**
     * factory
     * @return
     */
    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountService = RetroFactory.getInstance()
                .create(AccountClientService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.fragmentDiscoverRecyclerView);
        recyclerView.addItemDecoration(new SpaceItemDecoration(5));
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Call<Page<DiscoverUserAccount>> discoverCall =
                        accountService.discoverUsers(pageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN));
                discoverCall.enqueue(call);
            }
        });
        centeredMessage = (TextView)view.findViewById(R.id.fragmentCenteredMessage);

        switch(SharedUtils.checkInternetConnection(getActivity())){
            case NOT_CONNECTED:
                progressBar.setVisibility(View.GONE);
                centeredMessage.setVisibility(View.VISIBLE);
                centeredMessage.setText(getResources().getString(R.string.no_internet_connection));
                break;
            default:
                Call<Page<DiscoverUserAccount>> discoverCall =
                        accountService.discoverUsers(pageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN));
                discoverCall.enqueue(call);
                break;
        }
        return view;
    }

    private Callback<Page<DiscoverUserAccount>> call = new Callback<Page<DiscoverUserAccount>>() {
        @Override
        public void onResponse(Call<Page<DiscoverUserAccount>> call, Response<Page<DiscoverUserAccount>> response) {
            switch (response.code()){
                case 200:
                    if(response.body().getItems().isEmpty()){
                        //hide progress bar
                        progressBar.setVisibility(View.GONE);
                        centeredMessage.setText("There are no available items :( Try again later");
                        centeredMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else{
                        adapter = new DiscoverUsersAdapter(getActivity(), response.body().getItems(), new DiscoverUsersAdapter.OnButtonClicked() {
                            @Override
                            public void onButtonClicked(DiscoverUserAccount account, DiscoverUsersAdapter.ButtonType type, final int position) {
                                switch (type){
                                    case ADD:
                                        accountService.requestConnection(account.getId(),
                                                SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                .enqueue(new Callback<EntityRegistrationResponse>() {
                                                    @Override
                                                    public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                                        Toast.makeText(getActivity(), "You request has been sent", Toast.LENGTH_LONG)
                                                                .show();
                                                        adapter.remove(position);
                                                    }

                                                    @Override
                                                    public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {
                                                        Toast.makeText(getActivity(), "There has been an error", Toast.LENGTH_LONG)
                                                                .show();
                                                    }
                                                });
                                        break;
                                    case CONTACT:
                                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                        intent.putExtra(ProfileActivity.USER_PROFILE_PARAM, ProfileActivity.USER_PROFILE);
                                        intent.putExtra(ProfileActivity.USER_IMAGE_URL, account.getImageUrl());
                                        intent.putExtra(ProfileActivity.USER_ID, account.getId());
                                        intent.putExtra(ProfileActivity.USER_NAME, account.getName());
                                        startActivity(intent);
                                        break;
                                }
                            }
                        });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        centeredMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if(refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                    }
                    break;
                case 500:
                    Toast.makeText(getActivity(), "There has been an error, try again", Toast.LENGTH_LONG)
                            .show();
                    break;
            }
        }

        @Override
        public void onFailure(Call<Page<DiscoverUserAccount>> call, Throwable t) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), t.getMessage(), Snackbar.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
            centeredMessage.setVisibility(View.GONE);
        }
    } ;

}
