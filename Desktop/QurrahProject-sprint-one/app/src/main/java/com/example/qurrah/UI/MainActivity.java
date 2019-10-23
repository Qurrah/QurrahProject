package com.example.qurrah.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText userِEmail;
    private EditText Password;
    private Button Login, guest;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword;
    private ProgressBar progressBar;
    String Email, password;

//    public static void hideSoftKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) activity.getSystemService(
//                        Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(
//                activity.getCurrentFocus().getWindowToken(), 0);
//    }

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
        guest = findViewById(R.id.guest);
//-------------------------------------------------------------
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }
//--------------------------------------------------------------
        setupUI(findViewById(R.id.parent));

        userِEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Email = userِEmail.getText().toString().trim();
                if (validateE(Email) && !Password.getText().toString().isEmpty()){
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
                if (validateP(password) && !userِEmail.getText().toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(userِEmail.getText().toString().trim()).matches()){
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

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , UnregisteredUserSecondActivity.class));
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
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
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



    // Added these to set errors messages only when login button is clicked
    private boolean validateE(String email){
        if (email.isEmpty()) {
            return false;
        }

//        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            return false;
//        }
        return true;
    }
    private boolean validateP(String password){
        if (password.isEmpty())
            return false;

        return true;
    }






    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
//                    hideSoftKeyboard(MainActivity.this);
                    findViewById(R.id.dummyFocus).requestFocus();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}

