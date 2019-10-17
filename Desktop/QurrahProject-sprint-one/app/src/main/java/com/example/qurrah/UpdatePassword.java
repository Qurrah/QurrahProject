package com.example.qurrah;

import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassword extends AppCompatActivity {

    private EditText newPassword;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    String userPasswordNew;
    Boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
 //------------------------------------------------
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//------------------------------------------------
        Button update = findViewById(R.id.UpdatePassword);
        newPassword = (EditText)findViewById(R.id.NewPassword);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(validate()){
                firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UpdatePassword.this, "تم تغيير كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(UpdatePassword.this, "فشلت عملية تغيير كلمة المرور", Toast.LENGTH_SHORT).show();
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
    private Boolean validate(){

        userPasswordNew = newPassword.getText().toString();
        result = false;
        if (userPasswordNew.isEmpty()) {
            newPassword.setError("الرجاء ادخال كلمة المرور");
            newPassword.requestFocus();
        }

        else if (userPasswordNew.length() < 6) {
            newPassword.setError("أدخل كلمة مرور من 6 خانات أو اكثر");
            newPassword.requestFocus();
        }
        else{
            result=true;
        }
        return result;
    }
}
