package com.example.qurrah.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()){
                    //Upload data to the database
                    step1();
                }
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
        userName = (EditText)findViewById(R.id.etUserName);
        userPassword = (EditText)findViewById(R.id.etUserPassword);
        userEmail = (EditText)findViewById(R.id.etUserEmail);
        regButton = (Button)findViewById(R.id.btnRegister);
        userLogin = (TextView)findViewById(R.id.tvUserLogin);
//        userAge = (EditText)findViewById(R.id.etAge2);
        phone = (EditText)findViewById(R.id.phone);
        policyCheck = (CheckBox)findViewById(R.id.policy);
    }

    private Boolean validate(){
        result = false;

        name = userName.getText().toString();
        password = userPassword.getText().toString();
        email = userEmail.getText().toString();
//            age = userAge.getText().toString();
        phoneNumber = phone.getText().toString().trim();
        phoneN = phone.getText().toString().trim();
        //String code = CountryCode.countryAreaCodes[spinner.getSelectedItemPosition()];


        if (name.isEmpty()) {
            userName.setError("الرجاء ادخال اسم المستخدم");
            userName.requestFocus();
        }

        else if (name.length() < 3 ) {
            userName.setError("أدخل اسم مستخدم مكون من 3 خانات أو اكثر");
            userName.requestFocus();
        }

        else if (email.isEmpty()) {
            userEmail.setError("الرجاء ادخال البريد الالكتروني");
            userEmail.requestFocus();
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("صيغة البريد الالكتروني غير صحيحة");
            userEmail.requestFocus();
        }

        else if (password.isEmpty()) {
            userPassword.setError("الرجاء ادخال كلمة المرور");
            userPassword.requestFocus();
        }

        else if (password.length() < 6) {
            userPassword.setError("أدخل كلمة مرور من 6 خانات أو اكثر");
            userPassword.requestFocus();
        }

        else if (phoneN.isEmpty()) {
            phone.setError("الرجاء ادخال رقم الجوال");
            phone.requestFocus();
        }

        else if (phoneN.length() != 9) {
            phone.setError("الرقم غير صحيح، الرجاء ادخال الصيغة الصحيحة");
            phone.requestFocus();
        }


        else if (!(policyCheck.isChecked())) {
            policyCheck.setError("الرجاء الموافقة على الشروط والأحكام");
            policyCheck.requestFocus();
        }

        else{
            result =duplicate();
        }


        return result;
    }





    private Boolean duplicate(){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()){

                    valid = task.getResult().getSignInMethods().isEmpty();

                    if (valid){
                    }
                    else {
                        userEmail.setError("البريد الالكتروني مستخدم مسبقاً،الرجاء ادخال بريد الكتروني صحيح");
                        userEmail.requestFocus();
                    }
                }}
        });


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




    protected void step1() {
        try{

            Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
//                intent.putExtra("phonenumber", phoneNumber);
            Bundle extras = new Bundle();
            extras.putString("phonenumber",phoneN);
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
