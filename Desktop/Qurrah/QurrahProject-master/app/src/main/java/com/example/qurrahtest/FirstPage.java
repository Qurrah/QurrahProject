package com.example.qurrahtest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

public class FirstPage extends AppCompatActivity {
private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent hp = new Intent(FirstPage.this , MainActivity.class);
                startActivity(hp);
                finish();
            }

            },SPLASH_TIME_OUT);
        }
    }
