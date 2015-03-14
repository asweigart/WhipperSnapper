package com.al.whippersnapper.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.al.whippersnapper.fragments.CreateTaskDetailsFragment;
import com.al.whippersnapper.fragments.CreateTaskLocationFragment;

public class CreateTaskFragmentPagerAdapter extends FragmentPagerAdapter {
    public final int FRAGMENT_COUNT = 2;

    private CreateTaskDetailsFragment taskDetailsFragment;
    private CreateTaskLocationFragment taskLocationFragment;

    public CreateTaskFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        taskDetailsFragment = CreateTaskDetailsFragment.newInstance();
        taskLocationFragment = CreateTaskLocationFragment.newInstance();
    }


    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return taskDetailsFragment;
            case 1: return taskLocationFragment;
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


    public CreateTaskDetailsFragment getTaskDetailsFragment() {
        return taskDetailsFragment;
    }

    public CreateTaskLocationFragment getTaskLocationFragment() {
        return taskLocationFragment;
    }

}
