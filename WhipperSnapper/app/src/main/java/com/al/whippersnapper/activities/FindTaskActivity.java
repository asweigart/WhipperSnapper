package com.al.whippersnapper.activities;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.al.whippersnapper.R;
import com.al.whippersnapper.adapters.CreateTaskFragmentPagerAdapter;
import com.al.whippersnapper.adapters.FindTaskFragmentPagerAdapter;
import com.al.whippersnapper.adapters.TaskAdapter;
import com.al.whippersnapper.fragments.FindTaskListViewFragment;
import com.al.whippersnapper.fragments.FindTaskMapViewFragment;
import com.al.whippersnapper.models.ParseWSUser;
import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

public class FindTaskActivity extends FragmentActivity implements
        FindTaskMapViewFragment.OnFragmentInteractionListener,
        FindTaskListViewFragment.OnFragmentInteractionListener {

    private FindTaskFragmentPagerAdapter pagerAdapter;


    private ArrayAdapter<ParseWSUser> lvAdapter;
    private ArrayList<ParseWSUser> taskItems;



    public ArrayAdapter<ParseWSUser> getLvAdapter() {
        return lvAdapter;
    }

    public ArrayList<ParseWSUser> getTaskItems() {
        return taskItems;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_task);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerFindTask);
        pagerAdapter = new FindTaskFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabsFindTask);
        tabsStrip.setViewPager(viewPager);

        taskItems = new ArrayList<ParseWSUser>();
        lvAdapter = new TaskAdapter(this, taskItems); // TODO - not sure if getActivity() will work here
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) { }
}
