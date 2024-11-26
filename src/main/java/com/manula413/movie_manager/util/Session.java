package com.manula413.movie_manager.util;

public class Session {
    private static Session instance;
    private String username;

    // Private constructor
    private Session() {}

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

    public void clearSession() {
        this.username = null;
    }
}

