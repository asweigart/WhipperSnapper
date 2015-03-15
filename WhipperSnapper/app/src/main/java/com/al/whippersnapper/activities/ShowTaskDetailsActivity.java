package com.al.whippersnapper.activities;

import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.al.whippersnapper.R;

public class ShowTaskDetailsActivity extends ActionBarActivity {

    private ImageView ivTaskDetailsProfilePhoto;
    private TextView tvTaskDetailsSeniorName;
    private TextView tvTaskDetailsType;
    private TextView tvTaskDetailsDetails;
    private TextView tvTaskDetailsPostedOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task_details);

        ivTaskDetailsProfilePhoto = (ImageView) findViewById(R.id.ivTaskDetailsProfilePhoto);
        tvTaskDetailsSeniorName = (TextView) findViewById(R.id.tvTaskDetailsSeniorName);
        tvTaskDetailsType = (TextView) findViewById(R.id.tvTaskDetailsType);
        tvTaskDetailsDetails = (TextView) findViewById(R.id.tvTaskDetailsDetails);
        tvTaskDetailsPostedOn = (TextView) findViewById(R.id.tvTaskDetailsPostedOn);

        // get the task info from the intent
        byte[] taskPhotoBytes = getIntent().getByteArrayExtra("taskPhoto");
        if (taskPhotoBytes != null) {
            ivTaskDetailsProfilePhoto.setImageBitmap(BitmapFactory.decodeByteArray(taskPhotoBytes, 0, taskPhotoBytes.length));
        }
        tvTaskDetailsSeniorName.setText(getIntent().getStringExtra("seniorName"));
        tvTaskDetailsType.setText(getIntent().getStringExtra("taskType"));
        tvTaskDetailsDetails.setText(getIntent().getStringExtra("taskDetails"));
        tvTaskDetailsPostedOn.setText(getIntent().getStringExtra("postedOn"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_task_details, menu);
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

    public void onOfferClick(View v) {
        // TODO
    }
}
