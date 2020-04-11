package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.messageLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        signInUser();

        db = FirebaseFirestore.getInstance();
        setListener();
    }

    public void setListener(){
        db.collection("friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    System.out.println("There was an issue listening to the db");
                    return;
                }

                for(QueryDocumentSnapshot doc : value){
                    ArrayList<String> messages = (ArrayList<String>)doc.getData().get("messages");
                    for(String message : messages){
                        createSentBubble(message);
                    }
                }
            }
        });
    }

    public void createSentBubble(String message){
        TextView view = new TextView(layout.getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.BOTTOM|Gravity.RIGHT;

        view.setText(message);
        view.setTextColor(Color.WHITE);
        view.setTextSize(18);
        view.setBackgroundResource(message_sent);
        view.setPadding(20, 10, 20, 10);
        view.setLayoutParams(layoutParams);

        layout.addView(view);
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

    public void signInUser(){
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            System.out.println("Sign in success");
                        }
                        else{
                            System.out.println("Sign in failed");
                        }
                    }
                });
    }
}
