package com.example.qurrah;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegistrationActivity extends AppCompatActivity {

    private EditText phone,userName, userPassword, userEmail;
    private Button regButton;
    private TextView userLogin;
    private Spinner spinner;
    String email, name, password, phoneNumber , phoneN;
    Boolean result;
    boolean valid ;
    private CheckBox policyCheck ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

//        spinner = findViewById(R.id.spinnerCountries);
//        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryCode.countryNames));
        name = userName.getText().toString().trim();
        password = userPassword.getText().toString().trim();
        email = userEmail.getText().toString().trim();
//            age = userAge.getText().toString();
        phoneNumber = phone.getText().toString().trim();
        phoneN = phone.getText().toString().trim();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        regButton.setEnabled(false);
        regButton.setAlpha(0.6f);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = userName.getText().toString().trim();
                if (validateN()){

                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }

            }
        });



        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = userEmail.getText().toString().trim();
                if (validateN()){
                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }

            }
        });


        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password = userPassword.getText().toString().trim();
                if (validateN()){
                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }

            }
        });


        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phoneN = phone.getText().toString().trim();
                if (validateN()){
                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }

            }
        });







        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                regButton.setEnabled(false);
//                if(validate()){
                    //Upload data to the database

                // will remove recalling methods
                if(validateName()&& validateEmail()&& validatePass()&&validatePhone()&&validatePolicy()) {
                    if(duplicateEmail()&&duplicatePhone())  // find something to make the method wait , it returns true before it search the whole database
                        if(duplicatePhone())
                    step1();
                }
//                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

    }

    private void setupUIViews(){
        userName = findViewById(R.id.etUserName);
        userPassword = findViewById(R.id.etUserPassword);
        userEmail = findViewById(R.id.etUserEmail);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
//        userAge = (EditText)findViewById(R.id.etAge2);
        phone = findViewById(R.id.phone);
        policyCheck = findViewById(R.id.policy);
    }


    private Boolean validateName() {

        if (name.isEmpty()) {
            userName.setError("الرجاء ادخال اسم المستخدم");
            userName.requestFocus();
            return false;
        }
        if (name.length() >= 3 ) {
            userName.setError(null);
            return true;

        }else {
            userName.setError("أدخل اسم مستخدم مكون من 3 خانات أو اكثر");
            userName.requestFocus();
            return false;
        }

    }


    private Boolean validateEmail() {

        if (email.isEmpty()) {
            userEmail.setError("الرجاء ادخال البريد الالكتروني");
            userEmail.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("صيغة البريد الالكتروني غير صحيحة");
            userEmail.requestFocus();
            return false;
        }
        else if(duplicateEmail())
            userEmail.setError(null);
            return true;
    }



    private Boolean validatePass() {

        if (password.isEmpty()) {
            userPassword.setError("الرجاء ادخال كلمة المرور");
            userPassword.requestFocus();
            return false;
        }

        else if (password.length() < 7) {
            userPassword.setError("أدخل كلمة مرور من 7 خانات أو اكثر");
            userPassword.requestFocus();
            return false;
        }

        else
            userPassword.setError(null);
            return true;
    }



    private Boolean validatePhone() {

       if (phoneN.isEmpty()) {
            phone.setError("الرجاء ادخال رقم الجوال");
            phone.requestFocus();
            return false;
        }
        else if (phoneN.length() != 9 | phoneN.charAt(0)!= '5') {
            phone.setError("الرقم غير صحيح، الرجاء ادخال الصيغة الصحيحة");
            phone.requestFocus();
            return false;
        }
       else if(duplicatePhone())
           phone.setError(null);
        return true;
    }


    private Boolean validatePolicy() {

    if (!(policyCheck.isChecked())) {
        policyCheck.setError("الرجاء الموافقة على الشروط والأحكام");
        policyCheck.requestFocus();
        return false;
    }
            else
        policyCheck.setError(null);
                return true;
        }







    private boolean validateN(){
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneN.isEmpty()) {
            return false;
//            email.isEmpty() || password.isEmpty() || phoneN.isEmpty() || !(policyCheck.isChecked())
        }
//        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            return false;
//        }
        return true;
    }


//
//    private boolean validateE(){
//        if (email.isEmpty()) {
//            return false;
////            email.isEmpty() || password.isEmpty() || phoneN.isEmpty() || !(policyCheck.isChecked())
//        }
//        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            return false;
//        }
//        return true;
//    }
//
//
//
//    private boolean validateP(){
//        if (password.isEmpty()) {
//            return false;
////            email.isEmpty() || password.isEmpty() || phoneN.isEmpty() || !(policyCheck.isChecked())
//        }
////        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
////            return false;
////        }
//        return true;
//    }
//
//    private boolean validatePh(){
//        if (phoneN.isEmpty()) {
//            return false;
////            email.isEmpty() || password.isEmpty() || phoneN.isEmpty() || !(policyCheck.isChecked())
//        }
////        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
////            return false;
////        }
//        return true;
//    }
//
//    private boolean validatePo(){
//        if (!(policyCheck.isChecked())) {
//            return false;
////            email.isEmpty() || password.isEmpty() || phoneN.isEmpty() || )
//        }
////        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
////            return false;
////        }
//        return true;
//    }

















//    private Boolean validate(){
//        result = false;
//
//        name = userName.getText().toString();
//        password = userPassword.getText().toString();
//        email = userEmail.getText().toString();
////            age = userAge.getText().toString();
//        phoneNumber = phone.getText().toString().trim();
//        phoneN = phone.getText().toString().trim();
//        //String code = CountryCode.countryAreaCodes[spinner.getSelectedItemPosition()];
//
//
//        if (name.isEmpty()) {
//            userName.setError("الرجاء ادخال اسم المستخدم");
//            userName.requestFocus();
//        }
//
//        else if (name.length() < 3 ) {
//            userName.setError("أدخل اسم مستخدم مكون من 3 خانات أو اكثر");
//            userName.requestFocus();
//        }
//
//        else if (email.isEmpty()) {
//            userEmail.setError("الرجاء ادخال البريد الالكتروني");
//            userEmail.requestFocus();
//        }
//
//        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            userEmail.setError("صيغة البريد الالكتروني غير صحيحة");
//            userEmail.requestFocus();
//        }
//
//        else if (password.isEmpty()) {
//            userPassword.setError("الرجاء ادخال كلمة المرور");
//            userPassword.requestFocus();
//        }
//
//        else if (password.length() < 6) {
//            userPassword.setError("أدخل كلمة مرور من 6 خانات أو اكثر");
//            userPassword.requestFocus();
//        }
//
//        else if (phoneN.isEmpty()) {
//            phone.setError("الرجاء ادخال رقم الجوال");
//            phone.requestFocus();
//        }
//
//        else if (phoneN.length() != 9) {
//            phone.setError("الرقم غير صحيح، الرجاء ادخال الصيغة الصحيحة");
//            phone.requestFocus();
//        }
//
//
//        else if (!(policyCheck.isChecked())) {
//            policyCheck.setError("الرجاء الموافقة على الشروط والأحكام");
//            policyCheck.requestFocus();
//        }
//
//        else{
//            result =duplicate();
//        }
//
//
//        return result;
//    }

    private Boolean duplicateEmail() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {

                    valid = task.getResult().getSignInMethods().isEmpty();

                    if (valid) {

                    } else {
                        userEmail.setError("البريد الالكتروني مستخدم مسبقاً،الرجاء ادخال بريد الكتروني صحيح");
                        userEmail.requestFocus();
                    }
                }
            }
        });

        return valid;
    }



    private Boolean duplicatePhone() {

        phoneNumber = "+966"+phoneN;
        Query phoneQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(phoneNumber);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    valid = false;
                    phone.setError("رقم الهاتف مستخدم مسبقاً،الرجاء ادخال رقم هاتف صحيح");
                    phone.requestFocus();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return valid;
    }



//    private Boolean duplicate(){
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//
//        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
//            @Override
//            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
//                if (task.isSuccessful()){
//
//                    valid = task.getResult().getSignInMethods().isEmpty();
//
//                    if (valid){
//                    }
//                    else {
//                        userEmail.setError("البريد الالكتروني مستخدم مسبقاً،الرجاء ادخال بريد الكتروني صحيح");
//                        userEmail.requestFocus();
//                    }
//                }}
//        });
//
//
//        Query phoneQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(phoneNumber);
//        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getChildrenCount() > 0) {
//                    valid = false;
//                    phone.setError("رقم الهاتف مستخدم مسبقاً،الرجاء ادخال رقم هاتف صحيح");
//                    phone.requestFocus();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });
//
//        return valid;
//
//    }




    protected void step1() {
        try{

            Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
//                intent.putExtra("phonenumber", phoneNumber);
            Bundle extras = new Bundle();
            extras.putString("phonenumber",phoneNumber);
//                extras.putString("age",age);
            extras.putString("email",email);
            extras.putString("name",name);
            extras.putString("password",password);

            intent.putExtras(extras);
//                finish();
            startActivity(intent);}
        catch (Exception e){ e.getStackTrace();}

    }


}
