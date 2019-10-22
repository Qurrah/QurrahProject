package com.example.qurrah.UI;





import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.example.qurrah.Model.Report;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class RegisteredUserReportView extends AppCompatActivity {

    private TextView title, description;
    private ImageView photo, img;
    private String reporTitle, reportDescription;
    private String reportImg;
    private Button update, save;
    AlertDialog dialog;
    EditText titleEdit, descriptionEdit;
    protected boolean flag = false;
    protected Uri filePath, previousImg;
    static boolean sFlag = false, textFlag = true;
    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    FirebaseStorage storage;
    StorageReference storageReference, storageRef;
    String userID, date, des;
    Report report;
    Editable titleText, desc;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registered_user_report_page);


        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Report");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        title = findViewById(R.id.reportTitle);
        description = findViewById(R.id.reportDes);
        photo = findViewById(R.id.reportImg);
        update = findViewById(R.id.update);
        save = findViewById(R.id.saveChanges);

        save.setVisibility(View.INVISIBLE);


        reporTitle = getIntent().getStringExtra("Title");
        reportDescription = getIntent().getStringExtra("Description");
        reportImg = getIntent().getStringExtra("Image");
        report = (Report) getIntent().getSerializableExtra("report");


        if (report.getReportStatus().equals("مغلق"))
            update.setVisibility(View.INVISIBLE);


        Picasso.get().load(reportImg).into(photo);
        title.setText(reporTitle);
        description.setText(reportDescription);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                builder1.setMessage("سوف يتم تعديل البلاغ، هل انت متأكد؟");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "نعم",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // dialog.cancel();
                                date = report.getDate();
                                des = report.getLostDescription();


                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Report rep = snapshot.getValue(Report.class);

                                            if (date.equals(rep.getDate()) && rep.getLostDescription().equals(des)) {
                                                if (filePath != null) {
                                                    final ProgressDialog progressDialog = new ProgressDialog(RegisteredUserReportView.this);
                                                    progressDialog.setTitle("يتم الان تعديل بلاغك...");
                                                    progressDialog.show();


                                                    storageRef = storageReference.child("images/" + UUID.randomUUID().toString());

                                                    storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    saveToDatabase(uri.toString());

                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });

                                                } else {
                                                    saveToDatabase(null);
                                                }
                                                ref.child(snapshot.getKey()).removeValue();

                                            }
//                                            else
//                                                Toast.makeText(RegisteredUserReportView.this, "لم يتم حفظ البلاغ بنجاح", Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
//                                Toast.makeText(RegisteredUserReportView.this, "تم حفظ بلاغك", Toast.LENGTH_SHORT).show();
                                save.setVisibility(View.INVISIBLE);

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
        });

    }








    private void ShowDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog, null);

        Button confirm = view.findViewById(R.id.confirm_edit);
        Button cancel = view.findViewById(R.id.cancel_edit);
        titleEdit = view.findViewById(R.id.title_edit);
        descriptionEdit = view.findViewById(R.id.description_edit);
        img = view.findViewById(R.id.img);


        if (filePath == null) {
            Picasso.get().load(reportImg).into(img);
        } else if (filePath != null) {
            img.setImageURI(filePath);
            previousImg = filePath;
        }

        if (textFlag) {
            titleEdit.setText(reporTitle);
            descriptionEdit.setText(reportDescription);
        } else {
            titleEdit.setText(title.getText());
            descriptionEdit.setText(description.getText());

        }




        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText = titleEdit.getText();
                desc = descriptionEdit.getText();

                title.setText(titleText);
                description.setText(desc);

                sFlag = true;
                photo.setImageURI(filePath);
                previousImg = filePath;
                dialog.cancel();
                save.setVisibility(View.VISIBLE);
                textFlag = false;

            }
        });





        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.show();

    }







    public void upload_img(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            img.setImageURI(filePath);
            flag = true;

        } else {
            if (filePath != null || sFlag) {
                photo.setImageURI(filePath);
            }
        }
    }




    private void saveToDatabase(String link) {
        if (link == null) {
            report.setPhoto(reportImg);
//            link = "https://i-love-png.com/images/no-image_7299.png";
        }else
            report.setPhoto(link);

        report.setLostTitle(titleText.toString());
        report.setLostDescription(desc.toString());


        ref.push().setValue(report);
        Toast.makeText(getApplicationContext(), " تم تعديل بلاغك", Toast.LENGTH_SHORT).show();

    }







}








