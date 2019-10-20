package com.example.qurrah;

import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class ForgotPasswordActivity extends AppCompatActivity {
//-------------------------------------------------------
    private Button update;
    private EditText email;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    String emailString;
//-------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgot);

        update = findViewById(R.id.Update);
        email = findViewById(R.id.Email);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

           email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailString = email.getText().toString().trim();
                if (validateEmail(emailString)){
                    update.setEnabled(true);
                    update.setAlpha(1f);
                }else {
                    update.setEnabled(false);
                    update.setAlpha(0.6f);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = email.getText().toString().trim();
                update.setEnabled(false);
                update.setText("");
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "لقد تم ارسال رابط اعادة ضبط كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                            }else{
                                update.setEnabled(true);
                                update.setText("إعادة تعيين كلمة المرور");
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ForgotPasswordActivity.this, "حدث خطأ اثناء محاولة الارسال ، الرجاء ادخال البريد الالكتروني الصحيح الخاص بحسابك", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
    private boolean validateEmail (String emailString){
        if (emailString.isEmpty()) {
            email.setError("الرجاء ادخال البريد الالكتروني");
            email.requestFocus();
            return false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("صيغة البريد الالكتروني غير صحيحة");
            email.requestFocus();
            return false;
        }
        return true;
    }
}