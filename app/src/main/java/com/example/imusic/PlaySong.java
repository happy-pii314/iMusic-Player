package com.example.imusic;

import android.media.MediaPlayer;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class PlaySong extends AppCompatActivity {

    TextView songName, currentTime, totalTime;
    SeekBar seekBar;

    ImageButton btnPlay, btnNext, btnPrev, btnShuffle, btnRepeat;

    ImageView songImage;

    MediaPlayer mediaPlayer;

    ArrayList<File> songs;
    int position;

    boolean isShuffle = false;
    boolean isRepeat = false;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        songName = findViewById(R.id.songName);
        songName.setSelected(true);

        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);

        seekBar = findViewById(R.id.seekBar);

        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        btnShuffle = findViewById(R.id.btnShuffle);
        btnRepeat = findViewById(R.id.btnRepeat);

        songImage = findViewById(R.id.songImage);

        songs = (ArrayList<File>) getIntent().getSerializableExtra("songlist");
        position = getIntent().getIntExtra("position",0);

        if(songs == null || songs.size() == 0){
            Toast.makeText(this,"No songs found",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playSong();

        btnPlay.setOnClickListener(v -> {

            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            }else{
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            }

        });

        btnNext.setOnClickListener(v -> nextSong());

        btnPrev.setOnClickListener(v -> prevSong());

        btnShuffle.setOnClickListener(v -> {

            isShuffle = !isShuffle;

            if(isShuffle){
                Toast.makeText(this,"Shuffle ON",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Shuffle OFF",Toast.LENGTH_SHORT).show();
            }

        });

        btnRepeat.setOnClickListener(v -> {

            isRepeat = !isRepeat;

            if(isRepeat){
                Toast.makeText(this,"Repeat ON",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Repeat OFF",Toast.LENGTH_SHORT).show();
            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser && mediaPlayer != null){
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    void playSong(){

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        File song = songs.get(position);

        songName.setText(song.getName());

        // 🎵 Get Album Art
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(song.getAbsolutePath());

        byte[] art = mmr.getEmbeddedPicture();

        if (art != null) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            songImage.setImageBitmap(bitmap);

        } else {

            songImage.setImageResource(R.drawable.logo);

        }

        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(song));

        if(mediaPlayer == null){
            Toast.makeText(this,"Cannot play this song",Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer.start();

        btnPlay.setImageResource(android.R.drawable.ic_media_pause);

        seekBar.setMax(mediaPlayer.getDuration());

        totalTime.setText(formatTime(mediaPlayer.getDuration()));

        updateSeekBar();

        mediaPlayer.setOnCompletionListener(mp -> {

            if(isRepeat){
                playSong();
            }
            else if(isShuffle){
                position = new Random().nextInt(songs.size());
                playSong();
            }
            else{
                nextSong();
            }

        });

    }

    void nextSong(){

        position++;

        if(position >= songs.size()){
            position = 0;
        }

        playSong();

    }

    void prevSong(){

        position--;

        if(position < 0){
            position = songs.size()-1;
        }

        playSong();

    }

    void updateSeekBar(){

        if(mediaPlayer == null) return;

        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        currentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));

        if(mediaPlayer.isPlaying()){
            handler.postDelayed(this::updateSeekBar,500);
        }

    }

    String formatTime(int duration){

        int minutes = duration / 1000 / 60;
        int seconds = duration / 1000 % 60;

        String sec = String.valueOf(seconds);

        if(seconds < 10){
            sec = "0" + seconds;
        }

        return minutes + ":" + sec;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}