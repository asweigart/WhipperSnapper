package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class WaitingForChatActivity extends ActionBarActivity {
    private TextView tvWaitingTaskType;
    private TextView tvWaitingTaskDetails;
    private TextView tvWaitingTaskPostedOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_chat);

        tvWaitingTaskType = (TextView) findViewById(R.id.tvWaitingTaskType);
        tvWaitingTaskDetails = (TextView) findViewById(R.id.tvWaitingTaskDetails);
        tvWaitingTaskPostedOn = (TextView) findViewById(R.id.tvWaitingTaskPostedOn);

        // load task details into ui from the intent
        tvWaitingTaskType.setText(getIntent().getStringExtra("taskType"));
        tvWaitingTaskDetails.setText(getIntent().getStringExtra("taskDetails"));
        tvWaitingTaskPostedOn.setText(Util.getRelativeTimeAgo(((Date)getIntent().getSerializableExtra("postedOn")).toString()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_waiting_for_chat, menu);
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

    public void onCancelTaskClick(View v) {
        ParseWSUser theUser = (ParseWSUser) ParseWSUser.getCurrentUser();
        /*theUser.setTaskAddress(JSONObject.NULL);
        theUser.setTaskAvailable(JSONObject.NULL);
        theUser.setTaskDetails(JSONObject.NULL);
        theUser.setTaskLat(JSONObject.NULL);
        theUser.setTaskLng(JSONObject.NULL);
        theUser.setTaskType(JSONObject.NULL);
        theUser.setTaskPhoto(JSONObject.NULL);*/ // Instead of setting every column to null, just set TaskType to null and TaskAvailable to false
        theUser.setTaskType("");
        theUser.setTaskAvailable(false);

        theUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(WaitingForChatActivity.this, getResources().getString(R.string.Your_task_request_has_been_canceled), Toast.LENGTH_LONG).show();

                Intent i = new Intent(WaitingForChatActivity.this, SeniorHomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
                startActivity(i);
                finish();
            }
        });
    }
}
