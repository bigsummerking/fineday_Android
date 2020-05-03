package com.example.finedayapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CardView card_DailyCount;
    private CardView card_PersonNote;
    private CardView card_StarMovie;
    private CardView card_daycheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        card_DailyCount = findViewById(R.id.card_count);
        card_PersonNote = findViewById(R.id.card_note);
        card_StarMovie = findViewById(R.id.card_movie);
        card_daycheck = findViewById(R.id.card_daycheck);

        card_DailyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Count_MainActivity.class);
                startActivity(intent);
                Toast toast = onCardClick(getApplicationContext(), "欢迎来到乌鸦记账", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        card_PersonNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Note_MainActivity.class);
                startActivity(intent);
                Toast toast = onCardClick(getApplicationContext(), "欢迎来到鸵鸟便签", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        card_StarMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Movie_MainActivity.class);
                startActivity(intent);
                Toast toast = onCardClick(getApplicationContext(), "欢迎来到水牛电影", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        card_daycheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyMap_Activity.class);
                startActivity(intent);
                Toast toast = onCardClick(getApplicationContext(), "欢迎来到蜗牛足迹", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public Toast onCardClick(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        return toast;
    }

}
