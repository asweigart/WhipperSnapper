package com.al.whippersnapper.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ParseChatRooms;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ShowTaskDetailsActivity extends ActionBarActivity {

    private ImageView ivTaskDetailsProfilePhoto;
    private TextView tvTaskDetailsSeniorName;
    private TextView tvTaskDetailsType;
    private TextView tvTaskDetailsDetails;
    private TextView tvTaskDetailsPostedOn;
    private String seniorUsername;

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
        seniorUsername = getIntent().getStringExtra("seniorUsername");
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
        ParseQuery<ParseWSUser> q = ParseQuery.getQuery("_User");
        q.whereEqualTo("username", seniorUsername);
        q.findInBackground(new FindCallback<ParseWSUser>() {
            @Override
            public void done(List<ParseWSUser> parseWSUsers, ParseException e) {
                byte[] thisUserPhotoBytes = null;
                byte[] otherUserPhotoBytes = null;

                if (e == null) {
                    ParseWSUser thisUser = (ParseWSUser) ParseUser.getCurrentUser();
                    ParseWSUser senior = parseWSUsers.get(0); // TODO this shouldn't possible fail, there should always be 1 senior unless the db is incoherent

                    // add chat room to the db
                    ParseChatRooms chatRoom = new ParseChatRooms();
                    chatRoom.setSeniorUsername(senior.getUsername());
                    chatRoom.setSeniorFullName(senior.getFullName());
                    chatRoom.setVolunteerUsername(thisUser.getUsername());
                    chatRoom.setVolunteerFullName(thisUser.getFullName());
                    try {
                        chatRoom.save();
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }

                    // send push to the senior
                    ParsePush push = new ParsePush();
                    push.setChannel(senior.getUsername());
                    push.setMessage(thisUser.getFullName() + getResources().getString(R.string.has_offered_to_do_this_task_for_you));
                    try {
                        push.send();
                    } catch (ParseException e3) {
                        e3.printStackTrace();
                    }

                    // load the profile photo bytes of both users
                    try {
                        thisUserPhotoBytes = thisUser.getPhoto().getData(); // assume that senior is "theUser", we'll swap it if that's not the case.
                        otherUserPhotoBytes = senior.getPhoto().getData();
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }

                    // start the chat activity
                    Intent i = new Intent(ShowTaskDetailsActivity.this, ChatActivity.class);
                    i.putExtra("otherUsername", senior.getUsername());
                    i.putExtra("otherUserFullName", Util.getAnonymizedName(senior.getFullName()));
                    i.putExtra("fromShowTaskDetailsActivity", true); // don't add the task summary chat message
                    i.putExtra("thisUserPhoto", thisUserPhotoBytes);
                    i.putExtra("otherUserPhoto", otherUserPhotoBytes);
                    startActivity(i);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
