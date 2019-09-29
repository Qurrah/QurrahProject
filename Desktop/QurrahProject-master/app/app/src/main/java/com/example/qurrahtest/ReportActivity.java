package com.example.qurrahtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kofigyan.stateprogressbar.StateProgressBar;
import static android.text.TextUtils.isEmpty;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;


public class ReportActivity extends AppCompatActivity {


   private  TextInputLayout lostTitle, lostDescription, senderName, phoneNumber, location;
   private String lostTitleInput, lostDescriptionInput, senderNameInput, phoneNumberInput, radioValue, imgURI, userID;
   private  StateProgressBar stateProgressBar;
   private boolean flag = false;
   private Uri filePath;
   ImageView img;
   DatabaseReference ref;
   FirebaseStorage storage;
   StorageReference storageReference, storageRef;
   Report report;
   RadioGroup rg;
   RadioButton radioButton;
   private FirebaseAuth mAuth;
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mAuth = FirebaseAuth.getInstance();
        setupUI(findViewById(R.id.parent));
        String[] descriptionData = {getString(R.string.lost_info), getString(R.string.sender_info), getString(R.string.location_photo)};
        stateProgressBar = findViewById(R.id.StateProgressBarID);
        stateProgressBar.setStateDescriptionData(descriptionData);

        img = findViewById(R.id.img);
//        phoneNumber = findViewById(R.id.SenderPhoneNumber);
        lostTitle = findViewById(R.id.LostTitle);
        lostDescription = findViewById(R.id.LostDescription);
//        senderName = findViewById(R.id.SenderName);
        location = findViewById(R.id.Location);

        // HARD CODED DUMMMMY
        userID = mAuth.getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Reports");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        report = new Report();

        lostTitle.getEditText().addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        lostTitleInput = lostTitle.getEditText().getText().toString().trim();
        validateLostTitle();
    }
});
        lostDescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lostDescriptionInput = lostDescription.getEditText().getText().toString().trim();
                validateLostDescription();
            }
        });
//        senderName.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                senderNameInput = senderName.getEditText().getText().toString().trim();
//                validateSenderName();
//
//            }
//        });
//        phoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                phoneNumberInput = phoneNumber.getEditText().getText().toString().trim();
//                validatePhoneNumber();
//
//            }
//        });



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(ReportActivity.this);
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



    public void upload_img(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,100);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        findViewById(R.id.TextViewImageChange).setVisibility(View.VISIBLE);
        findViewById(R.id.TextViewImage).setVisibility(View.GONE);
        findViewById(R.id.img).setVisibility(View.VISIBLE);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            img.setImageURI(filePath);
            flag = true;

        }else {
            if (filePath != null){
                img.setImageURI(filePath);
            }else {
                findViewById(R.id.TextViewImage).setVisibility(View.VISIBLE);
                findViewById(R.id.img).setVisibility(View.GONE);
                findViewById(R.id.TextViewImageChange).setVisibility(View.GONE);
            }
        }

    }



    public boolean validateLostTitle() {
            if (!isEmpty(lostTitleInput)){
                lostTitle.setError(null);
                return true;
            }
           else {
                lostTitle.setError("لا يمكن ترك هذه الخانة فارغة");
                return false;
            }
    }


    public boolean validateLostDescription() {
        if (!isEmpty(lostDescriptionInput)){
            lostDescription.setError(null);
            return true;
        }
        else {
            lostDescription.setError("لا يمكن ترك هذه الخانة فارغة");
            return false;

        }
    }


    private void hideLostInfo(){
        // اخفاء معلومات المفقود
        findViewById(R.id.LostTitle).setVisibility(View.GONE);
        findViewById(R.id.LostDescription).setVisibility(View.GONE);
        findViewById(R.id.CategoryGroup).setVisibility(View.GONE);
        findViewById(R.id.LostInfoNext).setVisibility(View.GONE);
    }


    private void showLostInfo(){
        // اخفاء معلومات المفقود
        findViewById(R.id.LostTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.LostDescription).setVisibility(View.VISIBLE);
        findViewById(R.id.CategoryGroup).setVisibility(View.VISIBLE);
        findViewById(R.id.LostInfoNext).setVisibility(View.VISIBLE);
    }


//    private void hideSenderInfo(){
//        //اخفاء معلومات المرسل
//        findViewById(R.id.SenderName).setVisibility(View.GONE);
//        findViewById(R.id.SenderPhoneNumber).setVisibility(View.GONE);
//        findViewById(R.id.SenderInfoBack).setVisibility(View.GONE);
//        findViewById(R.id.SenderInfoNext).setVisibility(View.GONE);
//    }
//
//
//    private void showSenderInfo(){
//        //اظهار معلومات المرسل
//        findViewById(R.id.SenderName).setVisibility(View.VISIBLE);
//        findViewById(R.id.SenderPhoneNumber).setVisibility(View.VISIBLE);
//        findViewById(R.id.SenderInfoBack).setVisibility(View.VISIBLE);
//        findViewById(R.id.SenderInfoNext).setVisibility(View.VISIBLE);
//    }


    private void showPhotoAndLocationInfo(){
        findViewById(R.id.Location).setVisibility(View.VISIBLE);
        findViewById(R.id.LocationPhotoSubmit).setVisibility(View.VISIBLE);
//        findViewById(R.id.BackToSenderInfo).setVisibility(View.VISIBLE);
        if (flag){
        findViewById(R.id.img).setVisibility(View.VISIBLE);
        findViewById(R.id.TextViewImageChange).setVisibility(View.VISIBLE);
        }else {
        findViewById(R.id.TextViewImage).setVisibility(View.VISIBLE);
        }

    }


    private void hidePhotoAndLocationInfo(){
        findViewById(R.id.img).setVisibility(View.GONE);
        findViewById(R.id.TextViewImageChange).setVisibility(View.GONE);
        findViewById(R.id.TextViewImage).setVisibility(View.GONE);
        findViewById(R.id.Location).setVisibility(View.GONE);
        findViewById(R.id.LocationPhotoSubmit).setVisibility(View.GONE);
//        findViewById(R.id.BackToSenderInfo).setVisibility(View.GONE);
    }


    public void LostInfoNextValidate(View view) {
            if (!validateLostTitle() | !validateLostDescription())
            {return; }
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
             hideLostInfo();
             showPhotoAndLocationInfo();

    }



    public void backToLostInfo(View view) {
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
        hidePhotoAndLocationInfo();
        showLostInfo();

    }


    public void NextToPhotoAndLocation(View view) {
        if (!validateSenderName() | !validatePhoneNumber()){
            return;
        }
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
        showPhotoAndLocationInfo();
        hideLostInfo();

    }


    public void backToSenderInfo(View view) {
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        //showSenderInfo();
        hidePhotoAndLocationInfo();
    }

    private boolean validateSenderName(){
        if (!isEmpty(senderNameInput)){
            senderName.setError(null);
            return true;
        }
        else {
            senderName.setError("لا يمكن ترك هذه الخانة فارغة");
            return false;
        }
    }


    private boolean validatePhoneNumber(){
        if (isEmpty(phoneNumberInput)){
            phoneNumber.setError("لا يمكن ترك هذه الخانة فارغة");
            return false;
        }
        if (!phoneNumberInput.matches("05[0-9]{8}") ){
            phoneNumber.setError("رقم الهاتف غير صحيح");
            return false;

        }

            phoneNumber.setError(null);
            return true;

    }


    public void sendAllInfo(View view) {

        // the updated choice of the radio
        //-----------------------------------------
        rg = findViewById(R.id.CategoryGroup);
        int radioID = rg.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        radioValue = radioButton.getText().toString();
        //-----------------------------------------

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReportActivity.this);
        builder1.setMessage("سوف يتم ارسال طلبك، هل انت متأكد؟");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    //  dialog.cancel();
                        getValues();
                        ref.push().setValue(report);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(filePath != null)
                                {
//                                    final ProgressDialog progressDialog = new ProgressDialog(ReportActivity.this);
//                                    progressDialog.setTitle("Uploading...");
//                                    progressDialog.show();

                                    storageRef = storageReference.child("images/"+ UUID.randomUUID().toString());

                                    storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                 //   Toast.makeText(getApplicationContext(), "onSuccess: uri= "+ uri.toString(),Toast.LENGTH_SHORT).show();
                                                     imgURI = uri.toString();

                                                }
                                            });
                                        }
                                    });
//
                                }

                                Toast.makeText(getApplicationContext(), " تم ارسال طلبك", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ReportActivity.this, SecondActivity.class);
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });


                    }

                });

        builder1.setNegativeButton(
                "إلغاء الامر",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();

        alert11.show();

    }
    private void getValues(){
        report.setLostTitle(lostTitleInput);
        report.setLostDescription(lostDescriptionInput);
        report.setCategoryOption(radioValue);
        report.setSenderName(senderNameInput);
        report.setPhoneNumber(phoneNumberInput);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String date = dateFormat.format(new Date());
        report.setDate(date);

        if (imgURI == null) {
            imgURI = "https://i-love-png.com/images/no-image_7299.png";
        }

        report.setPhoto(imgURI);
        report.setLocation(location.getEditText().getText().toString().trim());

    }

}
