package com.al.whippersnapper.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.al.whippersnapper.R;
import com.al.whippersnapper.utils.GifAnimationDrawable;
import com.al.whippersnapper.utils.Util;

import java.io.IOException;

public class UserTypePickerActivity extends ActionBarActivity {
    public static final int USER_TYPE_REQUEST_CODE = 42;
    private Typeface bikoTypeface;
    private TextView tvAppTitle;
    private Button btnIAmASenior;
    private Button btnIAmAVolunteer;
    private ImageView ivTitleSeniorAvatar1;
    private ImageView ivTitleSeniorAvatar2;
    private ImageView ivTitleVolunteerAvatar1;
    private ImageView ivTitleVolunteerAvatar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_picker);

        bikoTypeface = Typeface.createFromAsset(getAssets(), "fonts/Biko_Regular.otf");
        tvAppTitle = (TextView) findViewById(R.id.tvAppTitle);
        tvAppTitle.setTypeface(bikoTypeface);

        btnIAmASenior = (Button) findViewById(R.id.btnIAmASenior);
        btnIAmAVolunteer = (Button) findViewById(R.id.btnIAmAVolunteer);

        btnIAmASenior.setTypeface(bikoTypeface);
        btnIAmAVolunteer.setTypeface(bikoTypeface);

        // tagline
        ((TextView) findViewById(R.id.tvTagLineLabel)).setTypeface(bikoTypeface);

        // load the animated gifs of the little avatars
        ivTitleSeniorAvatar1 = (ImageView) findViewById(R.id.ivTitleSeniorAvatar1);
        ivTitleSeniorAvatar2 = (ImageView) findViewById(R.id.ivTitleSeniorAvatar2);
        ivTitleVolunteerAvatar1 = (ImageView) findViewById(R.id.ivTitleVolunteerAvatar1);
        ivTitleVolunteerAvatar2 = (ImageView) findViewById(R.id.ivTitleVolunteerAvatar2);
        Util.loadGifIntoImageView(this, ivTitleSeniorAvatar1, R.raw.title_senior_avatar1);
        Util.loadGifIntoImageView(this, ivTitleSeniorAvatar2, R.raw.title_senior_avatar2);
        Util.loadGifIntoImageView(this, ivTitleVolunteerAvatar1, R.raw.title_volunteer_avatar1);
        Util.loadGifIntoImageView(this, ivTitleVolunteerAvatar2, R.raw.title_volunteer_avatar2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_type_picker, menu);
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

    public void onSeniorClick(View v) {
        Intent i = new Intent(UserTypePickerActivity.this, FillOutProfileActivity.class);
        i.putExtra("isSenior", true);
        Pair<View, String> p1 = Pair.create((View)ivTitleSeniorAvatar1, "avatar1");
        Pair<View, String> p2 = Pair.create((View)ivTitleSeniorAvatar2, "avatar2");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, p1, p2);
        startActivity(i, options.toBundle());
    }

    public void onVolunteerClick(View v) {
        Intent i = new Intent(UserTypePickerActivity.this, FillOutProfileActivity.class);
        i.putExtra("isSenior", false);
        Pair<View, String> p1 = Pair.create((View)ivTitleVolunteerAvatar1, "avatar1");
        Pair<View, String> p2 = Pair.create((View)ivTitleVolunteerAvatar2, "avatar2");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, p1, p2);
        startActivity(i, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish(); // we actually don't care about the result, we just want this activity to finish when FillOutProfileActivity finishes.
    }
}
