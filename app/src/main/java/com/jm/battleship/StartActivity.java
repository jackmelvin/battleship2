package com.jm.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btPlayVsPlayer = findViewById(R.id.bt_play_vs_player);
        Button btPlayVsComputer = findViewById(R.id.bt_play_vs_computer);

        btPlayVsPlayer.setOnClickListener(new PlayListener(GameManager.MODE_VS_PLAYER));
        btPlayVsComputer.setOnClickListener(new PlayListener(GameManager.MODE_VS_COMPUTER));
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