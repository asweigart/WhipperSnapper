package com.al.whippersnapper.applications;


import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.al.whippersnapper.activities.MainActivity;
import com.al.whippersnapper.models.ParseChatRooms;
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
        ParseObject.registerSubclass(ParseChatRooms.class);

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "h87Y27Wi43OUVGkYzUp8qEoJGxfrTgSYk5rMoQbN", "nwHzNf7lytl5FGkwm5DQtyXisBoCCnEnuEvYVuM2");

        // obtain the phone number from the device
        final String channelName = "c" +  getThisDevicePhoneNumber();

        // unsubscribe first, because if we uninstall and reinstall the app, the device will receive 2 pushes and show 2 system notifications
        ParsePush.unsubscribeInBackground(channelName, new SaveCallback() {
            @Override
            public void done(ParseException e1) {
                if (e1 == null) {
                    ParsePush.subscribeInBackground(channelName, new SaveCallback() { // channel must start with a letter, so I chose "c"
                        @Override
                        public void done(ParseException e2) {
                            if (e2 == null) {
                                Log.e("XXXXXXXXcom.parse.push", "successfully subscribed to the broadcast channel.");
                            } else {
                                Log.e("XXXXXXXXcom.parse.push", "failed to subscribe for push", e2);
                            }
                        }
                    });
                } else {
                    e1.printStackTrace();
                }

            }
        });
    }

    // TODO used for debugging, and apparently I have to copy/paste this function into every
    // Activity class I want to use it from since getApplicationContext() can't be called from
    // a static method.
    public String getThisDevicePhoneNumber() {
        if (MainActivity.DEBUG_USE_REAL_PHONE_NUMBER) {
            TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            return tMgr.getLine1Number();
        } else {
            return MainActivity.DEBUG_FAKE_PHONE_NUMBER;
        }
    }
}
