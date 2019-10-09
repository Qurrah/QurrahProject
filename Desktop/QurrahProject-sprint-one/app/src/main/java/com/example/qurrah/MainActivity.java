package com.example.qurrah;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText userِEmail;
    private EditText Password;
    private Button Login;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword;
    private ProgressBar progressBar;
    String Email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//-------------------------------------------------------------
        userِEmail = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Login = findViewById(R.id.btnLogin);
        userRegistration = findViewById(R.id.tvRegister);
        forgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);
//-------------------------------------------------------------
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }
//--------------------------------------------------------------

        userِEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Email = userِEmail.getText().toString().trim();
                if (validateEmail(Email) && !Password.getText().toString().isEmpty()){
                    Login.setEnabled(true);
                    Login.setAlpha(1f);
                }else {
                    Login.setEnabled(false);
                    Login.setAlpha(0.6f);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = Password.getText().toString().trim();
                if (validatePassword(password) && !userِEmail.getText().toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(userِEmail.getText().toString().trim()).matches()){
                    Login.setEnabled(true);
                    Login.setAlpha(1f);
                }else {
                    Login.setEnabled(false);
                    Login.setAlpha(0.6f);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login.setEnabled(false);
                Login.setText("");
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if( validate(Email, password)) {

                firebaseAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "تم تسجيل الدخول", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, SecondActivity.class));

                        } else {
                            Login.setEnabled(true);
                            Toast.makeText(MainActivity.this, "فشل تسجيل الدخول, الرجاء ادخال بيانات صحيحة", Toast.LENGTH_SHORT).show();
                            Login.setText("تسجيل الدخول");
                            progressBar.setVisibility(View.INVISIBLE);

                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        }
                    }

                });
            }else {
                Login.setEnabled(true);
                Login.setText("تسجيل الدخول");
                progressBar.setVisibility(View.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            }

        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });


    }

    private boolean validate(String Email, String userPassword) {

        boolean result;

        result = validateEmail(Email);
        if (result) {

           result = validatePassword(userPassword);

            return result;
        }
        return result;
    }

private boolean validateEmail (String email){
    if (email.isEmpty()) {
        userِEmail.setError("الرجاء ادخال البريد الالكتروني");
        userِEmail.requestFocus();
        return false;
    }

    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        userِEmail.setError("صيغة البريد الالكتروني غير صحيحة");
        userِEmail.requestFocus();
        return false;
    }
    return true;
}
private boolean validatePassword(String password){
    if (password.isEmpty()) {
        Password.setError("الرجاء ادخال كلمة المرور");
        Password.requestFocus();
        return false;
    }
    return true;
}

}
