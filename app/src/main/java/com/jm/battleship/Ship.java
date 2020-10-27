package com.jm.battleship;

import java.io.Serializable;
import java.util.*;

class Ship implements Serializable {
    // The ship's id
    private final int id;
	// The ship's length, can't be changed after initialization
	private final int size;
	// The ship's name, can't be changed after initialization
	private final String name;
	// The location cells where the ship is placed on
	private ArrayList<Cell> location;
	// Boolean indicates the ship's direction
	// default is horizontal
	private boolean isHorizontal = true;
	// Cells that have been hit
    // if number of hit cells == ship's size then ship is sunk
    private final ArrayList<Cell> hitCells = new ArrayList<>();
    // Boolean indicates the ship's visibility
    private boolean isVisible = true;

    void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    // Initialize name and size
	public Ship(int id, String name, int size) {
        this.id = id;
		this.name = name;
		this.size = size;
	}
	// Getters

    public int getId() {
        return id;
    }

	public int getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	public boolean isHorizontal() {
		return isHorizontal;
	}

    public boolean isVisible() {
        return isVisible;
    }

    public ArrayList<Cell> getLocation() {
        return location;
    }

    public Cell getHeadCell() {
        return location.get(0);
    }

    public boolean isPlaced() {
	    return location != null;
    }

    public void remove() {
        if(!isPlaced()) return;
        //Remove ship from cells placed on
        for(Cell cell : location) {
            cell.removeShip();
        }
        location = null;
    }

    public void addHitCell(Cell cell) {
	    hitCells.add(cell);
    }

    public int getImageId() {
        int imageId = 0;
        if (isHorizontal) {
            switch (name) {
                case "Carrier":
                    imageId = R.drawable.ship_carrier_00;
                    break;
                case "Battleship":
                    imageId = R.drawable.ship_battleship_00;
                    break;
                case "Cruiser":
                    imageId = R.drawable.ship_cruiser_00;
                    break;
                case "Submarine":
                    imageId = R.drawable.ship_submarine_00;
                    break;
                case "Destroyer":
                    imageId = R.drawable.ship_destroyer_00;
                    break;
            }
        } else {
            switch (name) {
                case "Carrier":
                    imageId = R.drawable.ship_carrier_01;
                    break;
                case "Battleship":
                    imageId = R.drawable.ship_battleship_01;
                    break;
                case "Cruiser":
                    imageId = R.drawable.ship_cruiser_01;
                    break;
                case "Submarine":
                    imageId = R.drawable.ship_submarine_01;
                    break;
                case "Destroyer":
                    imageId = R.drawable.ship_destroyer_01;
                    break;
            }
        }
        return imageId;
    }

	// Set location cells where the ship is placed on
	// and set the ship for each cell
	public void place(ArrayList<Cell> loc) {
		location = loc;
		for (Cell cell : location) {
			cell.setShip(this);
		}
	}

	// Check if the ship has been sunk or not
    public boolean isSunk() {
        if (hitCells.size() == size) {
            if (!isVisible) { // opponent's ship that has not been sunk yet
                // place the ship on board to display it
                isVisible = true;
                Collections.sort(hitCells, new CompareCell());
                place(hitCells);
                if(hitCells.get(0).getX() == hitCells.get(1).getX()) {
                    // two cells are on the same column
                    // the ship must be vertical
                    isHorizontal = false;
                }
            }
            return true;
        }
        return false;
	}

	private static class CompareCell implements Comparator<Cell> {
        @Override
        public int compare(Cell c1, Cell c2) {
            // if two cells are in the same column then c1.getX() - c2.getX() == 0
            // if two cells are in the same row then c1.getY() - c2.getY() == 0
            return c1.getX() - c2.getX() + c1.getY() - c2.getY();
        }
    }
}