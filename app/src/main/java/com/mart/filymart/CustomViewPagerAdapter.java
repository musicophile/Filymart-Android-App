package com.mart.filymart;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {

    public CustomViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        if (position == 0) {
            fragment = new FragmentSliderOne();
        } else if (position == 1) {
            fragment = new FragmentSliderTwo();
        } else {
            fragment = new FragmentSliderThree();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        // return the number of your fragments,
        // we have 3 fragments
        return 3;
    }
}
