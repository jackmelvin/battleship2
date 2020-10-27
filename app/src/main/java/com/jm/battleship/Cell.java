package com.jm.battleship;
import java.io.Serializable;

import static com.jm.battleship.GamePlayActivity.GameManager.*;

public class Cell implements Serializable {

	// Column coordinate on the board, 0-based
	private int x;
	// Row coordinate on the board, 0-based
	private int y;
	// Boolean indicating if the cell is hit or not
	private boolean isHit;
	// Cell can have only one ship, if it doesn't have one then ship is null
	private Ship ship;
	// Initialize a cell with coordinates
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	// Getters and Setters
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isHit() {
		return isHit;
	}

	public Ship getShip() {
		return ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}

	public boolean hasShip() {
		return !(ship == null);
	}

	// Check a shoot
	// Return HIT if there is a ship and MISS if there isn't
	public String checkYourself() {
		String result;
		hit();
		if (!hasShip()) {
			result = MISS;
		} else {
			if (ship.isSunk()) {
				result = KILL;
			} else {
				result = HIT;
			}
		}
		return result;
	}

	public void removeShip() {
		ship = null;
	}

	//For printing enemy board
	public void hit() {
		isHit = true;
		if (hasShip()) {
			ship.addHitCell(this);
		}
	}
	//For printing enemy board
	public void hit(Ship s) {
		isHit = true;
		if (!hasShip()) {
			ship = s;
		}
		ship.addHitCell(this);
	}
}