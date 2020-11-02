package com.jm.battleship;

import android.content.Context;
import android.media.MediaPlayer;

class MediaManager {

    private Context mContext;
    private MediaPlayer mediaPlayer;

    public MediaManager(Context context) {
        mContext = context;
        mediaPlayer = new MediaPlayer();
    }

    void play(int musicId) {
        release();
        mediaPlayer = MediaPlayer.create(mContext, musicId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    void pause() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    void release() {
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