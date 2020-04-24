package com.example.chattingapp;

import android.net.Uri;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String url;
    @ServerTimestamp
    private Date messageTime;

    public ChatMessage(String text, String user, String url){
        messageText = text;
        messageUser = user;
        this.url = url;
    }

    public ChatMessage(){}

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public Date getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(Date timestamp){
        messageTime = timestamp;
    }
}
