package com.dareu.mobile.activity.fragment;


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
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.ConnectionRequest;
import com.dareu.web.dto.response.entity.Page;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentPendingRequestsFragment extends Fragment {


    private View currentView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PendingRequestsAdapter adapter;
    private ProgressBar progressBar;
    private TextView message;

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
        recyclerView = (RecyclerView)currentView.findViewById(R.id.sentPendingRequestsRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout)currentView.findViewById(R.id.swipeRefreshLayout);
        progressBar = (ProgressBar)currentView.findViewById(R.id.progressBar);
        message = (TextView)currentView.findViewById(R.id.message);

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
                                        adapter = new PendingRequestsAdapter(getActivity().getApplicationContext(), response.body(), new PendingRequestsAdapter.ViewClickListener() {
                                            @Override
                                            public void onViewClick(ConnectionRequest request, PendingRequestsAdapter.ButtonType type) {
                                                switch (type){
                                                    case CANCEL:
                                                        clientService.cancelFriendshipRequest(request.getConnectionId(), SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                                .enqueue(new Callback<UpdatedEntityResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                                        switch (response.code()){
                                                                            case 200:
                                                                                Toast.makeText(getActivity(), "The request has been canceled", Toast.LENGTH_LONG)
                                                                                        .show();
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
                                                        //TODO: start activity
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
