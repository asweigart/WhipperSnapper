package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ParseTask;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class WaitingForChatActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_chat);
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
        ParseQuery<ParseTask> q = ParseQuery.getQuery("Task");

        // TODO - kind of a hack, but we'll just use the phone number as the username value here.
        // obtain the phone number from the device
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String userPhoneNumber = tMgr.getLine1Number();

        q.whereContains("SeniorUsername", userPhoneNumber);
        List<ParseTask> results = null;
        try {
            results = q.find();
            ParseTask theTask = results.get(0); // this should always exist
            theTask.deleteEventually();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, getResources().getString(R.string.Your_task_request_has_been_canceled), Toast.LENGTH_LONG).show();

        Intent i = new Intent(this, SeniorHomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
        startActivity(i);
        finish();
    }
}
