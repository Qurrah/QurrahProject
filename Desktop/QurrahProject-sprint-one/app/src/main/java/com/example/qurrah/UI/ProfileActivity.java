package com.example.qurrah.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.chaos.view.PinView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.laizexin.sdj.library.ProgressButton;
import com.squareup.picasso.Picasso;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView  profilePic;
    private EditText profileName, profileEmail,profilePhone;
    private ImageView editProfile ,addImg ,cancel;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private Switch allowPhoneaccess ;
    private TextView changePassword;
    private Button update ;
    ProgressButton check;
    FirebaseStorage storage;
    static boolean sFlag = false;
    protected boolean flag = false;
    protected Uri filePath;
    private String email, name, phoneNumber;
    private String Lemail, Lname, LphoneNumber;
    private String verificationId;
    StorageReference storageReference, storageRef;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    PinView pinView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic =  (CircleImageView) findViewById(R.id.ivProfilePic);
        addImg =  (CircleImageView) findViewById(R.id.img_plus);
        profileName = findViewById(R.id.tvProfileName);
        profileEmail = findViewById(R.id.tvProfileEmail);
        profilePhone = findViewById(R.id.tvProfilePhone);
        allowPhoneaccess=findViewById(R.id.accessPhoneNo);
        changePassword=findViewById(R.id.editPassword);
        editProfile= findViewById(R.id.edit_profile);
        update=  findViewById(R.id.update);
        cancel = (CircleImageView) findViewById(R.id.cancel);


    //----------------------------------------------------------------
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("حسابي");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        //----------------------------------------------------------------

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String userId=firebaseAuth.getCurrentUser().getUid();

        firebaseUser = firebaseAuth.getInstance().getCurrentUser();

         databaseReference = firebaseDatabase.getReference().child("Users").child(userId);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profilePhone.setText(userProfile.getPhone());
                LphoneNumber=userProfile.getPhone();
                profileName.setText(userProfile.getUserName());
                Lname=userProfile.getUserName();
                profileEmail.setText(userProfile.getUserEmail());
                Lemail=userProfile.getUserEmail();
                if(!userProfile.getImageURL().equals("default"))
                    Picasso.get().load(userProfile.getImageURL()).into(profilePic);
                String isChecked =userProfile.getAllowPhone();
                allowPhoneaccess.setChecked(isChecked.equals("true"));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        allowPhoneaccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!allowPhoneaccess.isChecked()){
                    databaseReference.child("allowPhone").setValue("false");

                }
                else{
                    databaseReference.child("allowPhone").setValue("true");

                }
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editProfile.setVisibility(View.GONE);
                addImg.setVisibility(View.VISIBLE);
                update.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                profileName.setEnabled(true);
                profileEmail.setEnabled(true);
                profilePhone.setEnabled(true);
                name = profileName.getText().toString();
                profileName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        name = profileName.getText().toString().trim();
                        if(name.length()>=3)
                            validateName();
                        validateName();

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        name = profileName.getText().toString().trim();
                        if (validateN()){

                            update.setEnabled(true);
                            update.setAlpha(1f);
                        }else {
                            update.setEnabled(false);
                            update.setAlpha(0.6f);
                        }

                        if(name.length()>=3)
                            validateName();
                        validateName();

                    }
                });
                email = profileEmail.getText().toString().trim();
                profileEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        email = profileEmail.getText().toString().trim();
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            validateEmail();}
                        validateEmail();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        email = profileEmail.getText().toString().trim();
                        if (validateN()){
                            update.setEnabled(true);
                            update.setAlpha(1f);
                        }else {
                            update.setEnabled(false);
                            update.setAlpha(0.6f);
                        }
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            validateEmail();}
                        validateEmail();
                    }
                });
                phoneNumber = profilePhone.getText().toString().trim();
                profilePhone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        phoneNumber = profilePhone.getText().toString().trim();
                        if (phoneNumber.length() == 13 && phoneNumber.indexOf(0)== '+')
                            validatePhone();
                        validatePhone();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        phoneNumber = profilePhone.getText().toString().trim();
                        if (validateN()){
                            update.setEnabled(true);
                            update.setAlpha(1f);
                        }else {
                            update.setEnabled(false);
                            update.setAlpha(0.6f);
                        }

                        if (phoneNumber.length() != 13 || phoneNumber.charAt(0)!= '+' || phoneNumber.charAt(1)!='9'|| phoneNumber.charAt(2)!='6'|| phoneNumber.charAt(3)!='6'|| phoneNumber.charAt(4)!='5')
                            validatePhone();
                        validatePhone();

                    }
                });

            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, UpdatePassword.class));
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                validateName();
                validateEmail();
                validatePhone();

                // will remove recalling methods
                if(validateName()&& validateEmail()&& validatePhone()) {
                    if(!Lemail.equals(email)){
                        firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this, "تم تغيير البريد الالكتروني بنجاح ", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(ProfileActivity.this, "فشلت عملية تغيير البريد الالكتروني", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        databaseReference.child("userEmail").setValue(email);

                    }

                    if(!Lname.equals(name))
                        databaseReference.child("userName").setValue(name);

                    if(!LphoneNumber.equals(phoneNumber)) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_verify_phone,null);
                        final PinView pinView = mView.findViewById(R.id.pinView);

                        sendVerificationCode(phoneNumber);
                        check =  mView.findViewById(R.id.buttonVerify);
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
                                    Toast.makeText(ProfileActivity.this,"أدخل رمز تحقق صحيح",Toast.LENGTH_SHORT).show();
                                    pinView.requestFocus();
                                    return;
                                }



                                verifyCode(code);
                            }
                        });
                        mBuilder.setView(mView);
                        AlertDialog dialog =mBuilder.create();
                        dialog.show();
                    }

                    if (filePath != null) {
                        storageRef = storageReference.child("images/" + UUID.randomUUID().toString());
                        storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        databaseReference.child("imageURL").setValue(uri.toString());
                                    }
                                });
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(), " تم تعديل معلوماتك", Toast.LENGTH_SHORT).show();

                }
                update.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                editProfile.setVisibility(View.VISIBLE);
                addImg.setVisibility(View.GONE);
                profileName.setEnabled(false);
                profileEmail.setEnabled(false);
                profilePhone.setEnabled(false);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
                builder1.setMessage("لن يتم حفظ التغييرات ، هل انت متأكد؟");
                builder1.setCancelable(true);

                builder1.setPositiveButton(

                        "نعم",
                        (dialog, id) -> {
                            update.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            addImg.setVisibility(View.GONE);
                            editProfile.setVisibility(View.VISIBLE);
                            profileName.setEnabled(false);
                            profileEmail.setEnabled(false);
                            profilePhone.setEnabled(false);
                        }
                        );

                builder1.setNegativeButton(
                        "إلغاء الامر",
                        (dialog, id) -> {
                            dialog.cancel();
                        }
                );

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);

            }
        });




    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            profilePic.setImageURI(filePath);
            flag = true;

        } else {
            if (filePath != null || sFlag) {
                profilePic.setImageURI(filePath);
            } else {
                findViewById(R.id.TextViewImage).setVisibility(View.VISIBLE);
                findViewById(R.id.img).setVisibility(View.GONE);
                findViewById(R.id.TextViewImageChange).setVisibility(View.GONE);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    private Boolean validateName(){

        if (name.isEmpty()) {
            profileName.setError("الرجاء ادخال اسم المستخدم");
            return false;
        }
        if (name.length() >= 3 ) {
            profileName.setError(null);
            return true;

        }else {
            profileName.setError("أدخل اسم مستخدم مكون من 3 خانات أو اكثر");
            return false;
        }
    }


    private Boolean validateEmail() {

        if (email.isEmpty()) {
            profileEmail.setError("الرجاء ادخال البريد الالكتروني");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            profileEmail.setError("صيغة البريد الالكتروني غير صحيحة");
            return false;
        }
        return true;
    }






    private Boolean validatePhone() {

        if (phoneNumber.isEmpty()) {
            profilePhone.setError("الرجاء ادخال رقم الجوال");
            return false;
        }
        else if (phoneNumber.length() != 13 || phoneNumber.charAt(0)!= '+' || phoneNumber.charAt(1)!='9'|| phoneNumber.charAt(2)!='6'|| phoneNumber.charAt(3)!='6'|| phoneNumber.charAt(4)!='5') {
            profilePhone.setError("صيغة الرقم غير صحيحة");
            return false;
        }
        return true;

    }
    private boolean validateN(){
        if (name.isEmpty() || email.isEmpty() ||  phoneNumber.isEmpty()) {
            return false;
        }
        return true;
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
            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void verifyCode(String code) {
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);}
        catch (Exception e){}
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseUser.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                            databaseReference.child("phone").setValue(phoneNumber);

                        }  else{
                            check.setProgress(0);
                            Toast.makeText(ProfileActivity.this, "رمز التحقق غير صحيح", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
