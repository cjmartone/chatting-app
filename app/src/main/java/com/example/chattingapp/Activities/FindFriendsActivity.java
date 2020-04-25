package com.example.chattingapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattingapp.Database.DatabaseRepository;
import com.example.chattingapp.Database.OnDataCompleteListener;
import com.example.chattingapp.R;
import com.example.chattingapp.DTO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity {

    private DatabaseRepository dbRepo;
    private ArrayList<String> friendIds;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friend_screen);

        dbRepo = new DatabaseRepository();
        friendIds = getFriendIds();

        LinearLayout layout = findViewById(R.id.search_friend_list);
        addSearchForFriendButtonListener(layout);
    }

    public ArrayList<String> getFriendIds(){
        final ArrayList<String> friendIds = new ArrayList<>();
        dbRepo.getFriends(FirebaseAuth.getInstance().getCurrentUser().getUid(), new OnDataCompleteListener() {
            @Override
            public void onSuccess(Object data) {
                ArrayList<User> users = (ArrayList<User>) data;
                for(User user: users){
                    friendIds.add(user.getUid());
                }
            }
        });
        return friendIds;
    }

    public void addSearchForFriendButtonListener(final LinearLayout layout){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Button searchForFriend = findViewById(R.id.search_for_friend_button);
        searchForFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchFriendText = findViewById(R.id.search_friend_text);

                dbRepo.searchUsersFor(searchFriendText.getText().toString(), new OnDataCompleteListener(){
                    @Override
                    public void onSuccess(Object data){
                        layout.removeAllViews();

                        ArrayList<User> users = (ArrayList<User>)data;

                        TextView label = new TextView(layout.getContext());
                        if(users.size() == 0) {
                            label.setText("No users found");
                        }
                        else{
                            label.setText("Tap on a user's name to add them to your friends list.");
                        }
                        layout.addView(label);

                        int usersAdded = 0;
                        for(final User user : users){
                            String userId = user.getUid();
                            if(!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && !friendIds.contains(userId)) {
                                usersAdded++;
                                TextView view = new TextView(layout.getContext());
                                view.setText(user.getDisplayName());
                                view.setTextColor(Color.BLACK);
                                view.setTextSize(24);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                view.setLayoutParams(layoutParams);

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dbRepo.addFriend(new User(currentUser), user);
                                        Toast.makeText(layout.getContext(), "Added " + user.getDisplayName() + " as a friend.", Toast.LENGTH_LONG).show();
                                    }
                                });

                                layout.addView(view);
                            }
                        }
                        if(usersAdded == 0){
                            label.setText("No users found");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("result", 1);
        setResult(RESULT_OK, intent);
        finish();
    }
}
