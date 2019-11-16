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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.laizexin.sdj.library.ProgressButton;


// fix the code
// continue as guest position
// it allows me to sign in with an email and password that exist in the authentication even though they're deleted from the database



public class MainActivity extends AppCompatActivity {

    private TextInputLayout userِEmail , Password;
//    private EditText userِEmail;
//    private EditText Password;
    private ProgressButton Login;
    private Button userRegistration;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword , guest;
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
       // overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
//-------------------------------------------------------------
        userِEmail = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Login = findViewById(R.id.btnLogin);

        userRegistration = findViewById(R.id.tvRegister);
        forgotPassword = findViewById(R.id.tvForgotPassword);
        guest = findViewById(R.id.guest);
//-------------------------------------------------------------

        Email = userِEmail.getEditText().getText().toString().trim();
        password = Password.getEditText().getText().toString().trim();

        Login.setEnabled(false);
        Login.setAlpha(0.6f);
        Login.setProgress(0);
//--------------------------------------------------------------
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
//
        if(user != null){
            startActivity(new Intent(MainActivity.this, SecondActivity.class).putExtra("from", "MainActivity"));
            finish();
        }
//--------------------------------------------------------------
//        setupUI(findViewById(R.id.parent));

        userِEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Email = userِEmail.getEditText().getText().toString().trim();
                if (validateN()){   //  && !Password.getEditText().getText().toString().isEmpty()
                    Login.setEnabled(true);
                    Login.setAlpha(1f);
                }else {
                    Login.setEnabled(false);
                    Login.setAlpha(0.6f);
                }

                Email = userِEmail.getEditText().getText().toString().trim();
                if (Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    validateEmail();}
            }

            @Override
            public void afterTextChanged(Editable s) {
                Email = userِEmail.getEditText().getText().toString().trim();
                if (Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    validateEmail();}
            }
        });
        Password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = Password.getEditText().getText().toString().trim();
                if (validateN()) {
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

        guest.setOnClickListener(view -> startActivity(new Intent(MainActivity.this , UnregisteredUserSecondActivity.class)));

        Login.setOnClickListener(view -> {
           //  Login.setEnabled(false);
            if (Login.getProgress() == 0) {
                Login.setProgress(50);
            } else if (Login.getProgress() == 100) {
                Login.setProgress(0);
            } else {
                Login.setProgress(100);
            }

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(validateEmail() && validatePassword()) {

                firebaseAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "تم تسجيل الدخول", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, SecondActivity.class));

                        } else {
                            Login.setEnabled(true);
                            Login.setProgress(0);
                            Toast.makeText(MainActivity.this, "فشل تسجيل الدخول, الرجاء ادخال بيانات صحيحة", Toast.LENGTH_SHORT).show();

                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        }
                    }

                });
            }else {
               Login.setEnabled(true);
               Login.setProgress(0);
               getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

        });

        userRegistration.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        });

        forgotPassword.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class)));


    }

//    private boolean validate(String Email, String userPassword) {
//
//        boolean result;
//
//        result = validateEmail(Email);
//        if (result) {
//
//            result = validatePassword(userPassword);
//
//            return result;
//        }
//        return result;
//    }

    private boolean validateEmail (){
        if (Email.isEmpty()) {
            userِEmail.setError("الرجاء ادخال البريد الالكتروني");
            userِEmail.requestFocus();
            return false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            userِEmail.setError("صيغة البريد الالكتروني غير صحيحة");
            userِEmail.requestFocus();
            return false;
        }
        else
            userِEmail.setError(null);

        return true;
    }


    private boolean validatePassword(){
        if (password.isEmpty()) {
            Password.setError("الرجاء ادخال كلمة المرور");
            Password.requestFocus();
            return false;
        }
        else
            Password.setError(null);
        return true;
    }



    // Added these to set errors messages only when login button is clicked
    private boolean validateE(){
        if (Email.isEmpty()) {
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


    private boolean validateN(){
        if (Email.isEmpty() || password.isEmpty()) {
            return false;
        }
        return true;
    }




//    public void setupUI(View view) {
//
//        // Set up touch listener for non-text box views to hide keyboard.
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener(new View.OnTouchListener() {
//                public boolean onTouch(View v, MotionEvent event) {
////                    hideSoftKeyboard(MainActivity.this);
//                    findViewById(R.id.dummyFocus).requestFocus();
//                    return false;
//                }
//            });
//        }
//
//        //If a layout container, iterate over children and seed recursion.
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setupUI(innerView);
//            }
//        }
//    }

}


