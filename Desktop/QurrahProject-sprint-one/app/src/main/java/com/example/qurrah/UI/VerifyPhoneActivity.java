package com.example.qurrah.UI;

import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.laizexin.sdj.library.ProgressButton;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    private ImageView back;


    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    //private TextInputLayout editText;
    String email, name, phonenumber,password, phoneNumberWithCode;
    ProgressButton check;
    PinView pinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();

        final PinView pinView = findViewById(R.id.pinView);


        progressBar = findViewById(R.id.progressbar);
        //editText = findViewById(R.id.editTextCode);
        Bundle extras = getIntent().getExtras();
        back = findViewById(R.id.back2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyPhoneActivity.this, RegistrationActivity.class));
            }});

        phoneNumberWithCode = "+966" + extras.getString("phonenumber");
        phonenumber = extras.getString("phonenumber");

        email = extras.getString("email");
        name = extras.getString("name");
//        age = extras.getString("age");
        password=extras.getString("password");

        sendVerificationCode(phonenumber);
       check =  findViewById(R.id.buttonSignUp);
       check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = pinView.getText().toString().trim();

                if (check.getProgress() == 0) {
                    check.setProgress(50);
                } else if (check.getProgress() == 100) {
                    check.setProgress(0);
                } else {
                    check.setProgress(100);
                }
                if (code.isEmpty() || code.length() < 6 ) {
                    Toast.makeText(VerifyPhoneActivity.this,"أدخل رمز تحقق صحيح",Toast.LENGTH_SHORT).show();
                    pinView.requestFocus();
                    return;
                }



                verifyCode(code);
            }
        });

    }

    private void verifyCode(String code) {
        try{
       PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);}
        catch (Exception e){}
    }


    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user_email = email.trim();
                    String user_password = password.trim();

                    mAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendUserData();
                                Intent intent = new Intent(VerifyPhoneActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(VerifyPhoneActivity.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                check.setProgress(0);
                                Toast.makeText(VerifyPhoneActivity.this, "فشل تسجيل الدخول", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                        }  else{
                            check.setProgress(0);
                            Toast.makeText(VerifyPhoneActivity.this, "رمز التحقق غير صحيح", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+9660" + number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pinView.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void sendUserData(){
        try{
        String userID = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = firebaseDatabase.getReference(mAuth.getUid());
        DatabaseReference myRef = firebaseDatabase.getReference().child("Users").child(userID);
        UserProfile userProfile = new UserProfile( userID, email, name, phoneNumberWithCode);
        myRef.setValue(userProfile);}
        catch (Exception e){}
    }
}
