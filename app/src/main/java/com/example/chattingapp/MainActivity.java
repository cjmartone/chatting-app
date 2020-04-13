package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        db = FirebaseFirestore.getInstance();
        startUser();
    }

    public void createHomeScreen(FirebaseUser currentUser){
        String uid = currentUser.getUid();
        LinearLayout friendListLayout = findViewById(R.id.friendListLayout);
        populateFriendList(friendListLayout, uid);
    }

    public void populateFriendList(final LinearLayout layout, String uid){
        db.collection(uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        TextView view = new TextView(layout.getContext());

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

                        view.setText(doc.getId());
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                                startActivity(intent);
                            }
                        });
                        view.setTextColor(Color.BLACK);
                        view.setTextSize(18);
                        view.setPadding(20, 10, 20, 10);
                        view.setLayoutParams(layoutParams);

                        layout.addView(view);
                    }
                }
            }
        });
    }

    public void startUser(){

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) {
            createHomeScreen(currentUser);
        }
        else {
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Sign in success");
                                createHomeScreen(firebaseAuth.getCurrentUser());
                            } else {
                                System.out.println("Sign in failed");
                            }
                        }
                    });
        }
    }
}
