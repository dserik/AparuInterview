package com.aparutest.aparuinterview.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aparutest.aparuinterview.R;
import com.aparutest.aparuinterview.chess.ChessBoard;
import com.aparutest.aparuinterview.chess.Piece;
import com.aparutest.aparuinterview.chess.SquareTouchedAction;

public class SecondTaskActivity extends AppCompatActivity {

    private SquareTouchedAction setKnightPosition;
    private SquareTouchedAction setEndpointPosition;
    private ChessBoard chessBoard;


    private final Point startPoint = new Point();
    private final Point endPoint = new Point();

    private void init() {
        setEndpointPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                chessBoard.markSquare(col, row, ChessBoard.GREEN);
                chessBoard.setSquareTouchEvent(null);
                endPoint.set(col, row);

                TextView tvAnswer = findViewById(R.id.tvSecondTaskAnswer);
                tvAnswer.setText(getAnswer());
            }
        };

        setKnightPosition = new SquareTouchedAction() {
            public void run(int col, int row) {
                chessBoard.putPiece(Piece.W_KNIGHT, col, row);
                chessBoard.setSquareTouchEvent(setEndpointPosition);
                startPoint.set(col, row);
            }
        };
    }

    private String getAnswer() {
        String verdict = "NO";
        for (Point firstPossiblePoint : Piece.W_KNIGHT.getPossibleMoves(startPoint, ChessBoard.COLS, ChessBoard.ROWS)) {
            if(firstPossiblePoint.equals(endPoint)) {
                verdict = "1";
                break;
            }

            for (Point secondPossiblePoint : Piece.W_KNIGHT.getPossibleMoves(firstPossiblePoint, ChessBoard.COLS, ChessBoard.ROWS)) {
                if (secondPossiblePoint.equals(endPoint)) {
                    verdict = "2";
                    break;
                }
            }
        }

        return ChessBoard.getSquareCoordinate(startPoint.x, startPoint.y) + " => "
                + ChessBoard.getSquareCoordinate(endPoint.x, endPoint.y) + "   " + verdict;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_task);

        chessBoard = findViewById(R.id.chessBoard);
        init();

        chessBoard.setSquareTouchEvent(setKnightPosition);
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
}
