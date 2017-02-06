package com.dareu.mobile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose.rubalcaba on 10/08/2016.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static List<Fragment> FRAGMENTS = new ArrayList<>();
    static{
        //FRAGMENTS.add(ViewPagerFragment.getInstance(R.drawable.welcome_1));
        //FRAGMENTS.add(ViewPagerFragment.getInstance(R.drawable.welcome_1));
        //FRAGMENTS.add(ViewPagerFragment.getInstance(R.drawable.welcome_1));
        //FRAGMENTS.add(ViewPagerFragment.getInstance(R.drawable.welcome_1));
    }

    public ScreenSlidePagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FRAGMENTS.get(position);
    }

    @Override
    public int getCount() {
        return FRAGMENTS.size();
    }
}
