package com.dareu.mobile.activity.fragment;


import android.content.Intent;
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
import com.dareu.mobile.activity.user.DareResponseActivity;
import com.dareu.mobile.adapter.ResponseDescriptionAdapter;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;

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
    private RecyclerView recyclerView;

    //refresh swipe layout
    private SwipeRefreshLayout refreshLayout;

    //progress bar
    private ProgressBar progressBar;

    //text info
    private TextView textView;

    private int currentPageNumber = 1;
    private DareClientService dareService;

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

        getCurrentComponents();

        switch(SharedUtils.checkInternetConnection(getActivity())){
            case NOT_CONNECTED:
                progressBar.setVisibility(View.GONE);
                textView.setText(getResources().getString(R.string.no_internet_connection));
                textView.setVisibility(View.VISIBLE);
                break;
            default:
                //executes task
                dareService.channel(currentPageNumber, SharedUtils.getStringPreference(getActivity(), PrefName.SIGNIN_TOKEN))
                        .enqueue(new Callback<Page<DareResponseDescription>>() {
                            @Override
                            public void onResponse(Call<Page<DareResponseDescription>> call, Response<Page<DareResponseDescription>> response) {
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
                                    ResponseDescriptionAdapter adapter =
                                            new ResponseDescriptionAdapter(getActivity(), response.body().getItems(), new ResponseDescriptionAdapter.ResponseDescriptionCallbacks() {
                                                @Override
                                                public void onButtonClicked(DareResponseDescription description, int position, ResponseDescriptionAdapter.ResponseDescriptionCallbackType type) {
                                                    Intent intent = null;
                                                    switch(type){
                                                        case CONTACT:
                                                            //TODO: Go to user profile activity from here
                                                            Toast.makeText(getActivity(), "Hold on, this is still on development", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        case PLAY:
                                                            //TODO: go to response activity from here
                                                            intent = new Intent(getActivity(), DareResponseActivity.class);
                                                            intent.putExtra(DareResponseActivity.DARE_RESPONSE_ID, description.getId());

                                                            startActivity(intent);
                                                            break;
                                                        case SHARE:
                                                            //TODO: Start share intent from here
                                                            Toast.makeText(getActivity(), "Hold on, this is still on development", Toast.LENGTH_SHORT).show();
                                                            break;
                                                    }
                                                }
                                            });
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<Page<DareResponseDescription>> call, Throwable t) {
                                //shows text view and hide everything
                                progressBar.setVisibility(View.GONE);
                                textView.setVisibility(View.VISIBLE);
                                textView.setText(t.getMessage());
                                recyclerView.setVisibility(View.GONE);
                            }
                        });
                break;
        }
        return currentView;
    }

    private void getCurrentComponents(){
        if(refreshLayout == null)
            refreshLayout = (SwipeRefreshLayout)currentView.findViewById(R.id.channelFragmentRefreshLayout);

        if(recyclerView == null)
            recyclerView = (RecyclerView)currentView.findViewById(R.id.channelFragmentRecyclerView);
        LinearLayoutManager mgr = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mgr);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpaceItemDecoration());
        if(progressBar == null)
            progressBar = (ProgressBar)currentView.findViewById(R.id.progressBar);

        if(textView == null)
            textView = (TextView)currentView.findViewById(R.id.channelFragmentTextView);
    }

}
