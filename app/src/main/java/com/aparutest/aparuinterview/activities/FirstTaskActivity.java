package com.aparutest.aparuinterview.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aparutest.aparuinterview.R;

import java.math.BigDecimal;

public class FirstTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_task);

    }

    public void onCalculate(View view) {
        EditText powerValText = findViewById(R.id.powerValue);
        TextView tvPower = findViewById(R.id.tvPower);
        TextView tvAnswer = findViewById(R.id.tvAnswer);

        int power = powerValText.getText().length() > 0
                ? Integer.parseInt(powerValText.getText().toString())
                : 0;
        double answer = Math.pow(2, power);

        tvPower.setText(String.valueOf(power));
        tvAnswer.setText(String.valueOf(answer));
    }

    public void jumpToSecond(View view) {
        Intent intent = new Intent(this, SecondTaskActivity.class);
        startActivity(intent);
    }
}
