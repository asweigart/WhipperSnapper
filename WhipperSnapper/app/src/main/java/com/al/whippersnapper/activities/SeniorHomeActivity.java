package com.al.whippersnapper.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.al.whippersnapper.R;
import com.al.whippersnapper.utils.Util;

public class SeniorHomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_home);

        // set up typeface
        Typeface bikoTypeface = Typeface.createFromAsset(getAssets(), "fonts/Biko_Regular.otf");
        TextView tvAppTitle = (TextView) findViewById(R.id.tvAppTitle);
        tvAppTitle.setTypeface(bikoTypeface);

        // set up avatars
        ImageView ivAvatar1 = (ImageView) findViewById(R.id.ivAvatar1);
        ImageView ivAvatar2 = (ImageView) findViewById(R.id.ivAvatar2);
        Util.loadGifIntoImageView(this, ivAvatar1, R.raw.title_senior_avatar1);
        Util.loadGifIntoImageView(this, ivAvatar2, R.raw.title_senior_avatar2);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_senior_home, menu);
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



    public void onCreateTaskRequestClick(View v) {
        Intent i = new Intent(this, CreateTaskActivity.class);
        startActivity(i);
        // TODO - get rid of this from back stack? We might want to keep it there though.
    }
}
