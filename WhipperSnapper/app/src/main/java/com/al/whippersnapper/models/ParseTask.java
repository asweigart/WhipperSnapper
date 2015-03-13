package com.al.whippersnapper.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Task")
public class ParseTask extends ParseObject {

    public ParseTask() {
        super();
    }

    public Number getTaskType() {
        return getNumber("TaskType");
    }
    public void setTaskType(Number value) {
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
}
