package com.jm.battleship;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class MyApp extends Application {
    private MediaManager mediaManager;
    private SoundPool soundPool;
    private boolean isMusicOn;
    private boolean isSoundOn;
    public static int SOUND_ID_GAME_START;
    public static int SOUND_ID_PLACE;
    public static int SOUND_ID_HIT;
    public static int SOUND_ID_MISS;
    public static int SOUND_ID_FIRE;
    public static int SOUND_ID_PICKUP;
    public static int SOUND_ID_WIN;
    public static int SOUND_ID_LOSE;
    public static int SOUND_ID_BLOCKED;
    public static int MUSIC_ID_START = R.raw.bg_music_start;
    public static int MUSIC_ID_PLAY = R.raw.bg_music_play;
    public static int MUSIC_ID_PLACE = R.raw.bg_music_place;
    final float LEFT_VOLUME_VALUE = 1.0f;
    final float RIGHT_VOLUME_VALUE = 1.0f;
    final int MUSIC_LOOP = 0;
    final int SOUND_PLAY_PRIORITY = 0;
    final float PLAY_RATE= 1.0f;
    SharedPreferences sharedPref;

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
        isMusicOn = sharedPref.getBoolean("music", true);
        isSoundOn = sharedPref.getBoolean("sound", true);

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
        // initialize sound effects
        SOUND_ID_GAME_START = soundPool.load(getApplicationContext(), R.raw.se_game_start, 1);
        SOUND_ID_PLACE = soundPool.load(getApplicationContext(), R.raw.se_place, 1);
        SOUND_ID_HIT = soundPool.load(getApplicationContext(), R.raw.se_hit, 1);
        SOUND_ID_MISS = soundPool.load(getApplicationContext(), R.raw.se_miss, 1);
        SOUND_ID_FIRE = soundPool.load(getApplicationContext(), R.raw.se_fire, 1);
        SOUND_ID_PICKUP = soundPool.load(getApplicationContext(), R.raw.se_pickup, 1);
        SOUND_ID_WIN = soundPool.load(getApplicationContext(), R.raw.se_win, 1);
        SOUND_ID_LOSE = soundPool.load(getApplicationContext(), R.raw.se_lose, 1);
        SOUND_ID_BLOCKED = soundPool.load(getApplicationContext(), R.raw.se_block, 1);
    }

    public void saveSettings() {
        if (sharedPref == null) {
            sharedPref = getSharedPreferences("settings", MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("music", isMusicOn);
        editor.putBoolean("sound", isSoundOn);
        editor.apply();
    }

    public void playSoundEffect(int soundId) {
        if(!isSoundOn || soundPool == null) {
            return;
        }
        soundPool.play(soundId, LEFT_VOLUME_VALUE, RIGHT_VOLUME_VALUE, SOUND_PLAY_PRIORITY, MUSIC_LOOP, PLAY_RATE);
    }

    public void releaseSoundPool() {
        if (soundPool != null) {
            soundPool.release();
        }
    }

    public void playMusic(int musicId) {
        if (isMusicOn) {
            mediaManager.play(musicId);
        }
    }

    public void pauseMusic() {
        mediaManager.pause();
    }

    public void resumeMusic() {
        mediaManager.resume();
    }

    public void releaseMediaPlayer() {
        mediaManager.release();
    }
}
