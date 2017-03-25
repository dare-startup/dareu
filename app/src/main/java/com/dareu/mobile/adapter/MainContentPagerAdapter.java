package com.dareu.mobile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dareu.mobile.activity.fragment.AnchoredFragment;
import com.dareu.mobile.activity.fragment.ChannelFragment;
import com.dareu.mobile.activity.fragment.DiscoverFragment;
import com.dareu.mobile.activity.fragment.HotFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose.rubalcaba on 10/11/2016.
 */

public class MainContentPagerAdapter extends FragmentStatePagerAdapter {

    private static List<Fragment> fragments;
    private static final String[] titles = new String[]{ "Channel", "Discover", "Hot", "Anchored" };
    static{
        fragments = new ArrayList<>();
        fragments.add(ChannelFragment.newInstance());
        fragments.add(DiscoverFragment.newInstance());
        fragments.add(HotFragment.newInstance());
        fragments.add(AnchoredFragment.newInstance());
    }

    public MainContentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }**/


}
