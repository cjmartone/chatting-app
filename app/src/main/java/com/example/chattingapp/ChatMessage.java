package com.example.chattingapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    @ServerTimestamp
    private Date messageTime;

    public ChatMessage(String text, String user){
        messageText = text;
        messageUser = user;
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

    public Date getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(Date timestamp){
        messageTime = timestamp;
    }
}
