package com.example.authservice.event;

import java.io.Serializable;

public class UserLoginEvent implements Serializable {
    private String username;
    private long timestamp;

    public UserLoginEvent() {}

    public UserLoginEvent(String username, long timestamp) {
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserLoginEvent{" +
                "username='" + username + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}