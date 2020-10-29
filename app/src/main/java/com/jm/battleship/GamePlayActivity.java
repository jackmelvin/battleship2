package com.jm.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class GamePlayActivity extends AppCompatActivity {
    // a view to display opponent's board
    private BoardView opponentBoardView;
    // a view to display some text messages
    private TextView tvStatus;
    private TextView tvResult;
    // the game
    private GameManager game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        tvStatus = findViewById(R.id.tv_status);
        tvResult = findViewById(R.id.tv_result);
        int mode = getIntent().getIntExtra("mode", GameManager.MODE_VS_PLAYER);
        Board myBoard = (Board) getIntent().getSerializableExtra("board");;
        setUp(mode, myBoard);
    }

    private void setUp(int mode, Board myBoard) {
        // a view to display player's board
        BoardView myBoardView = findViewById(R.id.my_board_view);
        opponentBoardView = findViewById(R.id.opponent_board_view);

        myBoardView.setBoard(myBoard);
        myBoardView.setReadyToDraw(true);
        Board opponentBoard = new Board();
        for (Ship ship : opponentBoard.getShips()) {
            ship.setVisible(false);
        }
        opponentBoardView.setBoard(opponentBoard);
        opponentBoardView.setReadyToDraw(true);
        disableOpponentBoard();

        game = new GameManager(mode, GamePlayActivity.this, myBoardView, opponentBoardView);
    }

    void enableOpponentBoard() {
        opponentBoardView.post(() -> opponentBoardView.setOnTouchListener(new BoardTouchListener()));
    }

    void disableOpponentBoard() {
        opponentBoardView.post(() -> opponentBoardView.setOnTouchListener(null));
    }

    void displayMessage(String message) {
        tvStatus.post(() -> tvStatus.setText(message));
    }

    void displayResult(String result) {
        tvResult.post(() -> tvResult.setText(result));
    }

    private class BoardTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Cell cell = opponentBoardView.locateCell(event.getX(), event.getY());
                    if (!cell.isHit()) {
                        game.me.lastShootCell = cell;
                        String cellLocation = cell.getX() + "," + cell.getY();
                        game.me.sendMessage(cellLocation);
                        disableOpponentBoard();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        }
    }
}