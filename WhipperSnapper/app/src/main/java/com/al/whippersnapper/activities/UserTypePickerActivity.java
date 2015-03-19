package com.al.whippersnapper.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.al.whippersnapper.R;

public class UserTypePickerActivity extends ActionBarActivity {
    public static final int USER_TYPE_REQUEST_CODE = 42;
    private Typeface bikoTypeface;
    private TextView tvAppTitle;
    private Button btnIAmASenior;
    private Button btnIAmAVolunteer;

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
        startActivityForResult(i, USER_TYPE_REQUEST_CODE);
    }

    public void onVolunteerClick(View v) {
        Intent i = new Intent(UserTypePickerActivity.this, FillOutProfileActivity.class);
        i.putExtra("isSenior", false);
        startActivityForResult(i, USER_TYPE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish(); // we actually don't care about the result, we just want this activity to finish when FillOutProfileActivity finishes.
    }
}
