package com.example.chattingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.chattingapp.R.drawable.message_sent;

public class MessagesActivity extends AppCompatActivity {

    private LinearLayout layout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.messageLayout);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        setListener(currentUser.getUid());
    }

    public void setListener(String uid){
        db.collection(uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    System.out.println("There was an issue listening to the db");
                    return;
                }

                for(QueryDocumentSnapshot doc : value){
                    ArrayList<String> messages = (ArrayList<String>)doc.getData().get("messages");
                    ArrayList<Integer> layoutMessages = getMessageIds(layout);
                    int numMessages = 0;
                    for(String message : messages){
                        numMessages ++;
                        if( ! layoutMessages.contains(createMessageId(numMessages))){
                            createSentBubble(message, numMessages);
                        }
                    }
                }
            }
        });
    }

    public ArrayList<Integer> getMessageIds(LinearLayout messageLayout){
        ArrayList<Integer> layoutMessages = new ArrayList<>();
        int numMessages = messageLayout.getChildCount();
        for(int i = 0; i < numMessages; i++){
            layoutMessages.add(messageLayout.getChildAt(i).getId());
        }

        return layoutMessages;
    }

    public void createSentBubble(String message, int id){
        TextView view = new TextView(layout.getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.BOTTOM|Gravity.RIGHT;

        view.setId(createMessageId(id));
        view.setText(message);
        view.setTextColor(Color.WHITE);
        view.setTextSize(18);
        view.setBackgroundResource(message_sent);
        view.setPadding(20, 10, 20, 10);
        view.setLayoutParams(layoutParams);

        layout.addView(view);
    }

    public int createMessageId(int id){
        int sum = 0;
        char[] message = "message".toCharArray();
        for(char letter : message){
            sum += letter;
        }
        return sum + id;
    }

    public void sendMessage(View view){
        EditText editText = findViewById(R.id.editText);
        Map<String, String> data = new HashMap<>();
        data.put("message", editText.getText().toString());
        db.collection("messages")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("Added message");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to add message");
                    }
                });
        editText.setText("");
    }

}
