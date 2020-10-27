package com.jm.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PlaceShipActivity extends AppCompatActivity {

    private ImageView[] ivShips;
    private Ship selectedShip;
    private boolean isDragging;
    private Cell selectedShipOriginalLocation;
    private Board board;
    private BoardView boardView;
    private Toast toast;
    private float cellSize;
    private Button btStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_ship);

        init();
    }

    private void init() {
        // find and attach components
        ivShips = new ImageView[5];
        ivShips[0] = findViewById(R.id.iv_carrier);
        ivShips[1] = findViewById(R.id.iv_battleship);
        ivShips[2] = findViewById(R.id.iv_cruiser);
        ivShips[3] = findViewById(R.id.iv_submarine);
        ivShips[4] = findViewById(R.id.iv_destroyer);
        Button btRotate = findViewById(R.id.bt_rotate);
        Button btRandom = findViewById(R.id.bt_random);
        btStart = findViewById(R.id.bt_start);
        boardView = findViewById(R.id.board);
        // set boardView a new clean board to start
        boardView.setBoard(new Board());
        // set on drag listener for ships drag and drop
        boardView.setOnDragListener(new BoardOnDragListener());
        // init instance variable board
        board = boardView.getBoard();

        // Resize components according to board's cell size
        // after the board has been completely constructed
        boardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cellSize = boardView.cellSize();
                // resize ships
                for (int i = 0; i < ivShips.length; i++) {
                    // Resize
                    int width = (int) (cellSize * board.getShip(i).getSize());
                    int height = (int) cellSize;
                    resize(ivShips[i], width, height);
                    // Set touch listener
                    ivShips[i].setOnTouchListener(new ShipTouchListener(i));
                }
                // resize buttons
                int buttonWidth = (int) (3 * cellSize);
                int buttonHeight = (int) cellSize;
                resize(btRandom, buttonWidth, buttonHeight);
                resize(btRotate, buttonWidth, buttonHeight);
            }
            private void resize(View view, int width, int height) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = width;
                params.height = height;
                view.setLayoutParams(params);
            }
        });

        // do nothing when a ship is dropped outside of the board
        findViewById(R.id.screen).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                if(event.getAction() == DragEvent.ACTION_DROP) {
                    View v = (View) event.getLocalState();
                    v.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        btStart.setOnClickListener(new StartListener());
        btRotate.setOnClickListener(new RotateListener());
        btRandom.setOnClickListener(new RandomListener());
    }

    private void displayMessage(CharSequence message) {
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(PlaceShipActivity.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class ShipTouchListener implements View.OnTouchListener {
        private final int id;

        ShipTouchListener(int id) {
            this.id = id;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View touchedShip, MotionEvent touchEvent) {
            int action = touchEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN && !isDragging) {
                selectedShip = board.getShip(id);
                if (selectedShip.isPlaced()) {
                    selectedShipOriginalLocation = selectedShip.getHeadCell();
                    selectedShip.remove();
                }
                ClipData clipData = ClipData.newPlainText("", "");
                View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(touchedShip);
                touchedShip.startDrag(clipData, dragShadowBuilder, touchedShip, 0);
                touchedShip.setVisibility(View.INVISIBLE);
                isDragging = true;
                return true;
            } else {
                return false;
            }
        }
    }

    private class BoardOnDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            BoardView boardView = (BoardView) v;
            Cell headCell;
            float cellX, cellY;
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //Do nothing
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //Draw ship shadow
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //Remove ship shadow
                    break;
                case DragEvent.ACTION_DROP:
                    if(selectedShip.isHorizontal()) { //Horizontal ship
                        cellX = event.getX() - ((float)selectedShip.getSize() / 2.0f - 0.5f) * cellSize;
                        if(cellX < 0) {
                            cellX = 0;
                        }
                        cellY = event.getY();
                    } else { //Vertical ship
                        cellX = event.getX();
                        cellY = event.getY() - ((float)selectedShip.getSize() / 2.0f - 0.5f) * cellSize;
                        if(cellY < 0) {
                            cellY = 0;
                        }
                    }
                    headCell = boardView.locateCell(cellX, cellY);

                    View ivShip = (View) event.getLocalState();

                    if(headCell != null && ivShip != null && board.placeShip(selectedShip, headCell)) {
                        //Drop the ship on the boardView
                        ViewGroup.LayoutParams originalParams = ivShip.getLayoutParams();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(originalParams.width, originalParams.height);
                        //Remove original view
                        ViewGroup owner = (ViewGroup) ivShip.getParent();
                        owner.removeView(ivShip);
                        //Add view to boardView
                        params.leftMargin = (int) (headCell.getX() * cellSize);
                        params.topMargin = (int) (headCell.getY() * cellSize);
                        boardView.addView(ivShip, params);
                        ivShips[selectedShip.getId()] = (ImageView) ivShip;
                        ivShip.setVisibility(View.VISIBLE);
                    } else {
                        //Can't place ship
                        //Display warning message
                        displayMessage("Can't place ship");
                        //Place ship at original place
                        if (selectedShip.isPlaced()) {
                            board.placeShip(selectedShip, selectedShipOriginalLocation);
                        }
                        if (ivShip != null) {
                            ivShip.setVisibility(View.VISIBLE);
                        }
                    }
                    if (board.allShipPlaced()) {
                        btStart.setVisibility(View.VISIBLE);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    isDragging = false;
                    break;
            }
            return true;
        }
    }

    private class RotateListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //myApp.playSoundEffect(MyApp.SOUND_ID_PLACE);
            if(selectedShip != null && selectedShip.isPlaced()) {
                if (board.rotateShip(selectedShip)) {
                    ImageView ivShip = ivShips[selectedShip.getId()];
                    //Resize ship view to match ship's direction
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivShip.getLayoutParams();
                    int temp = params.height;
                    params.height = params.width;
                    params.width = temp;
                    ivShip.setLayoutParams(params);
                    //Set rotated image to ship view
                    ivShips[selectedShip.getId()].setImageResource(selectedShip.getImageId());
                } else {
                    //Can't rotate ship, blocked
                    //Display warning message
                    displayMessage("Can't place ship");
                }
            } else {
                //No ship selected or selected ship hasn't been placed on board
                //Display warning message
                displayMessage("No ship selected or selected ship hasn't been placed on board");
            }
        }
    }

    private class RandomListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            myApp.playSoundEffect(MyApp.SOUND_ID_PLACE);
            // Remove all placed ship
            for (Ship ship : board.getShips()) {
                ship.remove();
            }
            // Deselect ship
            selectedShip = null;
            // Place ships randomly
            board.placeShipRandomly();

            //Move ship views into BoardView
            for(int i = 0; i < board.getShips().size(); i++) {
                //Remove original view
                ViewGroup owner = (ViewGroup) ivShips[i].getParent();
                owner.removeView(ivShips[i]);
                //Add view to boardView

                Ship ship = board.getShip(i);
                Cell headCell = ship.getHeadCell();
                RelativeLayout.LayoutParams params;
                if(ship.isHorizontal()) {
                    params = new RelativeLayout.LayoutParams((int)(cellSize * ship.getSize()), (int)cellSize);
                } else {
                    params = new RelativeLayout.LayoutParams((int)cellSize, (int)(cellSize * ship.getSize()));
                }
                ivShips[i].setImageResource(board.getShip(i).getImageId());
                params.leftMargin = (int)(headCell.getX() * cellSize);
                params.topMargin = (int)(headCell.getY() * cellSize);
                ivShips[i].setLayoutParams(params);
                boardView.addView(ivShips[i], params);
                // display start button
                btStart.setVisibility(View.VISIBLE);
            }
        }
    }

    private class StartListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PlaceShipActivity.this, GamePlayActivity.class);
            intent.putExtra("board", board);
            startActivity(intent);
        }
    }
}