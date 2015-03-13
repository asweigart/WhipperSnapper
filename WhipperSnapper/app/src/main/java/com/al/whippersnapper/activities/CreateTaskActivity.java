package com.al.whippersnapper.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.al.whippersnapper.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreateTaskActivity extends ActionBarActivity {
    private Spinner spTaskType;
    private EditText etTaskDetails;
    private ImageView ivTaskPhoto;
    private byte[] photoBytes;

    public static final int TAKE_TASK_PHOTO = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        spTaskType = (Spinner) findViewById(R.id.spTaskType);
        etTaskDetails = (EditText) findViewById(R.id.etTaskDetails);
        ivTaskPhoto = (ImageView) findViewById(R.id.ivTaskPhoto);
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

    public void onTaskPhotoClick(View v) {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, TAKE_TASK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        if (requestCode == TAKE_TASK_PHOTO && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        }

        if (photo != null) {
            // depending on if the photo is wider or taller, scale it down proportionally to the ImageView
            if (photo.getWidth() > photo.getHeight()) {
                photo = Bitmap.createScaledBitmap(photo, ivTaskPhoto.getWidth(), (int)(ivTaskPhoto.getHeight() * ((float)(photo.getHeight()) / (float)(photo.getWidth()))), true);
            } else {
                photo = Bitmap.createScaledBitmap(photo, (int)(ivTaskPhoto.getWidth() * ((float)(photo.getWidth()) / (float)(photo.getHeight()))), ivTaskPhoto.getHeight(), true);
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            photoBytes = stream.toByteArray();
            //Log.e("XXXXXXXXXXX", String.valueOf(photoBytes.length)); // confirmed that called createScaledBitmap signifcantly reduces the size.
            ivTaskPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        }
    }
}
