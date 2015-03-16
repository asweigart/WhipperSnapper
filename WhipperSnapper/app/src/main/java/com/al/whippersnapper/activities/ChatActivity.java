package com.al.whippersnapper.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ParseWSUser;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ChatActivity extends ActionBarActivity {
    private ParseWSUser otherUser;
    private TextView tvOtherUserName;
    private Button btnAccept;
    private Button btnDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        btnAccept = (Button) findViewById(R.id.btnAcceptOffer);
        btnDecline = (Button) findViewById(R.id.btnDeclineOffer);
        tvOtherUserName = (TextView) findViewById(R.id.tvOtherUserName);

        ParseWSUser thisUser = (ParseWSUser) ParseUser.getCurrentUser();

        // hide the accept button if this is the volunteer
        if (!thisUser.getIsSenior()) {
            btnAccept.setVisibility(View.GONE);
        }

        String otherUsername = getIntent().getStringExtra("otherUsername");
        String otherUserFullName = getIntent().getStringExtra("otherUserFullName");

        tvOtherUserName.setText(getResources().getString(R.string.You_are_chatting_with) + otherUserFullName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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


    public void onSendClick(View v) {

    }

    public void onDeclineOfferClick(View v) {

    }

    public void onAcceptOfferClick(View v) {

    }
}
