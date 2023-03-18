package com.github.splendor_mobile_game.model;

public class User {

    private final String name;

    private long sessionId;

    public User(String name, long sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }


    public String getName() {
        return name;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
