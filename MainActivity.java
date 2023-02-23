package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    reversi board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        board = new reversi(context);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.textView00:
            case R.id.textView01:
            case R.id.textView02:
            case R.id.textView03:
            case R.id.textView04:
            case R.id.textView05:
            case R.id.textView06:
            case R.id.textView07:
            case R.id.textView10:
            case R.id.textView11:
            case R.id.textView12:
            case R.id.textView13:
            case R.id.textView14:
            case R.id.textView15:
            case R.id.textView16:
            case R.id.textView17:
            case R.id.textView20:
            case R.id.textView21:
            case R.id.textView22:
            case R.id.textView23:
            case R.id.textView24:
            case R.id.textView25:
            case R.id.textView26:
            case R.id.textView27:
            case R.id.textView30:
            case R.id.textView31:
            case R.id.textView32:
            case R.id.textView33:
            case R.id.textView34:
            case R.id.textView35:
            case R.id.textView36:
            case R.id.textView37:
            case R.id.textView40:
            case R.id.textView41:
            case R.id.textView42:
            case R.id.textView43:
            case R.id.textView44:
            case R.id.textView45:
            case R.id.textView46:
            case R.id.textView47:
            case R.id.textView50:
            case R.id.textView51:
            case R.id.textView52:
            case R.id.textView53:
            case R.id.textView54:
            case R.id.textView55:
            case R.id.textView56:
            case R.id.textView57:
            case R.id.textView60:
            case R.id.textView61:
            case R.id.textView62:
            case R.id.textView63:
            case R.id.textView64:
            case R.id.textView65:
            case R.id.textView66:
            case R.id.textView67:
            case R.id.textView70:
            case R.id.textView71:
            case R.id.textView72:
            case R.id.textView73:
            case R.id.textView74:
            case R.id.textView75:
            case R.id.textView76:
            case R.id.textView77:

                TextView tv = findViewById(id);
                board.putStone(tv);

                break;
        }
    }
}