package com.al.whippersnapper.applications;


import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.al.whippersnapper.models.ParseWSUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.SaveCallback;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(ParseWSUser.class);

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "h87Y27Wi43OUVGkYzUp8qEoJGxfrTgSYk5rMoQbN", "nwHzNf7lytl5FGkwm5DQtyXisBoCCnEnuEvYVuM2");

        // obtain the phone number from the device
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String channelName = "c" +  tMgr.getLine1Number();

        ParsePush.subscribeInBackground(channelName, new SaveCallback() { // channel must start with a letter, so I chose "c"
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("XXXXXXXXcom.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("XXXXXXXXcom.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
