package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.al.whippersnapper.R;
import com.al.whippersnapper.applications.ParseApplication;
import com.al.whippersnapper.models.ParseWSUser;
import com.parse.ParseException;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity {
    public static int INVALID_LOGIN_CREDENTIAL = 101; // Odd. This constant doesn't seem to exist in Parse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean creatingProfile = false;

        ParseWSUser thisUser = new ParseWSUser();
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            thisUser.logIn(tMgr.getLine1Number(), "password");
            thisUser.fetchIfNeeded(); // TODO this doesn't work?
        } catch (ParseException e) {
            if (e.getCode() == INVALID_LOGIN_CREDENTIAL) {
                // bad login means the account does not exist, so launch the profile creation activity
                Intent i = new Intent(MainActivity.this, UserTypePickerActivity.class);
                startActivity(i);
                creatingProfile = true;
            } else {
                // Something actually went wrong.
                // TODO - do airplane mode test here
                e.printStackTrace();
            }
        }

        if (!creatingProfile) {
            // logged in, so go to the appropriate senior/volunteer activity
            if (thisUser.getIsSenior()) {
                // seniors go to the senior home activity
                Intent i = new Intent(MainActivity.this, SeniorHomeActivity.class);
                startActivity(i);
            } else {
                // volunteers go to the Find Task activity
                Intent i = new Intent(MainActivity.this, FindTaskActivity.class);
                startActivity(i);
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
