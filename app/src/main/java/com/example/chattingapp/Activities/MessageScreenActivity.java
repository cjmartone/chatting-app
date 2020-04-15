package com.example.chattingapp.Activities;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_screen);

        db = FirebaseFirestore.getInstance();
        dbRepo = new DatabaseRepo();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        setNewMessageListener(currentUser.getUid());

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

    public void addSendButtonListener(){
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText input = findViewById(R.id.input);

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String friendId = getIntent().getStringExtra("FRIEND_ID");
                dbRepo.postMessage(currentUser.getUid(), friendId, input.getText().toString(), currentUser.getDisplayName());
                input.setText("");
            }
        });
    }
}
