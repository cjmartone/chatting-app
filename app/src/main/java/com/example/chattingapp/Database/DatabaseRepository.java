package com.example.chattingapp.Database;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.chattingapp.DTO.ChatMessage;
import com.example.chattingapp.DTO.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
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

public class DatabaseRepository {

    private FirebaseFirestore db;
    private StorageReference storageReference;

    public DatabaseRepository(){
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void addUserToDB(FirebaseUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("Display Name", user.getDisplayName());
        db.collection("Users").document(user.getUid()).set(userInfo);
    }

    public void getFriends(String uid, final OnDataCompleteListener listener){
        final ArrayList<User> friendIds = new ArrayList<>();
        db.collection("Users").document(uid).collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public void postMessage(String uid, String friendId, ChatMessage message){
        db.collection("Users").document(uid).collection("friends").document(friendId).collection("messages")
                .add(message);
        db.collection("Users").document(friendId).collection("friends").document(uid).collection("messages")
                .add(message);
    }

    public void searchUsersFor(final String user, final OnDataCompleteListener listener){
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

    public void addFriend(User currentUser, User friend){
        String currentName = currentUser.getDisplayName();
        String friendName = friend.getDisplayName();
        String currentUid = currentUser.getUid();
        String friendUid = friend.getUid();

        Map<String, Object> init = new HashMap<>();
        init.put("Display Name", friendName);
        db.collection("Users").document(currentUid).collection("friends").document(friendUid).set(init);

        ChatMessage message = new ChatMessage("This is the beginning of your chat history", "Chat App", null, null);
        db.collection("Users").document(currentUid).collection("friends").document(friendUid).collection("messages").add(message);

        Map<String, Object> initFriend = new HashMap<>();
        init.put("Display Name", currentName);
        db.collection("Users").document(friendUid).collection("friends").document(currentUid).set(init);

        ChatMessage friendMessage = new ChatMessage("This is the beginning of your chat history", "Chat App", null, null);
        db.collection("Users").document(friendUid).collection("friends").document(currentUid).collection("messages").add(message);
    }

    public void uploadFile(Uri image, String child, final FirebaseUser user, final String friendId, final OnDataCompleteListener listener){
        final String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child(child + imageId);
        imageRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        postMessage(user.getUid(), friendId, new ChatMessage("", user.getDisplayName(), uri.toString(), null));
                        listener.onSuccess("Complete");
                    }
                });
            }
        });
    }

    public void uploadAudio(Uri audio, final FirebaseUser user, final String friendId, final OnDataCompleteListener listener){
        final String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("audio/" + imageId);
        imageRef.putFile(audio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        postMessage(user.getUid(), friendId, new ChatMessage("", user.getDisplayName(), null, uri.toString()));
                        listener.onSuccess("Complete");
                    }
                });
            }
        });
    }
}
