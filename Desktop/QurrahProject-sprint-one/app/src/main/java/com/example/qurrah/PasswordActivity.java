package com.example.qurrah;

import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class PasswordActivity extends AppCompatActivity {

    private Button update;
    private EditText email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        update = findViewById(R.id.Update);
        email = findViewById(R.id.Email);
        firebaseAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = email.getText().toString().trim();


                if (userEmail.isEmpty()) {
                    email.setError("الرجاء ادخال البريد الالكتروني");
                    email.requestFocus();
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    email.setError("صيغة البريد الالكتروني غير صحيحة");
                    email.requestFocus();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordActivity.this, "لقد تم ارسال رابط اعادة ضبط كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(PasswordActivity.this, "حدث خطأ اثناء محاولة الارسال ، الرجاء ادخال البريد الالكتروني الخاص بحسابك", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }}
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}