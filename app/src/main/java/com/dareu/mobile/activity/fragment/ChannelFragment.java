package com.dareu.mobile.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.adapter.ResponseDescriptionAdapter;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.dare.ChannelTask;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(currentView == null)
            currentView = getLayoutInflater(null).inflate(R.layout.fragment_channel, container, false);

        getCurrentComponents();

        //executes task
        new ChannelTask(getActivity(), currentPageNumber, new AsyncTaskListener<Page<DareResponseDescription>>() {
            @Override
            public void onTaskResponse(Page<DareResponseDescription> response) {
                if(response.getItems().isEmpty()){
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
                    ResponseDescriptionAdapter adapter = new ResponseDescriptionAdapter(getActivity(), response.getItems());
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onError(String errorMessage) {
                //shows text view and hide everything
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(errorMessage);
                recyclerView.setVisibility(View.GONE);
            }
        }).execute();
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


        if(progressBar == null)
            progressBar = (ProgressBar)currentView.findViewById(R.id.progressBar);

        if(textView == null)
            textView = (TextView)currentView.findViewById(R.id.channelFragmentTextView);
    }

}
