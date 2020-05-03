package com.example.finedayapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNightMode();
    }

    public void setNightMode() {
        setTheme(R.style.NoteTheme);
    }

}
