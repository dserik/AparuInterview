package com.aparutest.aparuinterview.activities;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aparutest.aparuinterview.R;
import com.aparutest.aparuinterview.chess.Board;
import com.aparutest.aparuinterview.chess.Piece;
import com.aparutest.aparuinterview.chess.SquareTouchedAction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class ThirdTaskActivity extends AppCompatActivity {

    private SquareTouchedAction setKnightPosition;
    private SquareTouchedAction setEndpointPosition;
    private SquareTouchedAction setClippedPosition;
    private Board chessBoard;

    private int COLS = 8, ROWS = 8;

    private final Point startPoint = new Point();
    private final Point endPoint = new Point();
    private final Map<Integer, Point> clippedSquares = new HashMap<>();

    List<Integer> visited = new ArrayList<>();
    Queue<Stack<Integer>> coordQueue = new ArrayDeque<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_task);

        chessBoard = findViewById(R.id.chessBoard);
        chessBoard.setDimension(COLS, ROWS);
        setEndpointPosition = new SetEndpointOnTouchEvent();
        setClippedPosition = new SetClippedSquareOnTouchEvent();
        setKnightPosition = new SetStartpointOnTouchEvent();
        refreshThird(null);

        chessBoard.setSquareTouchEvent(new SquareTouchedAction() {
            @Override
            public void perform(int col, int row) {
                RadioGroup radioGroup = findViewById(R.id.rgOperations);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbStartPos:
                        setKnightPosition.perform(col, row); break;
                    case R.id.rbEndPos:
                        setEndpointPosition.perform(col, row); break;
                    case R.id.rbCut:
                        setClippedPosition.perform(col, row);
                }
            }
        });
    }

    public void resolveRoute(View view) {
        TextView res = findViewById(R.id.tvRoute);
        if(startPoint.x < 0 || endPoint.x < 0) {
            res.setText("Отсутствует начальная или конечная точка");
            return;
        }

        int startPointCoord = startPoint.x * COLS + startPoint.y;
        visited.add(startPointCoord);
        Stack<Integer> startPointStack = new Stack<>();
        startPointStack.push(startPointCoord);

        Stack<Integer> result = route(startPointStack);

        if (result == null) {
            res.setText("Маршрут не найден");
            return;
        }

        int point = result.pop();
        StringBuilder routeString = new StringBuilder()
                .append(Board.getSquareCoordinate(point / COLS, point % COLS));

        while (!result.isEmpty()) {
            point = result.pop();
            chessBoard.markSquare(point / COLS, point % COLS, Board.GREEN);
            chessBoard.invalidate();
            routeString.insert(0, " => ")
                    .insert(0, Board.getSquareCoordinate(point / COLS, point % COLS));
        }

        res.setText(routeString.toString());
    }

    public Stack<Integer> route(Stack<Integer> routeStack) {
        Integer fromIndex = routeStack.peek();
        Point fromPoint = new Point(fromIndex / COLS, fromIndex % COLS);
        List<Point> possibleMoves = Piece.W_KNIGHT.getPossibleMoves(fromPoint, Board.COLS, Board.ROWS);

        for (Point possibleMovePoint : possibleMoves) {
            final int point = possibleMovePoint.x * COLS + possibleMovePoint.y;
            if (endPoint.equals(possibleMovePoint)) {
                routeStack.push(point);
                return routeStack;
            }

            if (visited.contains(point) || clippedSquares.containsKey(point)) {
                continue;
            }

            Stack<Integer> newStack = (Stack<Integer>)routeStack.clone();
            newStack.push(point);
            coordQueue.add(newStack);
            visited.add(point);
        }

        Stack<Integer> next = coordQueue.poll();
        if (next == null) return null;
        return route(next);
    }

    public void refreshThird(View view) {
        startPoint.set(-1, -1);
        endPoint.set(-1, -1);
        clippedSquares.clear();
        chessBoard.refresh();
    }

    public void jumpToFourth(View view) {
        Intent intent = new Intent(this, FourthTaskActivity.class);
        startActivity(intent);
    }

    private class SetEndpointOnTouchEvent implements SquareTouchedAction {
        public void perform(int col, int row) {
            Point selectedPoint = new Point(col, row);
            // avoid to mark cutted square
            if (clippedSquares.containsKey(col * COLS + row))
                return;

            chessBoard.markSquare(col, row, Board.GREEN);

            // remove previous selection
            if(endPoint.x > -1 && !endPoint.equals(selectedPoint)) {
                chessBoard.markSquare(endPoint.x, endPoint.y, null);
            }

            if (endPoint.equals(selectedPoint)) {
                endPoint.set(-1, -1);
            } else {
                endPoint.set(col, row);
            }
        }
    }

    private class SetClippedSquareOnTouchEvent implements SquareTouchedAction {
        public void perform(int col, int row) {
            Point selectedPoint = new Point(col, row);
            if(startPoint.equals(selectedPoint) || endPoint.equals(selectedPoint)) return;

            chessBoard.markSquare(col, row, Board.GRAY);
            int key = col * COLS + row;
            if (clippedSquares.containsKey(key)) {
                clippedSquares.remove(key);
            } else {
                clippedSquares.put(key, selectedPoint);
            }
        }
    }

    private class SetStartpointOnTouchEvent implements SquareTouchedAction {
        public void perform(int col, int row) {
            Point selectedPoint = new Point(col, row);

            // avoid to mark cutted square
            if (clippedSquares.containsKey(col * COLS + row))
                return;

            //remove previous position from the board if not the same square was selected
            if (startPoint.x > -1 && !(selectedPoint.equals(startPoint))) {
                chessBoard.putPiece(Piece.W_KNIGHT, startPoint.x, startPoint.y);
            }

            chessBoard.putPiece(Piece.W_KNIGHT, col, row);
            if (startPoint.equals(selectedPoint)) {
                startPoint.set(-1, -1);
            } else {
                startPoint.set(col, row);
            }
        }
    }
}
