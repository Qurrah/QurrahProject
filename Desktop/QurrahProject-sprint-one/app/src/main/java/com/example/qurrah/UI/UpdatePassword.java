package com.example.qurrah.UI;

import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassword extends AppCompatActivity {

    private TextInputLayout newPassword , currentPassword, repeatPassword;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    String userPasswordNew,currentPassword2,repeatPassword2;
    Boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        //----------------------------------------------------------------
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("تغيير كلمة المرور");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        //----------------------------------------------------------------
        Button update = findViewById(R.id.UpdatePassword);
        currentPassword = findViewById(R.id.CurrentPassword);
        newPassword = findViewById(R.id.NewPassword);
        repeatPassword = findViewById(R.id.RepeatPassword);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

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

        currentPassword2 = currentPassword.getEditText().getText().toString();
        userPasswordNew = newPassword.getEditText().getText().toString();
        repeatPassword2 = repeatPassword.getEditText().getText().toString();

        result = false;
        if (currentPassword2.isEmpty()) {
            currentPassword.setError("الرجاء ادخال كلمة المرور الحالية");
            currentPassword.requestFocus();
            return result;

        }

        else if(true) {
            firebaseAuth.signInWithEmailAndPassword(firebaseUser.getEmail(), currentPassword2).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    currentPassword.setError(null);


                } else {

                    currentPassword.setError("كلمة المرور المدخلة غير صحيحة");
                    currentPassword.requestFocus();

                }
            });
        }

        else if (userPasswordNew.isEmpty()) {
            newPassword.setError("الرجاء ادخال كلمة مرور");
            newPassword.requestFocus();
        }

        else if (userPasswordNew.length() < 6) {
            newPassword.setError("أدخل كلمة مرور من 6 خانات أو اكثر");
            newPassword.requestFocus();
        }

        else if (repeatPassword2.isEmpty()) {
            repeatPassword.setError("الرجاء إعادة ادخال كلمة المرور");
            repeatPassword.requestFocus();
        }

        else if (!repeatPassword2.equals(userPasswordNew)) {
            repeatPassword.setError("كلمة المرور غير متطابقة");
            repeatPassword.requestFocus();
        }


        else{
            result=true;
        }
        return result;
    }
}
