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
}
