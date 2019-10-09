package com.example.qurrah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SecondPage extends AppCompatActivity {

    private Button signup , signin , guest ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_page);

        signup = (Button)findViewById(R.id.signUpBtn);
        signin = (Button)findViewById(R.id.signInBtn);
        guest = (Button)findViewById(R.id.guest);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondPage.this , RegistrationActivity.class));
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondPage.this , MainActivity.class));
            }
        });


        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondPage.this , UserSecondActivity.class));
            }
        });


    }
}
