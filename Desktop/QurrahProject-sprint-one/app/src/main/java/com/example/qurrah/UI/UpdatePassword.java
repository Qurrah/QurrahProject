package com.example.qurrah.UI;

import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicBoolean;

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
        update.setEnabled(false);
        update.setAlpha(0.6f);

        currentPassword2 = currentPassword.getEditText().getText().toString().trim();
        userPasswordNew = newPassword.getEditText().getText().toString().trim();
        repeatPassword2 = repeatPassword.getEditText().getText().toString().trim();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();

        currentPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentPassword2 = currentPassword.getEditText().getText().toString().trim();
                if(currentPassword2.length()>=7)
                    validateCurrent();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentPassword2 = currentPassword.getEditText().getText().toString().trim();
                if (validateN()){
                    update.setEnabled(true);
                    update.setAlpha(1f);
                }else {
                    update.setEnabled(false);
                    update.setAlpha(0.6f);
                }
                if(currentPassword2.length()>=7)
                    validateCurrent();
            }
        });



        newPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userPasswordNew = newPassword.getEditText().getText().toString().trim();
                if(userPasswordNew.length()>=7)
                    validateNew();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userPasswordNew = newPassword.getEditText().getText().toString().trim();
                if (validateN()){
                    update.setEnabled(true);
                    update.setAlpha(1f);
                }else {
                    update.setEnabled(false);
                    update.setAlpha(0.6f);
                }

                if(userPasswordNew.length()>=7)
                    validateNew();
            }
        });

        repeatPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                repeatPassword2 = repeatPassword.getEditText().getText().toString().trim();
                if(repeatPassword2.length()>=7)
                    validateRepeat();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                repeatPassword2 = repeatPassword.getEditText().getText().toString().trim();
                if (validateN()){
                    update.setEnabled(true);
                    update.setAlpha(1f);
                }else {
                    update.setEnabled(false);
                    update.setAlpha(0.6f);
                }

                if(repeatPassword2.length()>=7)
                    validateRepeat();
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(validateCurrent()&&validateNew()&&validateRepeat()){
                AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword2);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        currentPassword.setError(null);
                        //result=true;
                        firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UpdatePassword.this, "تم تغيير كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(UpdatePassword.this, "فشلت عملية تغيير كلمة المرور", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
//                    } else {
//
//                        currentPassword.setError("كلمة المرور المدخلة غير صحيحة");
//                        currentPassword.requestFocus();
//                        result=false;
//                    }
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


    private boolean validateN(){
        if (currentPassword2.isEmpty() || userPasswordNew.isEmpty() || repeatPassword2.isEmpty()) {
            return false;
        }
        return true;
    }

    private Boolean validateCurrent() {
        result=true;
        currentPassword.setError(null);
        if (currentPassword2.isEmpty()) {
            currentPassword.setError("الرجاء ادخال كلمة المرور الحالية");
            currentPassword.requestFocus();
            return false;
        }

            AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword2);
            firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    currentPassword.setError(null);
                    //result=true;


                } else {

                    currentPassword.setError("كلمة المرور المدخلة غير صحيحة");
                    currentPassword.requestFocus();
                    result=false;
                }
            });


        return result;
    }

    private Boolean validateNew() {

        if (userPasswordNew.isEmpty()) {
            newPassword.setError("الرجاء ادخال كلمة مرور");
            newPassword.requestFocus();
            return false;

        }

        else if (userPasswordNew.length() < 7) {
            newPassword.setError("أدخل كلمة مرور من 7 خانات أو اكثر");
            newPassword.requestFocus();
            return false;

        }

            newPassword.setError(null);

        return true;
    }

    private Boolean validateRepeat() {

        if (repeatPassword2.isEmpty()) {
            repeatPassword.setError("الرجاء إعادة ادخال كلمة مرور");
            repeatPassword.requestFocus();
            return false;

        }

        else if (!repeatPassword2.equals(userPasswordNew)) {
            repeatPassword.setError("كلمة المرور غير متطابقة");
            repeatPassword.requestFocus();
            return false;
        }

        repeatPassword.setError(null);

        return true;
    }

}
