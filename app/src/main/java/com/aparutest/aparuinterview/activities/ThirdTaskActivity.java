package com.aparutest.aparuinterview.activities;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aparutest.aparuinterview.R;
import com.aparutest.aparuinterview.chess.ChessBoard;
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
    private ChessBoard chessBoard;

    private final Point startPoint = new Point();
    private final Point endPoint = new Point();
    private final Map<Integer, Point> clippedSquares = new HashMap<>();

    List<Integer> visited = new ArrayList<>();
    Queue<Stack<Integer>> coordQueue = new ArrayDeque<>();

    private void init() {
        setEndpointPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                Point selectedPoint = new Point(col, row);
                // avoid to mark cutted square
                if (clippedSquares.containsKey(col * 8 + row))
                    return;

                chessBoard.markSquare(col, row, ChessBoard.GREEN);

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
        };

        setClippedPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                Point selectedPoint = new Point(col, row);
                if(startPoint.equals(selectedPoint) || endPoint.equals(selectedPoint)) return;

                chessBoard.markSquare(col, row, ChessBoard.GRAY);
                int key = col * 8 + row;
                if (clippedSquares.containsKey(key)) {
                    clippedSquares.remove(key);
                } else {
                    clippedSquares.put(key, selectedPoint);
                }
            }
        };

        setKnightPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                Point selectedPoint = new Point(col, row);

                // avoid to mark cutted square
                if (clippedSquares.containsKey(col * 8 + row))
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
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_task);

        chessBoard = findViewById(R.id.chessBoard);
        init();
        refreshThird(null);

        chessBoard.setSquareTouchEvent(new SquareTouchedAction() {
            @Override
            public void run(int col, int row) {
                RadioGroup radioGroup = findViewById(R.id.rgOperations);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbStartPos:
                        setKnightPosition.run(col, row); break;
                    case R.id.rbEndPos:
                        setEndpointPosition.run(col, row); break;
                    case R.id.rbCut:
                        setClippedPosition.run(col, row);
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

        int startPointCoord = startPoint.x * 8 + startPoint.y;
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
                .append(ChessBoard.getSquareCoordinate(point / 8, point % 8));

        while (!result.isEmpty()) {
            point = result.pop();
            chessBoard.markSquare(point / 8, point % 8, ChessBoard.GREEN);
            chessBoard.invalidate();
            routeString.insert(0, " => ")
                    .insert(0, ChessBoard.getSquareCoordinate(point / 8, point % 8));
        }

        res.setText(routeString.toString());
    }

    public Stack<Integer> route(Stack<Integer> routeStack) {
        Integer fromIndex = routeStack.peek();
        Point fromPoint = new Point(fromIndex / 8, fromIndex % 8);
        List<Point> possibleMoves = Piece.W_KNIGHT.getPossibleMoves(fromPoint, ChessBoard.COLS, ChessBoard.ROWS);

        for (Point possibleMovePoint : possibleMoves) {
            final int point = possibleMovePoint.x * 8 + possibleMovePoint.y;
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
}
