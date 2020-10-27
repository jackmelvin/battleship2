package com.jm.battleship;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class BoardView extends RelativeLayout {

//	/** Callback interface to listen for board touches. */
//    public interface BoardTouchListener {
//
//        /**
//         * Called when a cell of the board is touched.
//         */
//        void onTouch(Cell cell);
//    }
	// The canvas to draw things on
	Canvas mCanvas;
    // The paint to draw board grid
	Paint boardLinePaint;
    // Actual board to draw with this view
    Board board;
	// Don't draw until game starts
	boolean readyToDraw = false;

	public BoardView(Context context) {
		super(context);
		initializePaint();
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializePaint();
	}

	public void setReadyToDraw(boolean readyToDraw) {
		this.readyToDraw = readyToDraw;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		mCanvas = canvas;
		drawGrid();
		drawShips();
		drawHitCells();
	}

	private void initializePaint() {
		boardLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		boardLinePaint.setColor(Color.BLUE);
		boardLinePaint.setStyle(Paint.Style.STROKE);
		boardLinePaint.setStrokeWidth(3);
	}

	// Set the board to be displayed by this view
	public void setBoard(Board board) {
		this.board = board;
	}

	private void drawGrid() {
		for (int i = 0; i <= Board.SIZE; i++) {
			// Draw vertical lines
			mCanvas.drawLine(i * cellSize(), 0, i * cellSize(), Board.SIZE * cellSize(), boardLinePaint);
			// Draw horizontal lines
			mCanvas.drawLine(0, i * cellSize(), Board.SIZE * cellSize(), i * cellSize(), boardLinePaint);
		}
	}

	private void drawShips() {
    	if (!readyToDraw || board == null) {
    		return;
		}
		for (Ship ship : board.getShips()) {
			if (ship.isPlaced() && ship.isVisible()) {
				Cell headCell = ship.getLocation().get(0);
				float left = headCell.getX() * cellSize();
				float top = headCell.getY() * cellSize();
				int width, height;
				if (ship.isHorizontal()) {
					width = (int) (ship.getSize() * cellSize());
					height = (int) cellSize();
				} else {
					width = (int) cellSize();
					height = (int) (ship.getSize() * cellSize());
				}
				mDrawBitmap(ship.getImageId(), left, top, width, height);
			}
		}
	}

	private void drawHitCells() {
		if (!readyToDraw || board == null) {
			return;
		}
		for (int x = 0; x < Board.SIZE; x++) {
			for (int y = 0; y < Board.SIZE; y++) {
				Cell cell = board.getCell(x, y);
				if (cell.isHit()) {
					float left = cell.getX() * cellSize();
					float top = cell.getY() * cellSize();
					if (cell.hasShip()) {
						mDrawBitmap(R.drawable.user_hit, left, top);
					} else {
						mDrawBitmap(R.drawable.cell_miss, left, top);
					}
				}
			}
		}
	}

	private void mDrawBitmap(int imageId, float left, float top) {
    	mDrawBitmap(imageId, left, top, (int) cellSize(), (int) cellSize());
	}

	private void mDrawBitmap(int imageId, float left, float top, int width, int height) {
		Bitmap original = BitmapFactory.decodeResource(getResources(), imageId);
		Bitmap bitmapToDraw = Bitmap.createScaledBitmap(original, width, height, true);
		mCanvas.drawBitmap(bitmapToDraw, left, top, null);
	}

    float cellSize() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) Board.SIZE;
    }

    Cell locateCell(float x, float y) {
    	if (board == null) {
    		return null;
		}
        return board.getCell((int)(x / cellSize()), (int)(y / cellSize()));
    }

	public Board getBoard() {
		return board;
	}
}