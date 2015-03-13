package com.al.whippersnapper.models;

public class ChatMessage {
    private String user;
    private String message;

    public ChatMessage(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
