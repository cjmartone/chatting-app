package com.example.chattingapp;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
    protected void onBindViewHolder(ChatMessageHolder holder, int i, final ChatMessage chatMessage) {
        if(chatMessage.getImage() != null) {
            Picasso.get().load(chatMessage.getImage()).into(holder.image);
        }
        else{
            holder.image.setImageDrawable(null);
        }

        if(chatMessage.getAudio() != null){
            holder.audioPlayer.setText(R.string.play_audio);
            holder.audioPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(chatMessage.getAudio());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            holder.audioPlayer.setVisibility(View.GONE);
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
        Button audioPlayer;
        TextView timestamp;

        public ChatMessageHolder(View itemView){
            super(itemView);

            messageText = itemView.findViewById(R.id.message_text);
            messageUser = itemView.findViewById(R.id.message_user);
            image = itemView.findViewById(R.id.image);
            audioPlayer = itemView.findViewById(R.id.audio_player_button);
            timestamp = itemView.findViewById(R.id.message_time);
        }
    }
}
