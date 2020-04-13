package com.example.chattingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MessagesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_screen);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        setListener(currentUser.getUid());

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String friendId = getIntent().getStringExtra("FRIEND_ID");
                db.collection(currentUser.getUid()).document(friendId).collection("messages")
                        .add(new ChatMessage(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MessagesActivity.this,"Could not send a message at this time.", Toast.LENGTH_LONG).show();
                            }
                        });
                input.setText("");
            }
        });
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

    public void setListener(String uid){
        Query query = db.collection(uid).document(getIntent().getStringExtra("FRIEND_ID")).collection("messages").orderBy("messageTime");
        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapter = new RecyclerAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.message_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

//        db.collection(uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
//                if(e != null){
//                    System.out.println("There was an issue listening to the db");
//                    return;
//                }
//
//                for(QueryDocumentSnapshot doc : value){
//                    ArrayList<String> messages = (ArrayList<String>)doc.getData().get("messages");
//                    ArrayList<Integer> layoutMessages = getMessageIds(layout);
//                    int numMessages = 0;
//                    for(String message : messages){
//                        numMessages ++;
//                        if( ! layoutMessages.contains(createMessageId(numMessages))){
//                            createSentBubble(message, numMessages);
//                        }
//                    }
//                }
//            }
//        });
    }

//    public ArrayList<Integer> getMessageIds(LinearLayout messageLayout){
//        ArrayList<Integer> layoutMessages = new ArrayList<>();
//        int numMessages = messageLayout.getChildCount();
//        for(int i = 0; i < numMessages; i++){
//            layoutMessages.add(messageLayout.getChildAt(i).getId());
//        }
//
//        return layoutMessages;
//    }
//
//    public void createSentBubble(String message, int id){
//        TextView view = new TextView(layout.getContext());
//
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.weight = 1.0f;
//        layoutParams.gravity = Gravity.BOTTOM|Gravity.RIGHT;
//
//        view.setId(createMessageId(id));
//        view.setText(message);
//        view.setTextColor(Color.WHITE);
//        view.setTextSize(18);
//        view.setBackgroundResource(message_sent);
//        view.setPadding(20, 10, 20, 10);
//        view.setLayoutParams(layoutParams);
//
//        layout.addView(view);
//    }
//
//    public int createMessageId(int id){
//        int sum = 0;
//        char[] message = "message".toCharArray();
//        for(char letter : message){
//            sum += letter;
//        }
//        return sum + id;
//    }

}
