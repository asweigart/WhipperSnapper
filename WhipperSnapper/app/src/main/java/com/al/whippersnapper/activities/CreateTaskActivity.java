package com.al.whippersnapper.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.al.whippersnapper.R;
import com.al.whippersnapper.adapters.CreateTaskFragmentPagerAdapter;
import com.al.whippersnapper.fragments.CreateTaskDetailsFragment;
import com.al.whippersnapper.fragments.CreateTaskLocationFragment;
import com.astuetz.PagerSlidingTabStrip;

import java.io.ByteArrayOutputStream;

public class CreateTaskActivity extends FragmentActivity implements
        CreateTaskDetailsFragment.OnFragmentInteractionListener,
        CreateTaskLocationFragment.OnFragmentInteractionListener,
        CreateTaskDetailsFragment.onTaskPhotoClickListener {

    private byte[] photoBytes;
    private Uri mCapturedImageURI;
    private CreateTaskFragmentPagerAdapter pagerAdapter;

    public static final int TAKE_TASK_PHOTO = 2000;
    public static final int TASK_PHOTO_MAX_WIDTH = 500;
    public static final int TASK_PHOTO_MAX_HEIGHT = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new CreateTaskFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_task, menu);
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
    public void onFragmentInteraction(Uri uri) {

    }

    public void onTaskPhotoClick() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, TAKE_TASK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        if (requestCode == TAKE_TASK_PHOTO && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        }

        ImageView ivTaskPhoto = pagerAdapter.getTaskDetailsFragment().getIvTaskPhoto(); // Did I mention that I don't like Java?
        Log.e("XXXXXXXXXX", "Original size: " + photo.getWidth() + " " + photo.getHeight());
        if (photo != null) {
            // Scale the photo
            //Matrix m = new Matrix();
            //m.setRectToRect(new RectF(0, 0, photo.getWidth(), photo.getHeight()), new RectF(0, 0, TASK_PHOTO_MAX_WIDTH, TASK_PHOTO_MAX_HEIGHT), Matrix.ScaleToFit.CENTER);
            //photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), m, true);

            //Log.e("XXXXXXXXXX", "Scaled size: " + photo.getWidth() + " " + photo.getHeight());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            photoBytes = stream.toByteArray();
            ivTaskPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        }

    }

}
