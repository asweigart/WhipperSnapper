package com.al.whippersnapper.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.al.whippersnapper.fragments.FindTaskListViewFragment;
import com.al.whippersnapper.fragments.FindTaskMapViewFragment;

/**
 * Created by Al on 3/13/2015.
 */
public class FindTaskFragmentPagerAdapter extends FragmentPagerAdapter {
    public final int FRAGMENT_COUNT = 2;
    private FindTaskMapViewFragment mapViewFragment;
    private FindTaskListViewFragment listViewFragment;


    public FindTaskFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mapViewFragment = FindTaskMapViewFragment.newInstance();
        listViewFragment = FindTaskListViewFragment.newInstance();
    }


    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return mapViewFragment;
            case 1: return listViewFragment;
            default: return null; // this will cause an error
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Map";
            case 1: return "List";
            default: return null; // this will cause an error
        }
    }
}
