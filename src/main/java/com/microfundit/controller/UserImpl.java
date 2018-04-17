package com.microfundit.controller;

/**
 * Created by Kevin Kimaru Chege on 4/11/2018.
 */
public class UserImpl {
    private String username;
    private String role;
    private long id;

    public UserImpl() {
    }

    public UserImpl(String username, String role, long id) {
        this.username = username;
        this.role = role;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
