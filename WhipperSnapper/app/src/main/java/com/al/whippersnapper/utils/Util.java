package com.al.whippersnapper.utils;

public final class Util {
    public static String getAnonymizedName(String fullName) {
        int firstSpace = fullName.indexOf(" ");
        if ((firstSpace == -1) || (fullName.length() == firstSpace + 1)) {
            return fullName; // Name doesn't have last name yet
        } else {
            return fullName.substring(0, firstSpace + 2) + ".";
        }
    }
}
