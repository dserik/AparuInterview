package com.aparutest.aparuinterview.chess;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;


public final class Board extends View {

    public static int COLS = 8;
    public static int ROWS = 8;

    private Tile[][] mTiles;
    private Map<Integer, DrawCollectionEntry> drawMarksCollection = new HashMap<>();
    private Map<Integer, DrawCollectionEntry> drawPiecesCollection = new HashMap<>();

    private int x0 = 0;
    private int y0 = 0;
    private int squareSize = 0;

    private SquareTouchedAction squareTouchEvent;


    /** 'true' if black is facing player. */
    private boolean flipped = false;

    public static final Paint GREEN;
    public static final Paint GRAY;
    public static final Paint RED;
    static {
        GREEN = new Paint();
        GREEN.setColor(Color.parseColor("#AAA23A"));
        GREEN.setAlpha(180);
        GRAY = new Paint();
        GRAY.setColor(Color.parseColor("#999999"));
        GRAY.setAlpha(180);
        RED = new Paint();
        RED.setColor(Color.parseColor("#FF0000"));
        RED.setAlpha(100);
    }

    public Board(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.mTiles = new Tile[COLS][ROWS];

        setFocusable(true);

        buildTiles();
    }

    private void buildTiles() {
        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS; r++) {
                mTiles[c][r] = new Tile(c, r);
            }
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();

        this.squareSize = Math.min(
                getSquareSizeWidth(width),
                getSquareSizeHeight(height)
        );

        computeOrigins(width, height);

        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS; r++) {
                final int xCoord = getXCoord(c);
                final int yCoord = getYCoord(r);

                final Rect tileRect = new Rect(
                        xCoord,               // left
                        yCoord,               // top
                        xCoord + squareSize,  // right
                        yCoord + squareSize   // bottom
                );

                mTiles[c][r].setTileRect(tileRect);
                mTiles[c][r].draw(canvas);
            }
        }

        drawUserActions(canvas);
    }

    private void drawUserActions(final Canvas canvas) {
        for (Map.Entry<Integer, DrawCollectionEntry> drawEntry : drawMarksCollection.entrySet()) {
            DrawCollectionEntry entry = drawEntry.getValue();
            entry.draw(canvas);
        }

        for (Map.Entry<Integer, DrawCollectionEntry> drawEntry : drawPiecesCollection.entrySet()) {
            DrawCollectionEntry entry = drawEntry.getValue();
            entry.draw(canvas);
        }
    }

    public void putPiece(Piece piece, int col, int row) {
        int key = col * COLS + row;
        if(drawPiecesCollection.containsKey(key)) {
            drawPiecesCollection.remove(key);
            return;
        }

        Tile tile = mTiles[col][row];
        final Drawable drawable = getResources().getDrawable(piece.id);
        drawable.setBounds(tile.getTileRect());

        DrawCollectionEntry drawPieceEntry = new DrawCollectionEntry() {
            public void draw(Canvas canvas) {
                drawable.draw(canvas);
            }
        };
        drawPiecesCollection.put(col * COLS + row, drawPieceEntry);
    }

    public void markSquare(final int col, final int row, Paint color) {
        int key = col * COLS + row;
        boolean unmark = drawMarksCollection.containsKey(key);
        final Paint squareColor = unmark
                ? mTiles[col][row].getSquareColor()
                : color;

        DrawCollectionEntry drawMarkEntry = new DrawCollectionEntry() {
            public void draw(Canvas canvas) {
                canvas.drawRect(mTiles[col][row].getTileRect(), squareColor);
            }
        };

        if (unmark) {
            drawMarksCollection.remove(key);
        } else {
            drawMarksCollection.put(key, drawMarkEntry);
        }
    }

    public void refresh() {
        drawPiecesCollection.clear();
        drawMarksCollection.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        final int x = (int) event.getX();
        final int y = (int) event.getY();

        Tile tile;
        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS; r++) {
                tile = mTiles[c][r];
                if (tile.isTouched(x, y)) {
                    performTouch(c, r);
                    invalidate();
                    return true;
                }
            }
        }

        return true;
    }

    private void performTouch(int col, int row) {
        if (squareTouchEvent != null) {
            squareTouchEvent.perform(col, row);
        }
    }

    public void setSquareTouchEvent(SquareTouchedAction squareTouchEvent) {
        this.squareTouchEvent = squareTouchEvent;
    }

    public void setDimension(int cols, int rows) {
        COLS = cols;
        ROWS = rows;
        mTiles = new Tile[COLS][ROWS];
        buildTiles();
        refresh();
    }

    private int getSquareSizeWidth(final int width) {
        return width / COLS;
    }

    private int getSquareSizeHeight(final int height) {
        return height / ROWS;
    }

    private int getXCoord(final int x) {
        return x0 + squareSize * (flipped ? (COLS - 1) - x : x);
    }

    private int getYCoord(final int y) {
        return y0 + squareSize * (flipped ? y : (ROWS - 1) - y);
    }

    private void computeOrigins(final int width, final int height) {
        this.x0 = (width  - squareSize * COLS) / 2;
        this.y0 = (height - squareSize * ROWS) / 2;
    }

    public static String getSquareCoordinate(int col, int row) {
        return new String(new char[]{
                (char)('A' + col),
                (char)('1' + row)});
    }

    private interface DrawCollectionEntry {
        void draw(Canvas canvas);
    }
}
