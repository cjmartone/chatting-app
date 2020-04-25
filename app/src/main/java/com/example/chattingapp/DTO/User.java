package com.example.chattingapp.DTO;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String uid;
    private String displayName;

    public User(String uid, String displayName){
        this.uid = uid;
        this.displayName = displayName;
    }

    public User(FirebaseUser user){
        uid = user.getUid();
        displayName = user.getDisplayName();
    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getDisplayName(){
        return displayName;
    }

    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }
}
