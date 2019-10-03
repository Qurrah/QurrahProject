package com.example.qurrah;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.UUID;

public class EditReportActivity extends ReportActivity {
    DatabaseReference databaseReferenceUserReportEdit;
    String date,photoUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button update = findViewById(R.id.LocationPhotoSubmit);
//------------------------------------------------
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//------------------------------------------------
        update.setText("تحديث");

        RadioButton radioButton1, radioButton2, radioButton3,radioButton4,radioButton5;
        Bundle extras = getIntent().getExtras();
        lostTitle.getEditText().setText(extras.getString("lostTitle"));
        lostDescription.getEditText().setText(extras.getString("lostDescription"));
        photoUrl = extras.getString("photo");
        sFlag = true;

        if (!extras.getString("photo").equals("https://i-love-png.com/images/no-image_7299.png")) {
            Picasso.get().load(extras.getString("photo")).into(img);

            findViewById(R.id.LostInfoNext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LostInfoNextValidate(v);
                    img.setVisibility(View.VISIBLE);
                    findViewById(R.id.TextViewImageChange).setVisibility(View.VISIBLE);
                    findViewById(R.id.TextViewImage).setVisibility(View.GONE);
                }
            });
        }

        location.getEditText().setText(extras.getString("location"));
        date = extras.getString("date");
        radioButton1 = findViewById(R.id.HumanRadio);
        radioButton2 = findViewById(R.id.AnimalRadio);
        radioButton3 = findViewById(R.id.DevicesRadio);
        if (radioButton1.getText().equals(extras.getString("categoryOption"))) {
            radioButton1.setChecked(true);
        } else if (radioButton2.getText().equals(extras.getString("categoryOption"))) {
            radioButton2.setChecked(true);
        } else if (radioButton3.getText().equals(extras.getString("categoryOption"))) {
            radioButton3.setChecked(true);
        }
        radioButton4 = findViewById(R.id.FoundRadio);
        radioButton5 = findViewById(R.id.LostRadio);
        if (radioButton4.getText().equals(extras.getString("ReportTypeOption"))) {
            radioButton4.setChecked(true);
        } else if (radioButton5.getText().equals(extras.getString("ReportTypeOption"))) {
            radioButton5.setChecked(true);
        }
        databaseReferenceUserReportEdit = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Report").child(extras.getString("key")).getRef();
    }

    public void sendAllInfo(View view) {

        // the updated choice of the radio
        //-----------------------------------------
        rg = findViewById(R.id.CategoryGroup);
        int radioID = rg.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        radioValue = radioButton.getText().toString();
        //-----------------------------------------
        //////////////////////////////////////////
        ReportTypeRadio=findViewById(R.id.ReportType);
        int TypeRadioId=ReportTypeRadio.getCheckedRadioButtonId();
        typeButton=findViewById(TypeRadioId);
        ReportType=typeButton.getText().toString();


        AlertDialog.Builder builder1 = new AlertDialog.Builder(EditReportActivity.this);
        builder1.setMessage("سوف يتم تحديث بلاغك، هل انت متأكد؟");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        databaseReferenceUserReportEdit.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (filePath != null) {
                                    final ProgressDialog progressDialog = new ProgressDialog(EditReportActivity.this);
                                    progressDialog.setTitle("يتم الان ارسال بلاغك...");
                                    progressDialog.show();


                                    storageRef = storageReference.child("images/" + UUID.randomUUID().toString());

                                    storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    saveToDatabase(uri.toString());
                                                    // Toast.makeText(getApplicationContext(), "onSuccess: uri= "+ uri.toString(),Toast.LENGTH_SHORT).show();

                                                    progressDialog.dismiss();



                                                }
                                            });
                                        }
                                    });
//
                                } else{
                                    saveToDatabase(null);
                                }


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

    private void getValues(String link) {
        report.setLostTitle(lostTitleInput);
        report.setLostDescription(lostDescriptionInput);
        report.setCategoryOption(radioValue);
        report.setReportTypeOption(ReportType);
        report.setDate(date);
        if (link == null) {
            link = photoUrl;
        }
        report.setPhoto(link);
        report.setLocation(location.getEditText().getText().toString().trim());
    }

    private void saveToDatabase(String link) {
        getValues(link);
        databaseReferenceUserReportEdit.setValue(report);
        Toast.makeText(getApplicationContext(), " تم تحديث بلاغك", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditReportActivity.this, MyReport.class);
        startActivity(intent);
    }
}

