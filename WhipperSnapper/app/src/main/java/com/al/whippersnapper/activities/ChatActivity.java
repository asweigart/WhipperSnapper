package com.al.whippersnapper.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.adapters.ChatListAdapter;
import com.al.whippersnapper.models.ChatMessage;
import com.al.whippersnapper.models.ParseChatRooms;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends ActionBarActivity {
    // messages to send for chat events
    public final static String SENIOR_DECLINE = "SENIOR_DECLINE";
    public final static String SENIOR_ACCEPT = "SENIOR_ACCEPT";
    public final static String VOLUNTEER_DECLINE = "VOLUNTEER_DECLINE";
    public final static String END = "END"; // needed because pubnub can't clear history for a channel

    private ParseWSUser otherUser;
    private TextView tvOtherUserName;
    private Button btnAccept;
    private Button btnDecline;
    private EditText etMessageToSend;
    private Button btnSend;

    private String chatRoomName; // name of the chat channel, made from the two usernames.
    private Pubnub pubnub;
    private ListView lvChat;
    private ArrayList<ChatMessage> mMessages;
    private ChatListAdapter mAdapter;
    private ParseWSUser thisUser;

    private String taskAddress = null;

    private byte[] thisUserProfilePhoto;
    private byte[] otherUserProfilePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        btnAccept = (Button) findViewById(R.id.btnAcceptOffer);
        btnDecline = (Button) findViewById(R.id.btnDeclineOffer);
        tvOtherUserName = (TextView) findViewById(R.id.tvOtherUserName);
        etMessageToSend = (EditText) findViewById(R.id.etMessageToSend);
        btnSend = (Button) findViewById(R.id.btnSend);

        thisUser = (ParseWSUser) ParseUser.getCurrentUser();

        String otherUsername = getIntent().getStringExtra("otherUsername");
        String otherUserFullName = getIntent().getStringExtra("otherUserFullName");
        boolean fromShowTaskDetailsActivity = getIntent().getBooleanExtra("fromShowTaskDetailsActivity", false);


        // hide the accept button if this is the volunteer
        if (!thisUser.getIsSenior()) {
            btnAccept.setVisibility(View.GONE);
            chatRoomName = thisUser.getUsername() + Util.CHAT_ROOM_SEPARATOR + otherUsername; // senior name goes first
        } else {
            chatRoomName = otherUsername + Util.CHAT_ROOM_SEPARATOR + thisUser.getUsername(); // senior name goes first
        }

        tvOtherUserName.setText(getResources().getString(R.string.You_are_chatting_with) + otherUserFullName);

        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<ChatMessage>();
        mAdapter = new ChatListAdapter(ChatActivity.this, thisUser.getUsername(), mMessages, getIntent().getByteArrayExtra("thisUserPhoto"), getIntent().getByteArrayExtra("otherUserPhoto"));
        lvChat.setAdapter(mAdapter);




        pubnub = new Pubnub("pub-c-b441d296-3edc-4025-b178-97c45e8f92aa", "sub-c-e2a329fa-cc25-11e4-8a92-02ee2ddab7fe");
        subscribeToChannel();
        if (fromShowTaskDetailsActivity) {
            // the volunteer has offered to help
        }
        refreshAndPopulateFromHistory();
    }

    /*
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
    */





    public void subscribeToChannel() {
        // Subscribe to the channel (i.e. join the chat room)
        try {
            pubnub.subscribe(chatRoomName, new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            addChatMessage((String)message);
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    public void onSendClick(View v) {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.e("XXXXXXXXXXXX",response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                Log.e("XXXXXXXXXXXX",error.toString());
            }
        };
        pubnub.publish(this.chatRoomName, thisUser.getUsername() + Util.CHAT_ROOM_SEPARATOR + etMessageToSend.getText().toString() , callback);
        etMessageToSend.setText(""); // blank out text field
    }

    public void refreshAndPopulateFromHistory() {

        // NOTE: PubNub does not support clearing the history. To work around this, we'll post a
        // message that simply says "END". When populating from the history, we start at the end
        // and search for this. If END is found, we ignore all the previous messages.
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                super.successCallback(channel, response);
                Log.e("XXXXXXXXXXXX", "SUCCESS HISTORY " + response.toString());
                try {
                    // search for an "END" message, starting from the end.
                    int endIndex = -1; // -1 means there is no END message
                    for (int i = ((JSONArray) response).getJSONArray(0).length() - 1; i >= 0; i--) {
                        if (((JSONArray) response).getJSONArray(0).getString(i).equals("END")) {
                            endIndex = i;
                            break;
                        }
                    }
                    if (endIndex == -1) {
                        endIndex = 0; // if no "END" message was found, start at the first message like normal
                    }

                    // Display the task details at the top of the chat room
                    ParseChatRooms chatRoom = getChatRoomAUserIsIn(thisUser.getUsername(), thisUser.getUsername()); // works if this user is either senior or volunteer
                    try {
                        ParseQuery<ParseWSUser> q = ParseQuery.getQuery("_User");
                        q.whereEqualTo("username", chatRoom.getSeniorUsername());
                        List<ParseWSUser> results = q.find();
                        taskAddress = results.get(0).getTaskAddress();
                        if (taskAddress == null) {
                            taskAddress = results.get(0).getAddress(); // if there is no task address, the task's address is the senior's address.
                        }
                        if (taskAddress == null) {
                            // if there is still no address, use the latlng of the user.
                            taskAddress = "Lat " + results.get(0).getLat() + " Lon " + results.get(0).getLng(); // TODO - this is a really ugly failure case
                        }
                        String messageText = results.get(0).getTaskType() + "\n" + results.get(0).getTaskDetails();
                        mMessages.add(new ChatMessage("", messageText));
                        mAdapter.notifyDataSetChanged();

                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }

                    // add the chat messages from the history
                    for (int i = endIndex; i < ((JSONArray) response).getJSONArray(0).length(); i++) {
                        String message = ((JSONArray) response).getJSONArray(0).getString(i);
                        addChatMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            public void errorCallback(String channel, PubnubError error) {
                Log.e("XXXXXXXXXXXX", "ERROR HISTORY " + error.toString());
            }
        };

        mMessages.clear();
        pubnub.history(chatRoomName, 100, callback);
    }

    public void addChatMessage(String message) {
        final String msg = message;

        // the list view can only be updated from the UI thread, so put this code in a Runnable.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String username = null;
                String text = null;

                if ( msg.equals(END)) {
                    return; // do nothing
                } else if (msg.equals(VOLUNTEER_DECLINE)) {
                    username = "";
                    if (thisUser.getIsSenior()) {
                        text = getResources().getString(R.string.The_other_person_has_left_the_chat_room);
                    } else {
                        text = getResources().getString(R.string.You_have_declined);
                    }
                    disableChatButtons();
                } else if (msg.equals(SENIOR_DECLINE)) {
                    username = "";
                    if (!thisUser.getIsSenior()) {
                        text = getResources().getString(R.string.The_other_person_has_left_the_chat_room);
                    } else {
                        text = getResources().getString(R.string.You_have_declined);
                    }
                    disableChatButtons();
                } else if (msg.equals(SENIOR_ACCEPT)) {
                    username = "";
                    text = getResources().getString(R.string.The_help_offer_for_this_task_has_been_accepted);
                    disableChatButtons();
                } else if (msg.indexOf(Util.CHAT_ROOM_SEPARATOR) != -1) {
                    username = msg.substring(0, msg.indexOf(Util.CHAT_ROOM_SEPARATOR));
                    text = msg.substring(msg.indexOf(Util.CHAT_ROOM_SEPARATOR) + Util.CHAT_ROOM_SEPARATOR.length());
                } else {
                    username = "";
                    text = msg;
                }

                mMessages.add(new ChatMessage(username, text));
                mAdapter.notifyDataSetChanged();
                lvChat.invalidate(); // redraw listview
            }
        });
    }


    public void disableChatButtons() {
        btnSend.setEnabled(false);
        btnAccept.setEnabled(false);
        btnDecline.setEnabled(false);
    }


    public void onDeclineOfferClick(View v) {
        if (!(etMessageToSend.getText().toString().equals(""))) {
            onSendClick(null); // send the last message (onSendClick doesn't use the view argument so we can pass null here)
        }
        if (thisUser.getIsSenior()) {
            endChat(SENIOR_DECLINE);
        } else {
            endChat(VOLUNTEER_DECLINE);
        }
    }

    public void onAcceptOfferClick(View v) {
        if (!(etMessageToSend.getText().toString().equals(""))) {
            onSendClick(null); // send the last message (onSendClick doesn't use the view argument so we can pass null here)
        }
        endChat(SENIOR_ACCEPT); // only seniors should have access to an accept button
    }

    public void endChat(final String messageType) {
        final String seniorUsername; // uhg. to avoid having to do a lookup in the db, let's just pass whatever username we have tp  deleteChatRoom()
        final String volunteerUsername;
        if (thisUser.getIsSenior()) {
            seniorUsername = thisUser.getUsername();
            volunteerUsername = "";
        } else {
            seniorUsername = "";
            volunteerUsername = thisUser.getUsername();
        }
        pubnub.publish(chatRoomName, messageType, new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                super.successCallback(channel, response);
                Log.e("XXXXXXXXXXXX", response.toString());

                // if the senior has accepted, display the accept message then the end message
                if (messageType.equals(SENIOR_ACCEPT)) {
                    // give the address for the task in the chat
                    pubnub.publish(chatRoomName, getResources().getString(R.string.The_address_for_this_task_is) + " " + taskAddress, new Callback() {
                        @Override
                        public void successCallback(String channel3, Object response3) {
                            super.successCallback(channel3, response3);
                            sendEndSystemChatMessage(seniorUsername, volunteerUsername, messageType);
                        }
                    });
                } else {
                    // if senior hasn't accept, just display the end message
                    sendEndSystemChatMessage(seniorUsername, volunteerUsername, messageType);
                }
            }
        });
    }

    public void sendEndSystemChatMessage(final String seniorUsername, final String volunteerUsername, final String messageType) {
        pubnub.publish(chatRoomName, END, new Callback() {
            @Override
            public void successCallback(String channel2, Object response2) {
                super.successCallback(channel2, response2);
                Log.e("XXXXXXXXXXXX",response2.toString());

                // delete the chat room
                deleteChatRoom(seniorUsername, volunteerUsername);

                // if the offer to do the task has been accepted...
                if (messageType.equals(SENIOR_ACCEPT)) {
                    // give the address for the task in the chat
                    //pubnub.publish(chatRoomName, getResources().getString(R.string.The_address_for_this_task_is) + taskAddress, new Callback() {});

                    thisUser.setTaskType(""); // setting task type to blank is enough to "delete" it.

                    try {
                        thisUser.save(); // nothing else needs to be done after this, so we don't need a callback here.
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //finish(); // actually, don't quit this activity, let them navigate away from it when they are done.

            }
        });
    }

    public void deleteChatRoom(String seniorUsername, String volunteerUsername) {
        ParseChatRooms chatRoom = getChatRoomAUserIsIn(seniorUsername, volunteerUsername);
        try {
            chatRoom.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ParseChatRooms getChatRoomAUserIsIn(String seniorUsername, String volunteerUsername) {
        // one of seniorUsername and volunteerUsername will be blank. This is fine since we only need one since a user can only be in one chat room at a time.
        ParseQuery<ParseChatRooms> q = ParseQuery.getQuery("ChatRooms");
        List<ParseChatRooms> results = null;
        try {
            results = q.find(); // this is only called from background threads already, so no need to do in background
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseChatRooms chatRoom = null;
        for (int i = 0; i < results.size(); i++) {
            chatRoom = results.get(i);
            if (chatRoom.getSeniorUsername().equals(seniorUsername) || chatRoom.getVolunteerUsername().equals(volunteerUsername)) {
                return chatRoom; // technically, there should only ever be at most one chat room in the db at a time, so we can break here.
            }
        }
        return null; // this should never happen
    }

    public void onBackPressed() {
        // when back is clicked to close the chat, renavigate to the main activity to be routed correctly.
        // TODO - unless chat hasn't ended, in which case, offer to close chat? Also, don't we only have to do this for seniors?
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
        startActivity(i);
    }

    public void onCallClick() {

    }
}
