package com.example.chattingapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattingapp.ChatMessage;
import com.example.chattingapp.Database.DatabaseRepo;
import com.example.chattingapp.Database.OnDataGetListener;
import com.example.chattingapp.R;
import com.example.chattingapp.RecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MessageScreenActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DatabaseRepo dbRepo;
    private RecyclerAdapter adapter;
    private static final int RESULT_LOAD_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_screen);

        db = FirebaseFirestore.getInstance();
        dbRepo = new DatabaseRepo();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        setNewMessageListener(currentUser.getUid());

        addGalleryButtonListener();
        addSendButtonListener();
    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    public void setNewMessageListener(String uid){
        Query query = db.collection("Users").document(uid).collection("friends").document(getIntent().getStringExtra("FRIEND_ID")).collection("messages").orderBy("messageTime");
        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapter = new RecyclerAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.message_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE){
            if(resultCode == RESULT_OK && data != null){
                Uri selectedImage = data.getData();
                dbRepo.uploadImage(selectedImage, FirebaseAuth.getInstance().getCurrentUser(), getIntent().getStringExtra("FRIEND_ID"), new OnDataGetListener() {
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(MessageScreenActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    public void addGalleryButtonListener(){
        Button openGalleryButton = findViewById(R.id.openGalleryButton);
        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }

    public void addSendButtonListener(){
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText input = findViewById(R.id.input);

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String friendId = getIntent().getStringExtra("FRIEND_ID");
                dbRepo.postMessage(currentUser.getUid(), friendId, input.getText().toString(), currentUser.getDisplayName(), null);

                input.setText("");
            }
        });
    }
}
