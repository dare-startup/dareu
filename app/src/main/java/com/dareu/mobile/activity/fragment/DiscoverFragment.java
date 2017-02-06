package com.dareu.mobile.activity.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.decoration.SpaceItemDecoration;
import com.dareu.mobile.adapter.DiscoverUsersAdapter;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.DiscoverUsersTask;
import com.dareu.web.dto.response.entity.DiscoverUserAccount;
import com.dareu.web.dto.response.entity.Page;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiscoverUsersAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private int pageNumber = 1;
    private TextView centeredMessage;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.fragmentDiscoverRecyclerView);
        recyclerView.addItemDecoration(new SpaceItemDecoration());
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DiscoverUsersTask task = new DiscoverUsersTask(getActivity(), listener, pageNumber);
                task.execute();
            }
        });
        centeredMessage = (TextView)view.findViewById(R.id.fragmentCenteredMessage);

        DiscoverUsersTask task = new DiscoverUsersTask(getActivity(), listener, pageNumber);
        task.execute();
        return view;
    }

    private AsyncTaskListener<Page<DiscoverUserAccount>> listener = new AsyncTaskListener<Page<DiscoverUserAccount>>() {
        @Override
        public void onTaskResponse(Page<DiscoverUserAccount> response) {
            if(response.getItems().isEmpty()){
                //hide progress bar
                progressBar.setVisibility(View.GONE);
                centeredMessage.setText("There are no available items :( Try again later");
                centeredMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else{
                adapter = new DiscoverUsersAdapter(getActivity(), response.getItems());
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                centeredMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if(refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(String errorMessage) {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), errorMessage, Snackbar.LENGTH_LONG)
                    .show();
            progressBar.setVisibility(View.GONE);
            centeredMessage.setVisibility(View.GONE);
        }
    };

}
