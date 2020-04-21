package com.example.chattingapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class RecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, RecyclerAdapter.ChatMessageHolder> {

    public RecyclerAdapter(FirestoreRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ChatMessageHolder holder, int i, ChatMessage chatMessage) {
        if(chatMessage.getUrl() != null) {
            Picasso.get().load(chatMessage.getUrl()).into(holder.image);
        }
        else{
            holder.image.setImageDrawable(null);
        }
        holder.messageText.setText(chatMessage.getMessageText());
        holder.messageUser.setText(chatMessage.getMessageUser());

        Date messageTime = chatMessage.getMessageTime();
        DateFormat df = new SimpleDateFormat("dd / MM / yyyy, HH:mm", Locale.US);
        if(messageTime == null){ //if a message is sent, the server timestamp will be null until it reaches server
            messageTime = Calendar.getInstance().getTime();
        }
        holder.timestamp.setText(df.format(messageTime));
    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        return new ChatMessageHolder(v);
    }

    class ChatMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText;
        TextView messageUser;
        ImageView image;
        TextView timestamp;

        public ChatMessageHolder(View itemView){
            super(itemView);

            messageText = itemView.findViewById(R.id.message_text);
            messageUser = itemView.findViewById(R.id.message_user);
            image = itemView.findViewById(R.id.image);
            timestamp = itemView.findViewById(R.id.message_time);
        }
    }
}
