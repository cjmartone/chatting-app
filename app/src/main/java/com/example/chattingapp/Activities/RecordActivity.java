package com.example.chattingapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chattingapp.Database.DatabaseRepository;
import com.example.chattingapp.Database.OnDataCompleteListener;
import com.example.chattingapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

public class RecordActivity  extends AppCompatActivity {

    private final int PERMISSION_RECORD_AUDIO = 5;
    private final int PERMISSION_WRITE_STORAGE = 6;

    private Button recordButton;
    private TextView recordLabel;

    private MediaRecorder mRecorder;
    private String mFileName;

    private DatabaseRepository dbRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_screen);

        recordButton = findViewById(R.id.record_button);
        recordLabel = findViewById(R.id.recording_label);

        mFileName = getApplicationContext().getFilesDir().getPath() + "/recorded_audio.3gp";

        dbRepo = new DatabaseRepository();

        requestAudioPermissions();
        setRecordButtonListener();
    }

    public void requestAudioPermissions(){
        if(ContextCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RecordActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO);
        }

        if(ContextCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RecordActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setRecordButtonListener(){
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean permissionGranted = (ContextCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

                if (permissionGranted) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        recordLabel.setText("Recording...");
                        startRecording();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        recordLabel.setText("Tap and Hold Above to Re-record\nOr Tap Send to Keep");

                        Button send = new Button(RecordActivity.this);
                        send.setText(R.string.send);
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dbRepo.uploadAudio(Uri.fromFile(new File(mFileName)), FirebaseAuth.getInstance().getCurrentUser(), getIntent().getStringExtra("FRIEND_ID"), new OnDataCompleteListener() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        onBackPressed();
                                    }
                                });
                            }
                        });

                        ConstraintLayout recordingLayout = findViewById(R.id.recording_screen_layout);
                        recordingLayout.addView(send);

                        stopRecording();
                    }
                }
                else{
                    Toast.makeText(RecordActivity.this, "Permission to record not granted.", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(this, "Could not record", Toast.LENGTH_LONG).show();
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}
