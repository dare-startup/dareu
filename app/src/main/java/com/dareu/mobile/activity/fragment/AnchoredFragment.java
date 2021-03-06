package com.dareu.mobile.activity.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.activity.listener.RecyclerPagerScrollListener;
import com.dareu.mobile.activity.shared.ProfileActivity;
import com.dareu.mobile.activity.user.DareResponseActivity;
import com.dareu.mobile.adapter.AnchoredContentAdapter;
import com.dareu.mobile.adapter.ResponseDescriptionAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.ClapRequest;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.AnchoredDescription;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnchoredFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnchoredFragment extends Fragment {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.message)
    TextView message;

    @BindView(R.id.anchoredRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.nextPageProgressBar)
    LinearLayout nextPageProgressBar;

    private View currentView;
    private int currentPageNumber = 1;
    private AnchoredContentAdapter adapter;
    private RecyclerPagerScrollListener listener;

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
        ButterKnife.bind(this, currentView);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAnchoredContent();
            }
        });

        getAnchoredContent();
        return currentView;
    }

    private void getAnchoredContent(){
        currentPageNumber = 1;
        clientService.getAnchoredContent(currentPageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<Page<AnchoredDescription>>() {
                    @Override
                    public void onResponse(Call<Page<AnchoredDescription>> call, Response<Page<AnchoredDescription>> response) {
                        ActivityOptionsCompat options;
                        switch(response.code()){
                            case 200:
                                if(response.body().getItems().isEmpty()){
                                    progressBar.setVisibility(View.GONE);
                                    message.setText("You do not have any starred content yet");
                                    message.setVisibility(View.VISIBLE);
                                }else{
                                    listener = new RecyclerPagerScrollListener(new RecyclerPagerScrollListener.RecyclerViewPagerScrollListener() {
                                        @Override
                                        public void onScrolledToBottom() {
                                            loadNextPage();
                                        }
                                    }, response.body().getPageSize(), response.body().getPagesAvailable());
                                    adapter = new AnchoredContentAdapter(response.body().getItems(), getActivity(), new AnchoredContentAdapter.AnchoredButtonClickListener() {
                                        @Override
                                        public void onAnchoredContentClick(final AnchoredDescription desc, final int position, AnchoredContentAdapter.AnchoredDescriptionCallbackType type) {
                                            switch(type){
                                                case UNANCHOR:
                                                    //show a confirm dialog
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("Remove")
                                                            .setMessage("Do you want to remove this response from starred content?")
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
                                                                                            Toast.makeText(getActivity(), "Starred content has been removed", Toast.LENGTH_LONG)
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
                                                                                            Toast.makeText(getActivity(), "Could not remove starred content", Toast.LENGTH_LONG)
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
                                                case CONTACT:
                                                    Intent intent = new Intent(getActivity(), ProfileActivity.class);

                                                    if(SharedUtils.getCurrentProfile(getActivity())
                                                            .getId().equals(desc.getContent().getUser().getId())){
                                                        intent.putExtra(ProfileActivity.USER_PROFILE_PARAM, ProfileActivity.OWN_PROFILE);
                                                    }else{
                                                        intent.putExtra(ProfileActivity.USER_ID, desc.getContent().getUser().getId());
                                                        intent.putExtra(ProfileActivity.USER_PROFILE_PARAM, ProfileActivity.USER_PROFILE);
                                                        intent.putExtra(ProfileActivity.USER_IMAGE_URL, desc.getContent().getUser().getImageUrl());
                                                        intent.putExtra(ProfileActivity.USER_NAME, desc.getContent().getUser().getName());
                                                    }
                                                    //TODO: options = new ActivityOptionsCompat()
                                                    startActivity(intent);
                                                    break;
                                                case PLAY:
                                                    intent = new Intent(getActivity(), DareResponseActivity.class);
                                                    intent.putExtra(DareResponseActivity.DARE_RESPONSE_ID, desc.getContent().getId());
                                                    startActivity(intent);
                                                    break;
                                                case SHARE:
                                                    //TODO: WORK to do here
                                                    Toast.makeText(getActivity(), "Hold on, this is still on development", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case THUMB:
                                                    if(desc.getContent().isClapped()){
                                                        //unclap
                                                        clientService.clapResponse(new ClapRequest(desc.getContent().getId(), false), SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                                .enqueue(new Callback<UpdatedEntityResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                                        switch (response.code()){
                                                                            case 200:
                                                                                adapter.clapResponse(position, false);
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                                                                    }
                                                                });
                                                    }else{
                                                        //clap
                                                        clientService.clapResponse(new ClapRequest(desc.getContent().getId(), true),
                                                                SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                                                                .enqueue(new Callback<UpdatedEntityResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                                                                        switch (response.code()){
                                                                            case 200:
                                                                                adapter.clapResponse(position, true);
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
                                                    break;
                                            }
                                        }
                                    });
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerView.setHasFixedSize(false);
                                    recyclerView.addItemDecoration(new SpaceItemDecoration(SpaceItemDecoration.EXTRA_LARGE_SPACE));
                                    recyclerView.setAdapter(adapter);
                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.addOnScrollListener(listener);
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
    private void loadNextPage() {
        nextPageProgressBar.setVisibility(View.VISIBLE);
        currentPageNumber ++;
        listener.setLoading(true);
        listener.setPageNumber(currentPageNumber);
        clientService.getAnchoredContent(currentPageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<Page<AnchoredDescription>>() {
                    @Override
                    public void onResponse(Call<Page<AnchoredDescription>> call, Response<Page<AnchoredDescription>> response) {
                        switch(response.code()){
                            case 200:
                                if(! response.body().getItems().isEmpty()){
                                    //add all items to adapter
                                    adapter.addAll(response.body().getItems());
                                    listener.setLoading(false);
                                    nextPageProgressBar.setVisibility(View.GONE);
                                }
                                break;
                            default:
                                nextPageProgressBar.setVisibility(View.GONE);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Page<AnchoredDescription>> call, Throwable t) {

                    }
                });
    }
}
