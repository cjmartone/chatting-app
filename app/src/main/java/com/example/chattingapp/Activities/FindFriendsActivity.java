package com.example.chattingapp.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattingapp.Database.DatabaseRepo;
import com.example.chattingapp.Database.OnDataGetListener;
import com.example.chattingapp.R;
import com.example.chattingapp.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity {

    private DatabaseRepo dbRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friend_screen);

        dbRepo = new DatabaseRepo();

        LinearLayout layout = findViewById(R.id.search_friend_list);
        addSearchForFriendButtonListener(layout);
    }

    public void addSearchForFriendButtonListener(final LinearLayout layout){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Button searchForFriend = findViewById(R.id.search_for_friend_button);
        searchForFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchFriendText = findViewById(R.id.search_friend_text);
                dbRepo.searchUsers(searchFriendText.getText().toString(), new OnDataGetListener(){
                    @Override
                    public void onSuccess(Object data){
                        ArrayList<User> users = (ArrayList<User>)data;
                        for(final User user : users){
                            TextView view = new TextView(layout.getContext());
                            view.setText(user.getDisplayName());
                            view.setTextColor(Color.BLACK);
                            view.setTextSize(18);
                            view.setPadding(20, 10, 20, 10);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            view.setLayoutParams(layoutParams);

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dbRepo.addFriend(currentUser.getUid(), user.getUid());
                                }
                            });

                            layout.addView(view);
                        }
                    }
                });
            }
        });
    }
}
