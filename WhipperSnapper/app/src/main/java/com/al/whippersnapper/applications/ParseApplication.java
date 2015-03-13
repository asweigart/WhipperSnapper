package com.al.whippersnapper.applications;


import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "h87Y27Wi43OUVGkYzUp8qEoJGxfrTgSYk5rMoQbN", "nwHzNf7lytl5FGkwm5DQtyXisBoCCnEnuEvYVuM2");

    }
}
