package com.dareu.mobile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.dareu.mobile.activity.fragment.ReceivedPendingRequestsFragment;
import com.dareu.mobile.activity.fragment.SentPendingRequestsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose.rubalcaba on 03/12/2017.
 */

public class PendingRequestsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public PendingRequestsPagerAdapter(FragmentManager fm){
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new ReceivedPendingRequestsFragment());
        fragments.add(new SentPendingRequestsFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Received" : "Sent";
    }
}
