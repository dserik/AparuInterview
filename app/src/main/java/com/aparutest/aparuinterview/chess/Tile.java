package com.aparutest.aparuinterview.chess;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public final class Tile {
    private static final String TAG = Tile.class.getSimpleName();

    private final int col;
    private final int row;

    private final Paint squareColor;
    private Rect tileRect;

    private static final Paint LIGHT_SQUARE_COLOR;
    private static final Paint DARK_SQUARE_COLOR;

    static {
        LIGHT_SQUARE_COLOR = new Paint();
        LIGHT_SQUARE_COLOR.setColor(Color.parseColor("#F0D9B5"));
        DARK_SQUARE_COLOR = new Paint();
        DARK_SQUARE_COLOR.setColor(Color.parseColor("#B58863"));
    }

    public Tile(final int col, final int row) {
        this.col = col;
        this.row = row;
        this.squareColor = isDark() ? DARK_SQUARE_COLOR : LIGHT_SQUARE_COLOR;
    }

    public void draw(final Canvas canvas) {
        canvas.drawRect(tileRect, squareColor);
    }

    public void draw(final Canvas canvas, Paint color) {
        canvas.drawRect(tileRect, color);
    }



    public void handleTouch() {
        Log.d(TAG, "handleTouch(): col: " + col);
        Log.d(TAG, "handleTouch(): row: " + row);
    }

    public boolean isDark() {
        return (col + row) % 2 == 0;
    }

    public boolean isTouched(final int x, final int y) {
        return tileRect.contains(x, y);
    }

    public void setTileRect(final Rect tileRect) {
        this.tileRect = tileRect;
    }

    public Rect getTileRect() {
        return this.tileRect;
    }

    public Paint getSquareColor() {
        return squareColor;
    }
}
