package com.dareu.mobile.activity.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.dareu.mobile.adapter.AnchoredContentAdapter;
import com.dareu.mobile.adapter.ResponseDescriptionAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.entity.AnchoredDescription;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnchoredFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnchoredFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView message;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private View currentView;
    private int currentPageNumber = 1;
    private AnchoredContentAdapter adapter;

    private DareClientService clientService;

    public AnchoredFragment() {
        // Required empty public constructor
    }

    public static AnchoredFragment newInstance() {
        AnchoredFragment fragment = new AnchoredFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientService = RetroFactory.getInstance()
                .create(DareClientService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(currentView != null)
            return currentView;

        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_anchored, container, false);
        progressBar = (ProgressBar)currentView.findViewById(R.id.progressBar);
        message = (TextView)currentView.findViewById(R.id.message);
        recyclerView = (RecyclerView)currentView.findViewById(R.id.anchoredRecyclerView);
        refreshLayout = (SwipeRefreshLayout)currentView.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                getAnchoredContent();
            }
        });
        getAnchoredContent();
        return currentView;
    }

    private void getAnchoredContent(){
        clientService.getAnchoredContent(currentPageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<Page<AnchoredDescription>>() {
                    @Override
                    public void onResponse(Call<Page<AnchoredDescription>> call, Response<Page<AnchoredDescription>> response) {
                        switch(response.code()){
                            case 200:
                                if(response.body().getItems().isEmpty()){
                                    progressBar.setVisibility(View.GONE);
                                    message.setText("You do not have any anchored content yet");
                                    message.setVisibility(View.VISIBLE);
                                }else{
                                    adapter = new AnchoredContentAdapter(response.body().getItems(), getActivity(), new AnchoredContentAdapter.AnchoredButtonClickListener() {
                                        @Override
                                        public void onAnchoredContentClick(final AnchoredDescription desc, final int position, AnchoredContentAdapter.AnchoredDescriptionCallbackType type) {
                                            switch(type){
                                                case UNANCHOR:
                                                    //show a confirm dialog
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("Un-anchor")
                                                            .setMessage("Do you want to un-anchor this response?")
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    clientService.unpinAnchorContent(desc.getAnchorId(),
                                                                            SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                                            .enqueue(new Callback<EntityRegistrationResponse>() {
                                                                                @Override
                                                                                public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                                                                                    switch(response.code()){
                                                                                        case 200:
                                                                                            Toast.makeText(getActivity(), "Anchored content has been removed", Toast.LENGTH_LONG)
                                                                                                    .show();
                                                                                            adapter.remove(position);
                                                                                            if(adapter.getItemCount() == 0){
                                                                                                //hide recycler view to show message
                                                                                                recyclerView.setVisibility(View.GONE);
                                                                                                message.setText("You do not have any anchored content yet");
                                                                                                message.setVisibility(View.VISIBLE);
                                                                                            }
                                                                                            break;
                                                                                        default:
                                                                                            Toast.makeText(getActivity(), "Could not remove anchored content", Toast.LENGTH_LONG)
                                                                                                    .show();
                                                                                            break;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {

                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .setNegativeButton("No", null)
                                                            .show();
                                                    break;
                                            }
                                        }
                                    });
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerView.setHasFixedSize(false);
                                    recyclerView.addItemDecoration(new SpaceItemDecoration(5));
                                    recyclerView.setAdapter(adapter);
                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    //hide refreshing if refreshing
                                    if(refreshLayout.isRefreshing())
                                        refreshLayout.setRefreshing(false);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Page<AnchoredDescription>> call, Throwable t) {

                    }
                });
    }
}
