package com.example.qurrah.UI;





import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import static com.example.qurrah.Constants.REQUEST_PLACE_PICKER_CODE;
import com.example.qurrah.Kotlin.PickLocationActivity;
import com.example.qurrah.R;
import com.example.qurrah.Model.Report;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import static com.example.qurrah.Constants.REQUEST_PLACE_PICKER_CODE;

public class RegisteredUserReportView extends AppCompatActivity implements OnMapReadyCallback {

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
    String userID, date, des ,latitude, longitude;
    Report report;
    Editable titleText, desc;
    LinearLayout placePicker;
    String address;
    TextView tvaddress;
    ImageView imageViewAddress;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        // Add a marker in a location.
        // and move the map's camera to the same location.
        LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        googleMap.addMarker(new MarkerOptions().position(location).icon(bitmapDescriptorFromVector(this,R.drawable.ic_location)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.setMinZoomPreference(15);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registered_user_report_page);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Report");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        placePicker = findViewById(R.id.pickPlace);
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
        latitude =  getIntent().getStringExtra("lat");
        longitude =  getIntent().getStringExtra("lon");

        if (report.getReportStatus().equals("مغلق"))
            update.setVisibility(View.INVISIBLE);


        Picasso.get().load(reportImg).into(photo);
        title.setText(reporTitle);
        description.setText(reportDescription);


        update.setOnClickListener(view -> ShowDialog());


        save.setOnClickListener(v -> {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
            builder1.setMessage("سوف يتم تعديل البلاغ، هل انت متأكد؟");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "نعم",
                    (dialog, id) -> {

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

                    });

            builder1.setNegativeButton(
                    "إلغاء الامر",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();

            alert11.show();

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
        tvaddress = view.findViewById(R.id.address);
        imageViewAddress = view.findViewById(R.id.imageAddress);


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





        confirm.setOnClickListener(view12 -> {
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

        });





        cancel.setOnClickListener(view1 -> dialog.cancel());


        dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.show();

    }
    public void pickPlace(View view) {
        startActivityForResult(new Intent(getApplicationContext(), PickLocationActivity.class), REQUEST_PLACE_PICKER_CODE);
    }







    public void upload_img(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        findViewById(R.id.TextViewImageChange).setVisibility(View.VISIBLE);
//        findViewById(R.id.TextViewImage).setVisibility(View.GONE);
//        findViewById(R.id.img).setVisibility(View.VISIBLE);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            img.setImageURI(filePath);
            flag = true;

        } else {
            if (filePath != null || sFlag) {
                img.setImageURI(filePath);
            } else {
//                findViewById(R.id.TextViewImage).setVisibility(View.VISIBLE);
//                findViewById(R.id.img).setVisibility(View.GONE);
//                findViewById(R.id.TextViewImageChange).setVisibility(View.GONE);
            }
        }
        if (requestCode == REQUEST_PLACE_PICKER_CODE) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                latitude = data.getStringExtra("Latitude");
                longitude = data.getStringExtra("Longitude");
                address = data.getStringExtra("Address");
                tvaddress.setText(address.trim().replaceAll(" +", " "));
                imageViewAddress.setBackground(getResources().getDrawable(R.drawable.ic_location));


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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




}