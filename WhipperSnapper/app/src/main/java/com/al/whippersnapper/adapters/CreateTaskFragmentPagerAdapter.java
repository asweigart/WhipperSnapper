package com.al.whippersnapper.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.al.whippersnapper.fragments.TaskDetailsFragment;
import com.al.whippersnapper.fragments.TaskLocationDetails;

public class CreateTaskFragmentPagerAdapter extends FragmentPagerAdapter {
    public final int FRAGMENT_COUNT = 2;

    public CreateTaskFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return TaskDetailsFragment.newInstance();
            case 1: return TaskLocationDetails.newInstance();
            default: return null; // this will cause an error
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Details";
            case 1: return "Location";
            default: return null; // this will cause an error
        }
    }
}
