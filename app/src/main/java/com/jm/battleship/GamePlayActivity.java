package com.jm.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class GamePlayActivity extends AppCompatActivity {

    private BoardView myBoardView;
    private BoardView opponentBoardView;
    private TextView tvStatus;
    private TextView tvResult;
    private String command;
    private boolean hasCommand;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private GameManager game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        tvStatus = findViewById(R.id.tv_status);
        tvResult = findViewById(R.id.tv_result);
        Board myBoard = (Board) getIntent().getSerializableExtra("board");;
        createBoards(myBoard);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (writer != null) {
            writer.close();
        }
        try {
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void putCommand(String com) {
        while (hasCommand) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        command = com;
        hasCommand = true;
        notify();
    }

    private synchronized String getCommand() {
        while (!hasCommand) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        hasCommand = false;
        notify();
        return command;
    }

    private void createBoards(Board myBoard) {
        myBoardView = findViewById(R.id.my_board_view);
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

        game = new GameManager(myBoard, opponentBoard);
        setUpNetwork();
    }

    private void enableOpponentBoard() {
        opponentBoardView.post(() -> opponentBoardView.setOnTouchListener(new BoardTouchListener()));
    }

    private void disableOpponentBoard() {
        opponentBoardView.post(() -> opponentBoardView.setOnTouchListener(null));
    }

    private void displayMessage(String message) {
        tvStatus.post(() -> tvStatus.setText(message));
    }

    private void displayResult(String result) {
        tvResult.post(() -> tvResult.setText(result));
    }

    private void setUpNetwork() {
        Thread readThread = new Thread(() -> {
            Socket socket = null;
            while (socket == null) {
                displayMessage("Trying to connect to server");
                try {
                    socket = new Socket("ec2-52-195-7-157.ap-northeast-1.compute.amazonaws.com", 5228);
//                    socket = new Socket("192.168.100.130", 5228);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                displayMessage("Connected to server");
                writer = new PrintWriter(socket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Thread playThread = new Thread(() -> game.play());
                playThread.start();
                while (game.isPlaying) {
                    String command = reader.readLine();
                    putCommand(command);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        readThread.start();
    }

    class GameManager {
        public static final String SHOOT = "Shoot";
        public static final String WAIT = "Wait";
        public static final String WIN = "Win";
        public static final String LOSE = "Lose";
        public static final String CARRIER = "Carrier";
        public static final String BATTLESHIP = "Battleship";
        public static final String CRUISER = "Cruiser";
        public static final String SUBMARINE = "Submarine";
        public static final String DESTROYER = "Destroyer";
        public static final String HIT = "Hit";
        public static final String MISS = "Miss";
        public static final String KILL = "Kill";
        public static final String OPPONENT_DISCONNECTED = "OpponentDisconnected";
        private final Board board;
        private final Board opponentBoard;
        private Cell lastShootCell;
        private boolean isPlaying = true;

        private GameManager(Board board, Board opponentBoard) {
            this.board = board;
            this.opponentBoard = opponentBoard;
        }

        private void play() {
            while (isPlaying) {
                String cmd = getCommand();
                displayMessage(cmd);
                switch (cmd) {
                    case OPPONENT_DISCONNECTED:
                        isPlaying = false;
                        displayMessage("OPPONENT DISCONNECTED");
                    case SHOOT:
                        enableOpponentBoard();
                        displayMessage("YOUR TURN");
                        break;
                    case WAIT:
                        displayMessage("OPPONENT'S TURN");
                        break;
                    case WIN:
                        String shipName = null;
                        for (Ship ship : opponentBoard.getShips()) {
                            if (!ship.isSunk()) {
                                shipName = ship.getName();
                                break;
                            }
                        }
                        updateOpponentBoardView(shipName);
                    case LOSE:
                        displayResult("You " + cmd);
                        isPlaying = false;
                        break;
                    case MISS:
                        //
                        displayResult("You missed");
                        disableOpponentBoard();
                        lastShootCell.hit();
                        opponentBoardView.invalidate();
                        lastShootCell = null;
                        break;
                    case CARRIER:
                    case BATTLESHIP:
                    case CRUISER:
                    case SUBMARINE:
                    case DESTROYER:
                        updateOpponentBoardView(cmd);
                        break;
                    default: // cell location in "row, column" format
                        if (!cmd.equals("null")) {
                            String[] location = cmd.split(",");
                            int x = Integer.parseInt(location[0]);
                            int y = Integer.parseInt(location[1]);
                            String result = board.checkYourself(x, y);
                            myBoardView.invalidate();
                            new MessageSender().execute(result);
                        } else {
                            displayMessage("Networking error");
                        }
                }
            }
        }

        private void updateOpponentBoardView(String shipName) {
            if (lastShootCell != null) {
                Ship ship = opponentBoard.getShip(shipName);
                if (ship != null) {
                    lastShootCell.hit(ship);
                    String result = "hit";
                    if (ship.isSunk()) {
                        result = "sunk";
                    }
                    displayResult("You " + result + " opponent's " + shipName);
                } else {
                    lastShootCell.hit();
                    displayResult("You hit opponent's ship");
                }
                lastShootCell = null;
                opponentBoardView.invalidate();
            }
        }
    }

    private class MessageSender extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... messages) {
            for (String message : messages) {
                // Write message to stream. We use a pipe to indicate the end of the message to the server.
                writer.println(message);
                writer.flush();
            }
            return null;
        }
    }

    private class BoardTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Cell cell = opponentBoardView.locateCell(event.getX(), event.getY());
                    if (!cell.isHit()) {
                        game.lastShootCell = cell;
                        String cellLocation = cell.getX() + "," + cell.getY();
                        new MessageSender().execute(cellLocation);
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