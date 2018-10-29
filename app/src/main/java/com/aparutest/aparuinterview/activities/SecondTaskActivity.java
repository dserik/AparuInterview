package com.aparutest.aparuinterview.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aparutest.aparuinterview.R;
import com.aparutest.aparuinterview.chess.Board;
import com.aparutest.aparuinterview.chess.Piece;
import com.aparutest.aparuinterview.chess.SquareTouchedAction;

import java.util.List;

public class SecondTaskActivity extends AppCompatActivity {

    private SquareTouchedAction setKnightPosition;
    private SquareTouchedAction setEndpointPosition;
    private Board chessBoard;

    private int COLS = 8, ROWS = 8;

    private final Point startPoint = new Point();
    private final Point endPoint = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_task);

        chessBoard = findViewById(R.id.chessBoard);
        chessBoard.setDimension(COLS, ROWS);
        setEndpointPosition = new SetEndpointOnTouchEvent();
        setKnightPosition = new SetStartpointOnTouchEvent();

        chessBoard.setSquareTouchEvent(setKnightPosition);
    }

    //no need to use graph expirience
    private String getAnswer() {
        String verdict = "NO";
        List<Point> firstPossibleMoves = Piece.W_KNIGHT.getPossibleMoves(startPoint, COLS, ROWS);
        for (Point firstMove : firstPossibleMoves) {
            if(firstMove.equals(endPoint)) {
                verdict = "1";
                break;
            }

            List<Point> secondPossibleMoves = Piece.W_KNIGHT.getPossibleMoves(firstMove, COLS, ROWS);
            for (Point secondMove : secondPossibleMoves) {
                if (secondMove.equals(endPoint)) {
                    verdict = "2";
                    break;
                }
            }
        }

        return Board.getSquareCoordinate(startPoint.x, startPoint.y) + " => "
                + Board.getSquareCoordinate(endPoint.x, endPoint.y) + "   " + verdict;
    }

    public void refresh(View view) {
        chessBoard.refresh();
        chessBoard.setSquareTouchEvent(setKnightPosition);
        TextView tvAnswer = findViewById(R.id.tvSecondTaskAnswer);
        tvAnswer.setText("");
    }

    public void jumpToThird(View view) {
        Intent intent = new Intent(this, ThirdTaskActivity.class);
        startActivity(intent);
    }

    private class SetEndpointOnTouchEvent implements SquareTouchedAction {
        public void perform(int col, int row) {
            chessBoard.markSquare(col, row, Board.GREEN);
            chessBoard.setSquareTouchEvent(null);
            endPoint.set(col, row);

            TextView tvAnswer = findViewById(R.id.tvSecondTaskAnswer);
            tvAnswer.setText(getAnswer());
        }
    }

    private class SetStartpointOnTouchEvent implements SquareTouchedAction {
        public void perform(int col, int row) {
            chessBoard.putPiece(Piece.W_KNIGHT, col, row);
            chessBoard.setSquareTouchEvent(setEndpointPosition);
            startPoint.set(col, row);
        }
    }
}
