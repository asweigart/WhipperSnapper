package com.al.whippersnapper.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("ChatRooms")
public class ParseChatRooms extends ParseObject {
    public ParseChatRooms() { super(); }


    public String getSeniorUsername() {
        return getString("SeniorUsername");
    }
    public void setSeniorUsername(String value) {
        put("SeniorUsername", value);
    }

    public String getVolunteerUsername() {
        return getString("VolunteerUsername");
    }
    public void setVolunteerUsername(String value) {
        put("VolunteerUsername", value);
    }

    public String getSeniorFullName() {
        return getString("SeniorFullName");
    }
    public void setSeniorFullName(String value) { put("SeniorFullName", value); }

    public String getVolunteerFullName() {
        return getString("VolunteerFullName");
    }
    public void setVolunteerFullName(String value) { put("VolunteerFullName", value);}
}
