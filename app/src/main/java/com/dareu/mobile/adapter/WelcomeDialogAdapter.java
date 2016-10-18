package com.dareu.mobile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.fragment.WelcomeDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose.rubalcaba on 10/18/2016.
 */

public class WelcomeDialogAdapter extends FragmentStatePagerAdapter {

    private static List<WelcomeDialogFragment> FRAGMENTS;

    static{
        FRAGMENTS = new ArrayList<>();
        FRAGMENTS.add(WelcomeDialogFragment.newInstance(R.drawable.welcome_dialog_1));
        FRAGMENTS.add(WelcomeDialogFragment.newInstance(R.drawable.welcome_dialog_2));
        FRAGMENTS.add(WelcomeDialogFragment.newInstance(R.drawable.welcome_dialog_3));
        FRAGMENTS.add(WelcomeDialogFragment.newInstance(R.drawable.welcome_dialog_4));
    }

    public WelcomeDialogAdapter(FragmentManager fm) {
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
