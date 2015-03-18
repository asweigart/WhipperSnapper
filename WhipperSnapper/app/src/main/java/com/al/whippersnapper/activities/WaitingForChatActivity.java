package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ParseChatRooms;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingForChatActivity extends ActionBarActivity {
    private TextView tvWaitingTaskType;
    private TextView tvWaitingTaskDetails;
    private TextView tvWaitingTaskPostedOn;
    private ParseWSUser thisUser;
    private static boolean activityVisible;

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

        thisUser = (ParseWSUser) ParseWSUser.getCurrentUser();
        final ParseWSUser finalUser = thisUser; // used so we can reference this in the callback

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        if (!activityVisible) {
                            return; // don't check if the activity isn't visible
                        }

                        //Toast.makeText(WaitingForChatActivity.this, "Checking for chat", Toast.LENGTH_SHORT).show();

                        ParseQuery<ParseChatRooms> q = ParseQuery.getQuery("ChatRooms");
                        q.whereEqualTo("SeniorUsername", thisUser.getUsername());
                        q.findInBackground(new FindCallback<ParseChatRooms>() {
                            @Override
                            public void done(List<ParseChatRooms> parseChatRoomses, ParseException e) {
                                byte[] thisUserPhotoBytes = null;
                                byte[] otherUserPhotoBytes = null;
                                Intent i = null;
                                boolean inChatRoom = parseChatRoomses.size() > 0; // TODO - this should only ever be 0 or 1

                                // go to the ChatActivity
                                if (inChatRoom) {
                                    // load the profile photo bytes of both users
                                    try {
                                        ParseQuery<ParseWSUser> q2 = ParseQuery.getQuery("User");
                                        q2.whereEqualTo("username", parseChatRoomses.get(0).getVolunteerUsername());
                                        List<ParseWSUser> results = q2.find();

                                        thisUserPhotoBytes = finalUser.getPhoto().getData(); // assume that senior is "theUser", we'll swap it if that's not the case.
                                        otherUserPhotoBytes = results.get(0).getPhoto().getData();
                                    } catch (ParseException e2) {
                                        e2.printStackTrace();
                                    }

                                    // launch ChatActivity
                                    i = new Intent(WaitingForChatActivity.this, ChatActivity.class);
                                    i.putExtra("otherUsername", parseChatRoomses.get(0).getVolunteerUsername());
                                    i.putExtra("otherUserFullName", parseChatRoomses.get(0).getVolunteerFullName());
                                    i.putExtra("fromShowTaskDetailsActivity", false); // don't add the task summary chat message
                                    i.putExtra("thisUserPhoto", thisUserPhotoBytes);
                                    i.putExtra("otherUserPhoto", otherUserPhotoBytes);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
                                    startActivity(i);
                                }
                            }
                        });


                    }
                });
            }
        };
        timer.schedule(timerTask, 5000, 5000); // TODO - this will keep running even when the app is not focused!
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityVisible = false;
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
        theUser.setTaskDetails(JSONObject.NULL);
        theUser.setTaskLat(JSONObject.NULL);
        theUser.setTaskLng(JSONObject.NULL);
        theUser.setTaskType(JSONObject.NULL);
        theUser.setTaskPhoto(JSONObject.NULL);*/ // Instead of setting every column to null, just set TaskType to null
        theUser.setTaskType("");

        // post an SENIOR_CANCEL and END message TODO - actually, this is an edge case and I'll leave it unfixed for now.
        // TODO - also, if the user has cancel without posting the END message, if this volunteer does a task for the senior again, the old chat messages will appear. Could fix this by adding a random task id to the ParseWSUser table and the chat room name.

        // delete a chat room (in the unlikely event it exists)
        final ParseWSUser finalUser = theUser;
        ParseQuery<ParseChatRooms> q = new ParseQuery("ChatRooms");
        q.whereEqualTo("SeniorUsername", thisUser.getUsername());
        q.findInBackground(new FindCallback<ParseChatRooms>() {
            @Override
            public void done(List<ParseChatRooms> parseChatRoomses, ParseException e) {
                if (parseChatRoomses.size() != 0) {
                    try {
                        parseChatRoomses.get(0).delete(); // delete a chat room (if there is one)
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }
                }

                // update deleting the task type
                try {
                    finalUser.save();
                } catch (ParseException e3) {
                    e3.printStackTrace();
                }

                Toast.makeText(WaitingForChatActivity.this, getResources().getString(R.string.Your_task_request_has_been_canceled), Toast.LENGTH_LONG).show();
                Intent i = new Intent(WaitingForChatActivity.this, SeniorHomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
                startActivity(i);
                finish();
                return;
            }
        });
    }
}
