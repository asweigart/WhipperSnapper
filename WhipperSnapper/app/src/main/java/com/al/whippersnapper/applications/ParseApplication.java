package com.al.whippersnapper.applications;


import android.app.Application;

import com.al.whippersnapper.models.ParseTask;
import com.al.whippersnapper.models.ParseWSUser;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(ParseWSUser.class);
        ParseObject.registerSubclass(ParseTask.class);

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "h87Y27Wi43OUVGkYzUp8qEoJGxfrTgSYk5rMoQbN", "nwHzNf7lytl5FGkwm5DQtyXisBoCCnEnuEvYVuM2");
    }
}
