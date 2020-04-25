package com.example.chattingapp.DTO;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String image;
    private String audio;
    @ServerTimestamp
    private Date messageTime;

    public ChatMessage(String text, String user, String image, String audio){
        messageText = text;
        messageUser = user;
        this.image = image;
        this.audio = audio;
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

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Date getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(Date timestamp){
        messageTime = timestamp;
    }
}
