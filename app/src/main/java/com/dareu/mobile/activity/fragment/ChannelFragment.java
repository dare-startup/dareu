package com.dareu.mobile.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.dareu.mobile.activity.user.DareResponseActivity;
import com.dareu.mobile.adapter.ResponseDescriptionAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.ClapRequest;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChannelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelFragment extends Fragment {

    //current view
    private View currentView;

    //recycler view
    @BindView(R.id.channelFragmentRecyclerView)
    RecyclerView recyclerView;

    //refresh swipe layout
    @BindView(R.id.channelFragmentRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    //progress bar
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //text info
    @BindView(R.id.channelFragmentTextView)
    TextView textView;

    private int currentPageNumber = 1;
    private DareClientService dareService;
    private ResponseDescriptionAdapter adapter;

    public ChannelFragment() {

    }

    public static ChannelFragment newInstance() {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(currentView == null)
            currentView = getLayoutInflater(null).inflate(R.layout.fragment_channel, container, false);

        ButterKnife.bind(this, currentView);
        LinearLayoutManager mgr = new LinearLayoutManager(getActivity());
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChannel();
            }
        });
        recyclerView.setLayoutManager(mgr);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SpaceItemDecoration.EXTRA_LARGE_SPACE));
        switch(SharedUtils.checkInternetConnection(getActivity())){
            case NOT_CONNECTED:
                progressBar.setVisibility(View.GONE);
                textView.setText(getResources().getString(R.string.no_internet_connection));
                textView.setVisibility(View.VISIBLE);
                break;
            default:
                //executes task
                getChannel();
                break;
        }
        return currentView;
    }

    private void getChannel(){
        dareService.channel(currentPageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<Page<DareResponseDescription>>() {
                    @Override
                    public void onResponse(Call<Page<DareResponseDescription>> call, final Response<Page<DareResponseDescription>> response) {
                        if(response.body().getItems().isEmpty()){
                            //shows text view and hide everything
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("There are no content available right now, try again in a while");
                            recyclerView.setVisibility(View.GONE);

                        }else{
                            //hide progress bar and show text view
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            //creates an adapter
                            adapter =
                                    new ResponseDescriptionAdapter(getActivity(), response.body().getItems(), new ResponseDescriptionAdapter.ResponseDescriptionCallbacks() {
                                        @Override
                                        public void onButtonClicked(DareResponseDescription description, final int position, ResponseDescriptionAdapter.ResponseDescriptionCallbackType type, View view) {
                                            Intent intent = null;
                                            ActivityOptionsCompat options;
                                            switch(type){
                                                case CONTACT:
                                                    intent = new Intent(getActivity(), ProfileActivity.class);

                                                    if(SharedUtils.getCurrentProfile(getActivity())
                                                            .getId().equals(description.getUser().getId())){
                                                        intent.putExtra(ProfileActivity.USER_PROFILE_PARAM, ProfileActivity.OWN_PROFILE);
                                                    }else{
                                                        intent.putExtra(ProfileActivity.USER_ID, description.getUser().getId());
                                                        intent.putExtra(ProfileActivity.USER_PROFILE_PARAM, ProfileActivity.USER_PROFILE);
                                                        intent.putExtra(ProfileActivity.USER_IMAGE_URL, description.getUser().getImageUrl());
                                                        intent.putExtra(ProfileActivity.USER_NAME, description.getUser().getName());
                                                    }

                                                    options = ActivityOptionsCompat
                                                            .makeSceneTransitionAnimation(getActivity(), view, "dareResponseUserImage");
                                                    startActivity(intent, options.toBundle());
                                                    break;

                                                case PLAY:
                                                    intent = new Intent(getActivity(), DareResponseActivity.class);
                                                    intent.putExtra(DareResponseActivity.DARE_RESPONSE_ID, description.getId());
                                                    options = ActivityOptionsCompat
                                                            .makeSceneTransitionAnimation(getActivity(), view, "responseThumbImage");
                                                    startActivity(intent, options.toBundle());
                                                    break;
                                                case SHARE:
                                                    //TODO: Start share intent from here
                                                    Toast.makeText(getActivity(), "Hold on, this is still on development", Toast.LENGTH_SHORT).show();
                                                    break;

                                                case THUMB:
                                                    if(description.isClapped()){
                                                        //unclap
                                                        dareService.clapResponse(new ClapRequest(description.getId(), false), SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
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
                                                        dareService.clapResponse(new ClapRequest(description.getId(), true),
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
                                    }, ResponseDescriptionAdapter.ResponseType.DEFAULT);
                            recyclerView.setAdapter(adapter);
                            if(refreshLayout.isRefreshing()){
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Page<DareResponseDescription>> call, Throwable t) {
                        //shows text view and hide everything
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("There has been an error");
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }


}
