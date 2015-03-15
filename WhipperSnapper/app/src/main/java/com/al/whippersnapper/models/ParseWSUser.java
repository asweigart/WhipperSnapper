package com.al.whippersnapper.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("_User")
public class ParseWSUser extends ParseUser {

    public ParseWSUser() {
        super();
    }

    public String getFullName() {
        return getString("FullName");
    }
    public void setFullName(String value) {
        put("FullName", value);
    }

    public String getPhone() {
        return getString("Phone");
    }
    public void setPhone(String value) {
        put("Phone", value);
    }

    public String getAddress() {
        return getString("Address");
    }
    public void setAddress(String value) {
        put("Address", value);
    }

    public String getCityStateZip() {
        return getString("CityStateZip");
    }
    public void setCityStateZip(String value) {
        put("CityStateZip", value);
    }

    public boolean getIsSenior() {
        return getBoolean("IsSenior");
    }
    public void setIsSenior(boolean value) {
        put("IsSenior", value);
    }

    public ParseFile getPhoto() { return getParseFile("Photo"); }
    public void setPhoto(ParseFile value) { put("Photo", value); }

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

    // Task-based columns:
    public Number getTaskLat() {
        return getNumber("TaskLat");
    }
    public void setTaskLat(Number value) {
        put("TaskLat", value);
    }

    public Number getTaskLng() {
        return getNumber("TaskLng");
    }
    public void setTaskLng(Number value) {
        put("TaskLng", value);
    }

    public String getTaskType() {
        return getString("TaskType");
    }
    public void setTaskType(String value) {
        put("TaskType", value);
    }

    public String getTaskDetails() {
        return getString("TaskDetails");
    }
    public void setTaskDetails(String value) {
        put("TaskDetails", value);
    }

    public boolean getTaskAvailable() {
        return getBoolean("TaskAvailable");
    }
    public void setTaskAvailable(boolean value) {
        put("TaskAvailable", value);
    }

    public String getTaskAddress() {
        return getString("TaskAddress");
    }
    public void setTaskAddress(String value) { put("TaskAddress", value); }

    public ParseFile getTaskPhoto() { return getParseFile("TaskPhoto"); }
    public void setTaskPhoto(ParseFile value) { put("TaskPhoto", value); }

}
