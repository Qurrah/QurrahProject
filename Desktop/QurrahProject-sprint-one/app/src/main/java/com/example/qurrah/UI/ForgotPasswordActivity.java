package com.example.qurrah.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.laizexin.sdj.library.ProgressButton;

import java.util.Objects;

//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
//-------------------------------------------------------
    private ProgressButton update;
    private ImageView back;
    private TextInputLayout email;
    private FirebaseAuth firebaseAuth;
//    private ProgressBar progressBar;
    String emailString;
//-------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgot);

        update = findViewById(R.id.Update);
        email = findViewById(R.id.Email);
//        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
        firebaseAuth = FirebaseAuth.getInstance();


        update.setProgress(0);

       // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            }});

        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailString = email.getEditText().getText().toString().trim();



            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                if (validateEmail(emailString)){
                    update.setEnabled(true);
                    update.setAlpha(1f);
                }else {
                    update.setEnabled(false);
                    update.setAlpha(0.6f);
                }
            }}
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (update.getProgress() == 0) {
                    update.setProgress(50);
                } else if (update.getProgress() == 100) {
                    update.setProgress(0);
                } else {
                    update.setProgress(100);
                }
                String userEmail = email.getEditText().getText().toString().trim();
                update.setEnabled(false);
                update.setText("");
//                progressBar.setVisibility(View.VISIBLE);
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "لقد تم ارسال رابط اعادة ضبط كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                            }else{
//                                update.setEnabled(true);
                                update.setProgress(0);
                                update.setText("إعادة تعيين كلمة المرور");
//                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ForgotPasswordActivity.this, "الرجاء ادخال البريد الالكتروني الصحيح الخاص بحسابك", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
        });


    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()){
//            case android.R.id.home:
//                onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }
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