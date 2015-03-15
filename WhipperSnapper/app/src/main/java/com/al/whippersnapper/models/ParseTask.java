package com.al.whippersnapper.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Task")
public class ParseTask extends ParseObject {

    public ParseTask() {
        super();
    }

    public String getSeniorUsername() {
        return getString("SeniorUsername");
    }
    public void setSeniorUsername(String value) {
        put("SeniorUsername", value);
    }

    public String getTaskType() {
        return getString("TaskType");
    }
    public void setTaskType(String value) {
        put("TaskType", value);
    }

    public String getDetails() {
        return getString("Details");
    }
    public void setDetails(String value) {
        put("Details", value);
    }

    public Number getLat() {
        return getNumber("Lat");
    }
    public void setLat(Number value) {
        put("Lat", value);
    }

    public Number getLng() {
        return getNumber("Lng");
    }
    public void setLng(Number value) {
        put("Lng", value);
    }

    public boolean getAvailable() {
        return getBoolean("Available");
    }
    public void setAvailable(boolean value) {
        put("Available", value);
    }


    // This column is totally redundant normalization, because this info could come from the owner's address. But it makes it easier to code, so there.
    // Note that, unlike the ParseWSUser's address, this will have the address AND city/state/zip info in it.
    public String getAddress() {
        return getString("Address");
    }
    public void setAddress(String value) { put("Address", value); }

    // Also redundant. In fact, since seniors can only have one task up at a time, there's no reason users and task should be separate tables.
    public String getSeniorFullName() { return getString("SeniorFullName"); }
    public void setSeniorFullName(String value) {
        put("SeniorFullName", value);
    }
}
