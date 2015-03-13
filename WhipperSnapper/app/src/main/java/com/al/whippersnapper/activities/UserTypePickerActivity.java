package com.al.whippersnapper.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.al.whippersnapper.R;

public class UserTypePickerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_picker);
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
        i.putExtra("userType", "senior");
        startActivity(i);
    }

    public void onVolunteerClick(View v) {
        Intent i = new Intent(UserTypePickerActivity.this, FillOutProfileActivity.class);
        i.putExtra("userType", "volunteer");
        startActivity(i);
    }
}
