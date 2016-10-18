package com.dareu.mobile.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dareu.mobile.R;

public class WelcomeDialogFragment extends Fragment {

    private static final String RESOURCE_ID = "param1";

    private int resourceId;


    public WelcomeDialogFragment() {
        // Required empty public constructor
    }

    public static WelcomeDialogFragment newInstance(int resourceId) {
        WelcomeDialogFragment fragment = new WelcomeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(RESOURCE_ID, resourceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            resourceId = getArguments().getInt(RESOURCE_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome_dialog, container, false);
        return view;
    }
}
