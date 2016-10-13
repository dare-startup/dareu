package com.dareu.mobile.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dareu.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotFragment extends Fragment {

    public HotFragment() {
        // Required empty public constructor
    }
    public static HotFragment newInstance() {
        HotFragment fragment = new HotFragment();
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
        return inflater.inflate(R.layout.fragment_hot, container, false);
    }

}
