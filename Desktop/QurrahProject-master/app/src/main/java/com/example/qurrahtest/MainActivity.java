package com.example.qurrahtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private TextView Info;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;
    String Email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userِEmail = (EditText)findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etPassword);
        //Info = (TextView)findViewById(R.id.tvInfo);
        Login = (Button)findViewById(R.id.btnLogin);
        userRegistration = (TextView)findViewById(R.id.tvRegister);
        forgotPassword = (TextView)findViewById(R.id.tvForgotPassword);


        //Info.setText("عدد المحاولات المتبقية : 5");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email = userِEmail.getText().toString();
                password = Password.getText().toString();


            if( validate(Email, password)) {
                firebaseAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "تم تسجيل الدخول", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, SecondActivity.class));

                        } else {
                            Toast.makeText(MainActivity.this, "فشل تسجيل الدخول, الرجاء ادخال بيانات صحيحة", Toast.LENGTH_SHORT).show();
//                    counter--;
//                    Info.setText("عدد المحاولات المتبقية : "+ counter);
//                    progressDialog.dismiss();
//                    if(counter == 0){
//                        Login.setEnabled(false);
                        }
                    }

                });
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

    private Boolean validate(String Email, String userPassword) {

        Boolean result = false;
//        progressDialog.setMessage("You can subscribe to my channel until you are verified!");
//        progressDialog.show();

        if (Email.isEmpty()) {
            userِEmail.setError("الرجاء ادخال البريد الالكتروني");
            userِEmail.requestFocus();
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            userِEmail.setError("صيغة البريد الالكتروني غير صحيحة");
            userِEmail.requestFocus();
        }

        else if (password.isEmpty()) {
            Password.setError("الرجاء ادخال كلمة المرور");
            Password.requestFocus();
        }
        else{
            result = true;
        }

        return result;




    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag){
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }else{
            Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

}
