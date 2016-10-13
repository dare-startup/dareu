package com.dareu.mobile.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dareu.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnchoredFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnchoredFragment extends Fragment {

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anchored, container, false);
    }

}
