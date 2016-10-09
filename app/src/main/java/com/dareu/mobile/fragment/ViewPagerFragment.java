package com.dareu.mobile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dareu.mobile.R;

/**
 * Created by jose.rubalcaba on 10/08/2016.
 */

public class ViewPagerFragment extends Fragment {

    private int resourceId;

    public static final String RESOURCE_ID = "resourceId";

    @Override
    public void onCreate(Bundle savedInstance){
        //get index
        this.resourceId = getArguments().getInt(RESOURCE_ID);
    }

    public static ViewPagerFragment getInstance(int resourceId){
        Bundle bundle = new Bundle();
        bundle.putInt(RESOURCE_ID, resourceId);

        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle){
        View view = inflater.inflate(R.layout.view_pager_welcome_item, parent, false);
        return view;
    }
}
