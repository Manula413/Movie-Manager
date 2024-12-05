package com.manula413.movie_manager.util;

public class Session {
    private static Session instance;
    private String username;
    private String userId;
    private String displayName;

    // Private constructor
    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userID) {
        this.userId = userID;

    }

    public void clearSession() {
        this.username = null;
        this.userId = null; // Clear userId as well
    }


}

