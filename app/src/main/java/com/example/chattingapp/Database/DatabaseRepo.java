package com.example.chattingapp.Database;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chattingapp.Activities.MessageScreenActivity;
import com.example.chattingapp.ChatMessage;
import com.example.chattingapp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseRepo {

    private FirebaseFirestore db;
    private StorageReference storageReference;

    public DatabaseRepo(){
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void addUserToDB(FirebaseUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("Display Name", user.getDisplayName());
        db.collection("Users").document(user.getUid()).set(userInfo);
    }

    public void getFriends(String document, final OnDataGetListener listener){
        final ArrayList<User> friendIds = new ArrayList<>();
        db.collection("Users").document(document).collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        if(!doc.getId().equals("User Info")) {
                            friendIds.add(new User(doc.getId(), doc.getData().get("Display Name") + ""));
                        }
                    }
                }
                listener.onSuccess(friendIds);
            }
        });
    }

    public void postMessage(String uid, String friendId, String message, String user, String url){
        db.collection("Users").document(uid).collection("friends").document(friendId).collection("messages")
                .add(new ChatMessage(message, user, url));
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
                            System.out.println("DB " + displayName);
                        }
                    }
                }
                listener.onSuccess(match);
            }
        });
    }

    public void addFriend(String uid, String friendUid, String friendDisplayName){
        Map<String, Object> init = new HashMap<>();
        init.put("Display Name", friendDisplayName);
        db.collection("Users").document(uid).collection("friends").document(friendUid).set(init);

        ChatMessage message = new ChatMessage("This is the beginning of your chat history", "Chat App", null);
        db.collection("Users").document(uid).collection("friends").document(friendUid).collection("messages").add(message);
    }

    public void uploadImage(Uri image, final FirebaseUser user, final String friendId, final OnDataGetListener listener){
        final String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + imageId);
        imageRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        postMessage(user.getUid(), friendId, "", user.getDisplayName(), uri.toString());
                        listener.onSuccess("Complete");
                    }
                });
            }
        });
    }
}
