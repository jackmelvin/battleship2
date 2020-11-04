package com.jm.battleship;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.jm.battleship.GameManager.*;

class ComputerPlayer extends AbstractPlayer {
    // first hit cell on a ship
    private Cell firstHit = null;
    // a hit after first hit on a ship
    private Cell nextHit = null;
    // directions to find the remaining cells of the ship
    private ArrayList<Integer> secondShootDir = new ArrayList<>(Arrays.asList(0, 1, 2, 3));

    public ComputerPlayer(String ip, int port, GamePlayActivity context, Board myBoard, Board opponentBoard) {
        super(ip, port, context, myBoard, opponentBoard);
    }

    @Override
    void shoot() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
       shootRandomly();
    }

    void shootRandomly() {
        Random rand = new Random();
        boolean shootSuccessfully = false;
        while (!shootSuccessfully) {
            int x = 0, y = 0;

            if (firstHit == null) { //Not getting any hit yet
                // Shoot randomly
                x = rand.nextInt(10);
                y = rand.nextInt(10);
                if (opponentBoard.getCell(x, y).isHit()) {
                    continue;
                }
            } else if (nextHit == null) { //Second shoot after getting a cell hit
                boolean success = false;
                while (!success) {
                    int randNum = rand.nextInt(secondShootDir.size());
                    int randDir = secondShootDir.get(randNum);
                    int firstX = firstHit.getX();
                    int firstY = firstHit.getY();
                    switch (randDir) {
                        case 0: //Shoot the cell to the top of firstHit
                            if (!opponentBoard.isOutOfBounds(firstX, firstY - 1) && !opponentBoard.getCell(firstX, firstY - 1).isHit()) {
                                x = firstX;
                                y = firstY - 1;
                                break;
                            } else {
                                secondShootDir.remove(Integer.valueOf(randDir));
                                continue;
                            }
                        case 1: //Shoot the cell to the bottom of firstHit
                            if (!opponentBoard.isOutOfBounds(firstX, firstY + 1) && !opponentBoard.getCell(firstX, firstY + 1).isHit()) {
                                x = firstX;
                                y = firstY + 1;
                                break;
                            } else {
                                secondShootDir.remove(Integer.valueOf(randDir));
                                continue;
                            }
                        case 2: //Shoot the cell to the left of firstHit
                            if (!opponentBoard.isOutOfBounds(firstX - 1, firstY) && !opponentBoard.getCell(firstX - 1, firstY).isHit()) {
                                x = firstX - 1;
                                y = firstY;
                                break;
                            } else {
                                secondShootDir.remove(Integer.valueOf(randDir));
                                continue;
                            }
                        case 3: //Shoot the cell to the right of firstHit
                            if (!opponentBoard.isOutOfBounds(firstX + 1, firstY) && !opponentBoard.getCell(firstX + 1, firstY).isHit()) {
                                x = firstX + 1;
                                y = firstY;
                                break;
                            } else {
                                secondShootDir.remove(Integer.valueOf(randDir));
                                continue;
                            }
                        default:
                            continue;
                    }
                    success = true;
                }
            } else {
                //Hit two cells in a row
                //Shoot the cell next to nextHit
                int firstX = firstHit.getX();
                int firstY = firstHit.getY();
                int nextX = nextHit.getX();
                int nextY = nextHit.getY();
                if (nextY == firstY) { //Shooting horizontally
                    y = nextY;
                    if (nextX > firstX) { //Shooting to the right
                        if (!opponentBoard.isOutOfBounds(nextX + 1, y) && !opponentBoard.getCell(nextX + 1, y).isHit()) {
                            x = nextX + 1;
                        } else {
                            x = firstX - 1;
                        }
                    } else { //Shooting to the left
                        if (!opponentBoard.isOutOfBounds(nextX - 1, y) && !opponentBoard.getCell(nextX - 1, y).isHit()) {
                            x = nextX - 1;
                        } else {
                            x = firstX + 1;
                        }
                    }
                }
                if (nextX == firstX) { //Shooting vertically
                    x = nextX;
                    if (nextY > firstY) { //Shooting to the bottom
                        if (!opponentBoard.isOutOfBounds(x, nextY + 1) && !opponentBoard.getCell(x, nextY + 1).isHit()) {
                            y = nextY + 1;
                        } else {
                            y = firstY - 1;
                        }
                    } else { //Shooting to the top
                        if (!opponentBoard.isOutOfBounds(x, nextY - 1) && !opponentBoard.getCell(x, nextY - 1).isHit()) {
                            y = nextY - 1;
                        } else {
                            y = firstY + 1;
                        }
                    }
                }
            }
            myApp.playSoundEffect(MyApp.SOUND_ID_FIRE);
            lastShootCell = opponentBoard.getCell(x, y);
            String cellLocation = x + "," + y;
            sendMessage(cellLocation);
            shootSuccessfully = true;
        }
    }

    @Override
    void _wait() {}

    @Override
    void endGame(String result) {
        isPlaying = false;
    }

    @Override
    void displayMessage(String message, String result) {}

    @Override
    void processShootResult(String shipName) {
        // ship being hit by last shot, null if result is MISS
        Ship hitShip = opponentBoard.getShip(shipName);
        lastShootCell.hit(hitShip);
        if (hitShip != null) { // Not a MISS
            // shoot result is either KILL or HIT
            // result is KILL if the ship was sunk, HIT if it was not
            String result = hitShip.isSunk() ? KILL : HIT;
            if (result.equals(KILL)) { //Case Kill
                //Got a kill, shoot randomly next time
                firstHit = null;
                nextHit = null;
            } else { //Case Hit
                //Set condition for next shoot
                if (firstHit == null) { //Got first hit on a ship
                    // set the first hit to prepare for next shoot
                    firstHit = lastShootCell;
                    // directions to find the remaining cells of the ship
                    secondShootDir = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
                } else {
                    // set the next hit to prepare for next shoot
                    nextHit = lastShootCell;
                }
            }
        }
        lastShootCell = null;
    }
}
