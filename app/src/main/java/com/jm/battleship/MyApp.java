package com.jm.battleship;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class MyApp extends Application {
    private MediaManager mediaManager;
    private static SoundPool soundPool;
    private boolean isMusicOn;
    private boolean isSoundOn;
    private SharedPreferences sharedPref;
    public static final int SOUND_ID_GAME_START = 0;
    public static final int SOUND_ID_PLACE = 1;
    public static final int SOUND_ID_HIT = 2;
    public static final int SOUND_ID_MISS = 3;
    public static final int SOUND_ID_FIRE = 4;
    public static final int SOUND_ID_PICKUP = 5;
    public static final int SOUND_ID_WIN = 6;
    public static final int SOUND_ID_LOOSE = 7;

    @Override
    public void onCreate() {
        super.onCreate();
        initSoundAndMusic();
    }

    public boolean isMusicOn() {
        return isMusicOn;
    }

    public void setMusicOn(boolean musicOn) {
        isMusicOn = musicOn;
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    public void setSoundOn(boolean soundOn) {
        isSoundOn = soundOn;
    }

    private void initSoundAndMusic() {
        //SharedPreferences for saving game settings
        sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
                //Load switch checked status
        isMusicOn = sharedPref.getBoolean("music", false);
        isSoundOn = sharedPref.getBoolean("sound", false);

        //Initialize MediaManager for BGM
        mediaManager = new MediaManager(this);
        //Initialize SoundPool for Sound effects
        final int NUMBER_OF_SIMULTANEOUS_SOUNDS = 5;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(NUMBER_OF_SIMULTANEOUS_SOUNDS)
                    .build();
        } else {
            // Deprecated way of creating a SoundPool before Android API 21.
            soundPool = new SoundPool(NUMBER_OF_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void playSoundEffect(int soundId) {
        if(!isSoundOn) {
            return;
        }
        final float LEFT_VOLUME_VALUE = 1.0f;
        final float RIGHT_VOLUME_VALUE = 1.0f;
        final int MUSIC_LOOP = 0;
        final int SOUND_PLAY_PRIORITY = 0;
        final float PLAY_RATE= 1.0f;
        switch (soundId) {
            case SOUND_ID_GAME_START:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_game_start, 1);
                break;
            case SOUND_ID_PLACE:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_place, 1);
                break;
            case SOUND_ID_HIT:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_hit, 1);
                break;
            case SOUND_ID_MISS:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_miss, 1);
                break;
            case SOUND_ID_FIRE:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_fire, 1);
                break;
            case SOUND_ID_PICKUP:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_pickup, 1);
                break;
            case SOUND_ID_WIN:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_win, 1);
                break;
            case SOUND_ID_LOOSE:
                soundId = soundPool.load(getApplicationContext(), R.raw.se_loose, 1);
                break;
        }
        soundPool.play(soundId, LEFT_VOLUME_VALUE, RIGHT_VOLUME_VALUE, SOUND_PLAY_PRIORITY, MUSIC_LOOP, PLAY_RATE);
    }

    public void loadMusic(int musicId) {
        mediaManager.load(musicId);
    }

    public void playMusic() {
        mediaManager.play();
    }

    public void pauseMusic() {
        mediaManager.pause();
    }
}
