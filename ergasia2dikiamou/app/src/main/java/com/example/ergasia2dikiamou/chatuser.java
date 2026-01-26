package com.example.ergasia2dikiamou;

public class chatuser {
    public String uid;
    public String username;
    public String email;
    public chatuser() {
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public chatuser(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }
}