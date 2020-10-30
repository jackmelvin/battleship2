package com.jm.battleship;

import java.io.Serializable;
import java.util.*;

import static com.jm.battleship.GameManager.*;

public class Board implements Serializable {
	// Board is constructed of SIZE * SIZE cells
	public static final int SIZE = 10;
	public static final int NUMBER_OF_SHIPS = 5;
	private Cell[][] cells;
	// Holds all ships that a player has
	private ArrayList<Ship> ships;

	// Initialize cells and ships on creating object
	public Board() {
		init();
	}

	// Getters and Setters

	public ArrayList<Ship> getShips() {
		return ships;
	}

	private void init() {
		cells = new Cell[SIZE][SIZE];
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				cells[x][y] = new Cell(x, y);
			}
		}
		ships = new ArrayList<>();
		ships.add(0, new Ship(0, CARRIER, 5));
		ships.add(1, new Ship(1, BATTLESHIP, 4));
		ships.add(2, new Ship(2, CRUISER, 3));
		ships.add(3, new Ship(3, SUBMARINE, 3));
		ships.add(4, new Ship(4, DESTROYER, 2));
	}

	public Cell getCell(int x, int y) {
		return cells[x][y];
	}

	boolean isOutOfBounds(int x, int y) {
		return (x < 0 || x >= SIZE || y < 0 || y >= SIZE);
	}

	public boolean allShipPlaced() {
		for (Ship ship : ships) {
			if (!ship.isPlaced()) {
				return false;
			}
		}
		return true;
	}

	public boolean placeShip(Ship ship, Cell headCell) {
		ArrayList<Cell> location = new ArrayList<>();
		int x = headCell.getX();
		int y = headCell.getY();
		for (int i = 0; i < ship.getSize(); i++) {
			if (ship.isHorizontal()) {
				if (isOutOfBounds(x + i, y)) {
					return false;
				}
				if (cells[x + i][y].hasShip()) {
					return false;
				}
				location.add(cells[x + i][y]);
			} else {
				if (isOutOfBounds(x, y + i)) {
					return false;
				}
				if (cells[x][y + i].hasShip()) {
					return false;
				}
				location.add(cells[x][y + i]);
			}
		}
		ship.place(location);
		return true;
	}

	public boolean rotateShip(Ship ship) {
		if (!ship.isPlaced()) {
			return false;
		}
		Cell headCell = ship.getHeadCell();
		ship.setHorizontal(!ship.isHorizontal());
		ship.remove();
		if (!placeShip(ship, headCell)) {
			ship.setHorizontal(!ship.isHorizontal());
			placeShip(ship, headCell);
			return false;
		}
		return true;
	}

	public void placeShipsRandomly() {
		Random rand = new Random();
		for (Ship ship : ships) {
			ship.remove();
			boolean success = false;
			while (!success) {
				ship.setHorizontal(rand.nextBoolean());
				int headX;
				int headY;
				if (ship.isHorizontal()) {
					headX = rand.nextInt(SIZE - ship.getSize() + 1);
					headY = rand.nextInt(SIZE);
				} else {
					headX = rand.nextInt(SIZE);
					headY = rand.nextInt(SIZE - ship.getSize() + 1);
				}
				success = placeShip(ship, cells[headX][headY]);
				if (success) {
					System.out.print("Ship " + ship.getId() + " placed at ");
					for (Cell cell : ship.getLocation()) {
						System.out.print(cell.getX() + "," + cell.getY() + "  ");
					}
					System.out.println("");
				}
			}
		}
	}

	public String checkYourself(int x, int y) {
        String result;
        Cell cell = cells[x][y];
        result = cell.checkYourself();
        if (result.equals(HIT) || result.equals(KILL)) {
        	if (areAllShipsSunk()) {
        		result = LOSE;
			} else {
        		result = cell.getShip().getName();
			}
		}
        return result;
    }

    private boolean areAllShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

	public Ship getShip(String name) {
		int index;
		switch (name) {
			case CARRIER:
				index = 0;
				break;
			case BATTLESHIP:
				index = 1;
				break;
			case CRUISER:
				index = 2;
				break;
			case SUBMARINE:
				index = 3;
				break;
			case DESTROYER:
				index = 4;
				break;
			default:
				index = -1;
		}
		if (index != -1) {
			return ships.get(index);
		}
		return null;
	}

	public Ship getShip(int index) {
		return ships.get(index);
	}
}