package com.jm.battleship;

class GameManager {
    public static final int MODE_VS_PLAYER = 0;
    public static final int MODE_VS_COMPUTER = 1;
    public static final String SHOOT = "Shoot";
    public static final String WAIT = "Wait";
    public static final String WIN = "Win";
    public static final String LOSE = "Lose";
    public static final String CARRIER = "Carrier";
    public static final String BATTLESHIP = "Battleship";
    public static final String CRUISER = "Cruiser";
    public static final String SUBMARINE = "Submarine";
    public static final String DESTROYER = "Destroyer";
    public static final String MISS = "Miss";
    public static final String HIT = "Hit";
    public static final String KILL = "Kill";
    public static final String NULL = "null";
    public static final String OPPONENT_DISCONNECTED = "OpponentDisconnected";

    Player me;

    GameManager(int mode, GamePlayActivity activity, BoardView myBoardView, BoardView opponentBoardView) {
        String ip;
        int port;
        if (mode == MODE_VS_PLAYER) {
            ip = "ec2-52-195-7-157.ap-northeast-1.compute.amazonaws.com";
            port = 5228;
        } else {
            ip = "127.0.0.1";
            port = new GameOnDeviceServer().start();
            opponentBoardView.getBoard().placeShipsRandomly();
            new ComputerPlayer(ip, port, activity, opponentBoardView, myBoardView);
        }
        me = new Player(ip, port, activity, myBoardView, opponentBoardView);
    }
}