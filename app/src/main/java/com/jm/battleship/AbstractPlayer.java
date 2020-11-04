package com.jm.battleship;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.jm.battleship.GameManager.BATTLESHIP;
import static com.jm.battleship.GameManager.CARRIER;
import static com.jm.battleship.GameManager.CRUISER;
import static com.jm.battleship.GameManager.DESTROYER;
import static com.jm.battleship.GameManager.LOSE;
import static com.jm.battleship.GameManager.MISS;
import static com.jm.battleship.GameManager.NULL;
import static com.jm.battleship.GameManager.OPPONENT_DISCONNECTED;
import static com.jm.battleship.GameManager.READY;
import static com.jm.battleship.GameManager.SHOOT;
import static com.jm.battleship.GameManager.SUBMARINE;
import static com.jm.battleship.GameManager.WAIT;
import static com.jm.battleship.GameManager.WIN;

public abstract class AbstractPlayer {
    // activity to display message and set board touch listener
    final GamePlayActivity activity;
    // the real player's board to display to the view
    final Board myBoard;
    // the real opponent's board to display to the view
    final Board opponentBoard;
    // the game server's ip
    final String ip;
    // the game server's port
    final int port;
    // a command received from the game server
    String command;
    // command available or not
    boolean hasCommand;
    // connection to the game server
    Socket socket;
    // PrintWriter to write to the game server
    PrintWriter writer;
    // BufferedReader to read from the game server
    BufferedReader reader;
    // game status
    boolean isPlaying = true;
    // save the last shot for calculating
    Cell lastShootCell;
    // custom Application class for music and sound
    MyApp myApp;

    // initialize things and set up server, start the game automatically when an object is created
    public AbstractPlayer(String ip, int port, GamePlayActivity context, Board myBoard, Board opponentBoard) {
        this.ip = ip;
        this.port = port;
        this.activity = context;
        this.myBoard = myBoard;
        this.opponentBoard = opponentBoard;
        this.myApp = (MyApp) context.getApplication();
        setUpNetwork();
    }

    // set up network connections and start the game
    private void setUpNetwork() {
        Thread readThread = new Thread(() -> {
            while (socket == null) {
                displayMessage("Trying to connect to server", "");
                try {
                    socket = new Socket(ip, port);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored){}
                }
            }
            try {
                displayMessage("Waiting for opponent to join", "");
                writer = new PrintWriter(socket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Thread playThread = new Thread(this::play);
                playThread.start();
                while (isPlaying) {
                    String command = reader.readLine();
                    putCommand(command);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        readThread.start();
    }

    // private access, only the setUpNetwork method called in constructor can start the game
    private void play() {
        while (isPlaying) {
            checkCommand(getCommand());
        }
    }

    // check and process commands received from the game server
    final void checkCommand(String command) {
        switch (command) {
            case READY:
                sendMessage(READY);
                break;
            case NULL:
            case OPPONENT_DISCONNECTED:
                isPlaying = false;
                displayMessage("Networking error", "Game ended");
                break;
            case SHOOT:
                shoot();
                break;
            case WAIT:
                _wait();
                break;
            case WIN:
                endGame(WIN);
                break;
            case LOSE:
                endGame(LOSE);
                break;
            //Shoot result
            case MISS:
                myApp.playSoundEffect(MyApp.SOUND_ID_MISS);
                processShootResult(command);
                break;
            case CARRIER:
            case BATTLESHIP:
            case CRUISER:
            case SUBMARINE:
            case DESTROYER:
                myApp.playSoundEffect(MyApp.SOUND_ID_HIT);
                processShootResult(command);
                break;
            default: // cell location in "row, column" format
                checkShoot(command);
        }
        activity.reDrawBoardViews();
    }

    abstract void endGame(String result);

    // check the board for shoot result and write it to the game server
    void checkShoot(String cellLocation) {
        String[] location = cellLocation.split(",");
        int x = Integer.parseInt(location[0]);
        int y = Integer.parseInt(location[1]);
        String result = myBoard.checkYourself(x, y);
        // result is either MISS or a ship's name
        sendMessage(result);
    }

    // synchronized method for updating command variable
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

    // synchronized method for getting command variable
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

    // send message to the game server
    final void sendMessage(String message) {
        new MessageSender().execute(message);
    }

    // do sending work in a worker thread
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

    void close() {
        isPlaying = false;
        if (writer != null) {
            writer.close();
        }
    }

    abstract void shoot();
    abstract void _wait();
    abstract void displayMessage(String message, String result);
    abstract void processShootResult(String shipName);
}
