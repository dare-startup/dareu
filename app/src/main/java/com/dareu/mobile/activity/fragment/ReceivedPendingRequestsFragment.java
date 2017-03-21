package com.dareu.mobile.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.dareu.mobile.adapter.PendingRequestsAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.ConnectionRequest;
import com.dareu.web.dto.response.entity.Page;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedPendingRequestsFragment extends Fragment {


    private View currentView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PendingRequestsAdapter adapter;
    private ProgressBar progressBar;
    private TextView message;

    private AccountClientService clientService;
    private int currentPageNumber = 1;

    public ReceivedPendingRequestsFragment() {
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
        currentView = inflater.inflate(R.layout.fragment_received_pending_requests, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)currentView.findViewById(R.id.swipeRefreshLayout);
        progressBar = (ProgressBar)currentView.findViewById(R.id.progressBar);
        message = (TextView)currentView.findViewById(R.id.message);

        switch(SharedUtils.checkInternetConnection(getActivity())){
            case NOT_CONNECTED:
                progressBar.setVisibility(View.GONE);
                message.setText(getResources().getString(R.string.no_internet_connection));
                message.setVisibility(View.VISIBLE);
                break;
            default:
                //executes a task
                clientService.getReceivedPendingRequests(currentPageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                        .enqueue(new Callback<Page<ConnectionRequest>>() {
                            @Override
                            public void onResponse(Call<Page<ConnectionRequest>> call, Response<Page<ConnectionRequest>> response) {
                                switch(response.code()){
                                    case 200:
                                        //get items
                                        adapter = new PendingRequestsAdapter(getActivity(), response.body(), new PendingRequestsAdapter.ViewClickListener() {
                                            @Override
                                            public void onViewClick(ConnectionRequest request, PendingRequestsAdapter.ButtonType type) {
                                                Intent intent;
                                                switch (type){
                                                    case ACCEPT:
                                                        clientService.confirmConnectionRequest(request.getConnectionId(), true, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                                .enqueue(new Callback<EntityRegistrationResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                                                        switch(response.code()){
                                                                            case 200:
                                                                                Toast.makeText(getActivity(), "Your request has been accepted", Toast.LENGTH_SHORT).show();
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {

                                                                    }
                                                                });
                                                        break;
                                                    case IMAGE:
                                                        //TODO: start activity
                                                        break;
                                                    case NAME:
                                                        //TODO: start activity
                                                        break;
                                                    case DECLINE:
                                                        clientService.confirmConnectionRequest(request.getConnectionId(), false, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                                .enqueue(new Callback<EntityRegistrationResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                                                        switch(response.code()){
                                                                            case 200:
                                                                                Toast.makeText(getActivity(), "Your request has been declined", Toast.LENGTH_SHORT).show();
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {

                                                                    }
                                                                });
                                                        break;
                                                }
                                            }
                                        }, false);

                                        recyclerView = (RecyclerView)currentView.findViewById(R.id.receivedPendingRequestsRecyclerView);
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
