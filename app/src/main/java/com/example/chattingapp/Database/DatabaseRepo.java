package com.example.chattingapp.Database;

import androidx.annotation.NonNull;

import com.example.chattingapp.ChatMessage;
import com.example.chattingapp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseRepo {

    private FirebaseFirestore db;

    public DatabaseRepo(){
        db = FirebaseFirestore.getInstance();
    }

    public void addUserToDB(FirebaseUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("Display Name", user.getDisplayName());
        db.collection("Users").document(user.getUid()).set(userInfo);
    }

    public void getFriends(String document, final OnDataGetListener listener){
        final ArrayList<String> friendIds = new ArrayList<>();
        db.collection("Users").document(document).collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        if(!doc.getId().equals("User Info")) {
                            friendIds.add(doc.getId());
                        }
                    }
                }
                listener.onSuccess(friendIds);
            }
        });
    }

    public void postMessage(String uid, String friendId, String message, String user){
        db.collection("Users").document(uid).collection("friends").document(friendId).collection("messages")
                .add(new ChatMessage(message, user));
    }

    public void searchUsers(final String user, final OnDataGetListener listener){
        final ArrayList<User> match = new ArrayList<>();
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    String displayName;
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        displayName = doc.getData().get("Display Name") + "";
                        if(displayName.toLowerCase().contains(user.toLowerCase())) {
                            match.add(new User(doc.getId(), displayName));
                        }
                    }
                }
                listener.onSuccess(match);
            }
        });
    }

    public void addFriend(String uid, String friendUid){
        Map<String, Object> init = new HashMap<>();
        init.put("init", "done");
        db.collection("Users").document(uid).collection("friends").document(friendUid).set(init);

        ChatMessage message = new ChatMessage("This is the beginning of your chat history", "Chat App");
        db.collection("Users").document(uid).collection("friends").document(friendUid).collection("messages").add(message);
    }
}
