package com.al.whippersnapper.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Util {
    public static String CHAT_ROOM_SEPARATOR = "|||";

    public static String getAnonymizedName(String fullName) {
        int firstSpace = fullName.indexOf(" ");
        if ((firstSpace == -1) || (fullName.length() == firstSpace + 1)) {
            return fullName; // Name doesn't have last name yet
        } else {
            return fullName.substring(0, firstSpace + 2) + ".";
        }
    }


    // from https://gist.github.com/nesquena/f786232f5ef72f6e10a7
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}

