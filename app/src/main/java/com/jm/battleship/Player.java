package com.jm.battleship;

public class Player extends AbstractPlayer {

    public Player(String ip, int port, GamePlayActivity context, Board myBoard, Board opponentBoard) {
        super(ip, port, context, myBoard, opponentBoard);
    }

    @Override
    void shoot() {
        displayMessage("YOUR TURN", "");
        activity.enableOpponentBoard();
    }

    @Override
    void _wait() {
        displayMessage("OPPONENT'S TURN", "");
        activity.disableOpponentBoard();
    }

    @Override
    void playSoundEffect(int soundId) {
        myApp.playSoundEffect(soundId);
    }

    @Override
    void endGame(String result) {
        if (result.equals(GameManager.WIN)) {
            displayMessage("You win", "GGWP");
            for (Ship ship : opponentBoard.getShips()) {
                if (!ship.isSunk()) {
                    processShootResult(ship.getName());
                    break;
                }
            }
            myApp.playSoundEffect(MyApp.SOUND_ID_WIN);
        } else if (result.equals(GameManager.LOSE)) {
            displayMessage("You lose", "LOSERRRRRR");
            myApp.playSoundEffect(MyApp.SOUND_ID_LOSE);
        }
        myApp.pauseMusic();
        isPlaying = false;
    }

    @Override
    void displayMessage(String message, String result) {
        if (!message.isEmpty()) {
            activity.displayMessage(message);
        }
        if (!result.isEmpty()) {
            activity.displayResult(result);
        }
    }

    @Override
    void checkShoot(String cellLocation) {
        super.checkShoot(cellLocation);
        String message = "Opponent ";
        String[] location = cellLocation.split(",");
        int x = Integer.parseInt(location[0]);
        int y = Integer.parseInt(location[1]);
        Ship hitShip = myBoard.getCell(x, y).getShip();
        if (hitShip != null) { // result is HIT or KILL
            if (hitShip.isSunk()) {
                message += "sunk";
            } else {
                message += "hit";
            }
            message += " your " + hitShip.getName();
        } else { // result is MISS
            message += "missed";
        }
        displayMessage("", message);
    }

    @Override
    void processShootResult(String shipNameOrMISS) {
        if (lastShootCell == null) {
            return;
        }
        // if result is MISS, ship = null
        Ship ship = opponentBoard.getShip(shipNameOrMISS);
        lastShootCell.hit(ship);
        String result = "You ";
        if (ship == null) { // MISS
            result += "missed";
        } else { // HIT or KILL
            if (ship.isSunk()) { // KILL
                result += "sunk";
            } else { // HIT
                result += "hit";
            }
            result += (" opponent's " + shipNameOrMISS);
        }
        activity.displayResult(result);
        lastShootCell = null;
    }
}
