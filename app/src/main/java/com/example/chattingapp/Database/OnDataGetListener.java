package com.example.chattingapp.Database;

import com.example.chattingapp.User;

import java.util.ArrayList;

public interface OnDataGetListener {
    ArrayList<User> onSuccess(Object data);
}
