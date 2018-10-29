package com.aparutest.aparuinterview.activities;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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

public class FourthTaskActivity extends AppCompatActivity {

    private SquareTouchedAction setStartPosition;
    private SquareTouchedAction setEndpointPosition;
    private SquareTouchedAction setBallPosition;
    private ChessBoard chessBoard;

    private static int BOARD_COLS = 8, BOARD_ROWS = 8;

    private final Point startPoint = new Point();
    private final Point endPoint = new Point();
    private final Map<Integer, Point> ballsCollection = new HashMap<>();

    private void init() {
        setEndpointPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                Point selectedPoint = new Point(col, row);
                if (ballsCollection.containsKey(col * BOARD_COLS + row))
                    return;

                // remove previous selection
                if(endPoint.x > -1 && !endPoint.equals(selectedPoint)) {
                    chessBoard.markSquare(endPoint.x, endPoint.y, null);
                }

                chessBoard.markSquare(col, row, ChessBoard.RED);
                if (endPoint.equals(selectedPoint)) {
                    endPoint.set(-1, -1);
                } else {
                    endPoint.set(col, row);
                }
            }
        };

        setBallPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                Point selectedPoint = new Point(col, row);
                if(endPoint.equals(selectedPoint)) return;

                chessBoard.putPiece(Piece.BALL, col, row);
                int key = col * BOARD_COLS + row;
                if (ballsCollection.containsKey(key)) {
                    ballsCollection.remove(key);
                } else {
                    ballsCollection.put(key, selectedPoint);
                }
            }
        };

        setStartPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                Point selectedPoint = new Point(col, row);
                if (!ballsCollection.containsKey(col * BOARD_COLS + row))
                    return;

                // unmark previous selection
                if (startPoint.x > -1 && !(selectedPoint.equals(startPoint))) {
                    chessBoard.markSquare(startPoint.x, startPoint.y, ChessBoard.GREEN);
                }

                chessBoard.markSquare(col, row, ChessBoard.GREEN);
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
        setContentView(R.layout.activity_forth_task);

        chessBoard = findViewById(R.id.forthTaskChessBoard);
        chessBoard.setDimension(BOARD_COLS, BOARD_ROWS);
        init();
        refreshForth(null);

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeDimension(progress + 2);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        chessBoard.setSquareTouchEvent(new SquareTouchedAction() {
            @Override
            public void run(int col, int row) {
                RadioGroup radioGroup = findViewById(R.id.rgOperations);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbStartPos:
                        setStartPosition.run(col, row); break;
                    case R.id.rbEndPos:
                        setEndpointPosition.run(col, row); break;
                    case R.id.rbCut:
                        setBallPosition.run(col, row);
                }
            }
        });
    }

    public void changeDimension(int val) {
        BOARD_COLS = BOARD_ROWS = val;
        chessBoard.setDimension(BOARD_COLS, BOARD_ROWS);
    }

    public void resolveRoute(View view) {
        TextView res = findViewById(R.id.tvRoute);
        if(startPoint.x < 0 || endPoint.x < 0) {
            res.setText("Отсутствует начальная или конечная точка");
            return;
        }

        int startPointCoord = startPoint.x * BOARD_COLS + startPoint.y;
        visited.add(startPointCoord);
        Stack<Integer> startPointStack = new Stack<>();
        startPointStack.push(startPointCoord);

        Stack<Integer> result = route(startPointStack);
        coordQueue.clear();
        visited.clear();
        if (result == null) {
            res.setText("Маршрут не найден");
            return;
        }

        int point = result.pop();
        StringBuilder routeString = new StringBuilder()
                .append(ChessBoard.getSquareCoordinate(point / BOARD_COLS, point % BOARD_COLS));

        while (!result.isEmpty()) {
            point = result.pop();
            chessBoard.markSquare(point / BOARD_COLS, point % BOARD_COLS, ChessBoard.GREEN);
            chessBoard.invalidate();
            routeString.insert(0, " => ")
                    .insert(0, ChessBoard.getSquareCoordinate(point / BOARD_COLS, point % BOARD_COLS));
        }

        res.setText(routeString.toString());
    }

    List<Integer> visited = new ArrayList<>();
    Queue<Stack<Integer>> coordQueue = new ArrayDeque<>();

    public Stack<Integer> route(Stack<Integer> routeStack) {
        Integer fromIndex = routeStack.peek();
        Point fromPoint = new Point(fromIndex / BOARD_COLS, fromIndex % BOARD_COLS);
        List<Point> possibleMoves = Piece.BALL.getPossibleMoves(fromPoint, BOARD_COLS, BOARD_ROWS);

        for (Point possibleMovePoint : possibleMoves) {
            final int point = possibleMovePoint.x * BOARD_COLS + possibleMovePoint.y;
            if (endPoint.equals(possibleMovePoint)) {
                routeStack.push(point);
                return routeStack;
            }

            if (visited.contains(point) || ballsCollection.containsKey(point)) {
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

    public void refreshForth(View view) {
        startPoint.set(-1, -1);
        endPoint.set(-1, -1);
        ballsCollection.clear();
        chessBoard.refresh();
    }
}
