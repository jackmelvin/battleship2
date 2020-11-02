package com.jm.battleship;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

public class StartActivity extends AppCompatActivity {
    // custom application class for music and sound
    MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        myApp = (MyApp) getApplication();
        Button btPlayVsPlayer = findViewById(R.id.bt_play_vs_player);
        Button btPlayVsComputer = findViewById(R.id.bt_play_vs_computer);
        SwitchCompat swMusic = findViewById(R.id.sw_music);
        SwitchCompat swSound = findViewById(R.id.sw_sound);

        swMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            myApp.setMusicOn(isChecked);
            if (isChecked) {
                myApp.playMusic(MyApp.MUSIC_ID_START);
            } else {
                myApp.pauseMusic();
            }
        });
        swSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            myApp.setSoundOn(isChecked);
        });
        swMusic.setChecked(myApp.isMusicOn());
        swSound.setChecked(myApp.isSoundOn());

        btPlayVsPlayer.setOnClickListener(new PlayListener(GameManager.MODE_VS_PLAYER));
        btPlayVsComputer.setOnClickListener(new PlayListener(GameManager.MODE_VS_COMPUTER));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApp.saveSettings();
        myApp.releaseMediaPlayer();
        myApp.releaseSoundPool();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myApp.resumeMusic();
    }

    class PlayListener implements View.OnClickListener {
        int mode;
        PlayListener(int mode) {
            this.mode = mode;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(StartActivity.this, PlaceShipActivity.class);
            intent.putExtra("mode", mode);
            startActivity(intent);
        }
    }
}