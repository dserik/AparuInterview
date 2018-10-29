package com.aparutest.aparuinterview.chess;

import android.graphics.Point;

import com.aparutest.aparuinterview.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Piece {
    W_KNIGHT(R.drawable.knigt) {
        public List<Point> getPossibleMoves(Point from, int colCount, int rowCount) {
            List<Point> possiblePoints = new ArrayList<>();
            for (int col = -2; col <= 2; col++) {
                for (int row = -2; row <= 2; row++) {
                    if (Math.abs(col) + Math.abs(row) == 3
                            && from.x + col >= 0 && from.x + col < colCount
                            && from.y + row >= 0 && from.y + row < rowCount) {
                        possiblePoints.add(new Point(from.x + col, from.y + row));
                    }
                }
            }
            return possiblePoints;
        }
    },

    BALL(R.drawable.ball) {
        public List<Point> getPossibleMoves(Point from, int colCount, int rowCount) {
            List<Point> possiblePoints = new ArrayList<>();
            if(from.y != rowCount - 1) possiblePoints.add(new Point(from.x, from.y + 1));
            if(from.y != 0) possiblePoints.add(new Point(from.x, from.y - 1));
            if(from.x != colCount - 1) possiblePoints.add(new Point(from.x + 1, from.y));
            if(from.x != 0) possiblePoints.add(new Point(from.x - 1, from.y));
            return possiblePoints;
        }
    };

    int id;
    Piece(int id) {
        this.id = id;
    }

    public abstract List<Point> getPossibleMoves(Point from, int colCount, int rowCount);
}
