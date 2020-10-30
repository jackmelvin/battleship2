package com.jm.battleship;

import android.content.Context;
import android.media.MediaPlayer;

class MediaManager {

    private Context mContext;
    private int musicId;
    private MediaPlayer mediaPlayer;

    public MediaManager(Context context) {
        mContext = context;
        musicId = R.raw.bg_music;
        mediaPlayer = new MediaPlayer();
    }

    void load(int music_id) {
        mediaPlayer = MediaPlayer.create(mContext, music_id);
    }

    void play() {
        mediaPlayer = MediaPlayer.create(mContext, musicId);
        mediaPlayer.start();
    }

    void pause() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    void releaseMediaPlayer() {
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}