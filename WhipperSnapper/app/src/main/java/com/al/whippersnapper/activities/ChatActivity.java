package com.al.whippersnapper.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

    private String chatRoomName; // name of the chat channel, made from the two usernames.
    private Pubnub pubnub;
    private ListView lvChat;
    private ArrayList<ChatMessage> mMessages;
    private ChatListAdapter mAdapter;
    private ParseWSUser thisUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        btnAccept = (Button) findViewById(R.id.btnAcceptOffer);
        btnDecline = (Button) findViewById(R.id.btnDeclineOffer);
        tvOtherUserName = (TextView) findViewById(R.id.tvOtherUserName);
        etMessageToSend = (EditText) findViewById(R.id.etMessageToSend);

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
        mAdapter = new ChatListAdapter(ChatActivity.this, thisUser.getUsername(), mMessages);
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
                String username;
                String text;
                username = msg.substring(0, msg.indexOf(Util.CHAT_ROOM_SEPARATOR));
                text = msg.substring(msg.indexOf(Util.CHAT_ROOM_SEPARATOR) + Util.CHAT_ROOM_SEPARATOR.length());

                mMessages.add(new ChatMessage(username, text));
                mAdapter.notifyDataSetChanged();
                lvChat.invalidate(); // redraw listview
            }
        });
    }


    public void onDeclineOfferClick(View v) {
        if (thisUser.getIsSenior()) {
            endChat(SENIOR_DECLINE);
        } else {
            endChat(VOLUNTEER_DECLINE);
        }
    }

    public void onAcceptOfferClick(View v) {
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
                pubnub.publish(chatRoomName, END, new Callback() {
                    @Override
                    public void successCallback(String channel, Object response2) {
                        super.successCallback(channel, response2);
                        Log.e("XXXXXXXXXXXX",response2.toString());

                        // delete the chat room
                        deleteChatRoom(seniorUsername, volunteerUsername);

                        // if this is an accept, also delete the task
                        if (messageType.equals(SENIOR_ACCEPT)) {
                            thisUser.setTaskType(""); // setting task type to blank is enough to "delete" it.
                            thisUser.saveInBackground(); // nothing else needs to be done after this, so we don't need a callback here.
                        }

                    }
                });
            }
        });
        finish(); // TODO - test that this goes back to SeniorHome/FindTask activity
    }

    public void deleteChatRoom(String seniorUsername, String volunteerUsername) {
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
            if (chatRoom.getSeniorUsername() == seniorUsername) {
                try {
                    chatRoom.delete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break; // technically, there should only ever be at most one chat room in the db at a time, so we can break here.
            }
        }
    }
}
