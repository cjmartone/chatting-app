package com.example.chattingapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chattingapp.Database.DatabaseRepo;
import com.example.chattingapp.Database.OnDataGetListener;
import com.example.chattingapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 0;
    private FirebaseFirestore db;
    private DatabaseRepo dbRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        db = FirebaseFirestore.getInstance();
        dbRepo = new DatabaseRepo();
        startUser();
    }

    public void startUser(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else{
            Toast.makeText(this, "Welcome " + currentUser.getDisplayName(), Toast.LENGTH_LONG).show();
            createHomeScreen(currentUser);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();
                dbRepo.addUserToDB(newUser);
                createHomeScreen(newUser);
            }
            else{
                Toast.makeText(this, "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void createHomeScreen(FirebaseUser currentUser){
        String uid = currentUser.getUid();
        LinearLayout friendListLayout = findViewById(R.id.friendListLayout);
        populateFriendList(friendListLayout, uid);
    }

    public void populateFriendList(final LinearLayout layout, String uid){
        dbRepo.getFriends(uid, new OnDataGetListener(){
            @Override
            public void onSuccess(Object data){
                ArrayList<String> friendIds = (ArrayList<String>)data;
                for(final String friendId : friendIds){
                    TextView view = new TextView(layout.getContext());
                    view.setText(friendId);
                    view.setTextColor(Color.BLACK);
                    view.setTextSize(18);
                    view.setPadding(20, 10, 20, 10);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    view.setLayoutParams(layoutParams);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, MessageScreenActivity.class);
                            intent.putExtra("FRIEND_ID", friendId);
                            startActivity(intent);
                        }
                    });

                    layout.addView(view);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "You have been signed out", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
        else if(item.getItemId() == R.id.menu_add_friend){
            Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
            startActivity(intent);
        }
        return true;
    }
}