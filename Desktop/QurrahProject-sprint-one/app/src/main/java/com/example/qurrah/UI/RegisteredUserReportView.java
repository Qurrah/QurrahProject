package com.example.qurrah.UI;





import android.annotation.SuppressLint;
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
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;
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

    private EditText title, description , mapDescription;   // changed this from textview to editable
    private ImageView photo, img;
    private String reporTitle, reportDescription;
    private String reportImg;
    private Button update ,save;
    private TextView noLocLable;
    private ImageView addImg;
    private TextView LocationText;
    private LinearLayout location;

//    AlertDialog dialog;
//    EditText titleEdit, descriptionEdit, locationDescriptionEdit;
    protected boolean flag = false, flag1= false;
    protected Uri filePath, previousImg;
    static boolean sFlag = false, textFlag = true;
    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    FirebaseStorage storage;
    StorageReference storageReference, storageRef;
    String userID, date, des ,latitude, longitude;
    Report report;
    Editable titleText, desc;
    String addressDesc , address1;
    TextView tvaddress ,locDesLable ;
    ImageView imageViewAddress;
    GoogleMap mMap;
    String editedTitle="", editedDesc="", editedAddress="",editedLocation="", editedLongitude="", editedLatitude="";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        findViewById(R.id.map).setVisibility(View.VISIBLE);

        // Add a marker in a location.
        // and move the map's camera to the same location.
        if (latitude!=null && longitude != null && latitude.length() >0 && longitude.length() >0) {
            findViewById(R.id.map).setVisibility(View.VISIBLE);
            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            mMap.addMarker(new MarkerOptions().position(location).icon(bitmapDescriptorFromVector(this, R.drawable.ic_location)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.setMinZoomPreference(15);
            mMap.setMapType(mMap.MAP_TYPE_HYBRID);
        }else {
            findViewById(R.id.map).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registered_user_report_page);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        //----------------------------------------------------------------
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("البلاغ");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        //----------------------------------------------------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        //هذا
        locDesLable = findViewById(R.id.locationDesLable);
        mapDescription = findViewById(R.id.locationDes);
        noLocLable = findViewById(R.id.noLoc);
        addImg = findViewById(R.id.img_plus);
        LocationText = findViewById(R.id.locText);
        location = findViewById(R.id.pickPlace);
        tvaddress = findViewById(R.id.address);


//       save.setVisibility(View.INVISIBLE);


        reporTitle = getIntent().getStringExtra("Title");
        reportDescription = getIntent().getStringExtra("Description");
        reportImg = getIntent().getStringExtra("Image");
        report = (Report) getIntent().getSerializableExtra("report");
        latitude = getIntent().getStringExtra("lat");
        longitude = getIntent().getStringExtra("lon");
        address1 = getIntent().getStringExtra("address");
        addressDesc = getIntent().getStringExtra("locationDescription");


        if (!(getIntent().getStringExtra("locationDescription").equals(""))) {
            mapDescription.setVisibility(View.VISIBLE);
//            findViewById(R.id.imageAddress).setVisibility(View.VISIBLE);
            locDesLable.setVisibility(View.VISIBLE);
            mapDescription.setText(addressDesc);

        } else if (getIntent().getStringExtra("locationDescription").equals("")) {
            mapDescription.setVisibility(View.GONE);
            locDesLable.setVisibility(View.GONE);
        }

        if (getIntent().getStringExtra("address") == null)
            noLocLable.setVisibility(View.VISIBLE);
//        if(!addressDesc.equals("")){
//            locDesLable.setVisibility(View.VISIBLE);
//            mapDescription.setText(addressDesc);
//
//        }

        if (report.getReportStatus().equals("مغلق"))
            update.setVisibility(View.INVISIBLE);


        Picasso.get().load(reportImg).into(photo);
        title.setText(reporTitle);
        description.setText(reportDescription);
        mapDescription.setText(addressDesc);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                update.setVisibility(View.VISIBLE);
                save.setVisibility(View.GONE);
                addImg.setVisibility(View.GONE);
                location.setVisibility(View.GONE);
                LocationText.setVisibility(View.VISIBLE);
                title.setEnabled(false);
                description.setEnabled(false);
                mapDescription.setEnabled(false);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        date = report.getDate();
                        des = report.getLostDescription();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Report rep = snapshot.getValue(Report.class);

                            if (date.equals(rep.getDate()) && rep.getLostDescription().equals(des)) {
                                if (filePath != null) {
                                    final ProgressDialog progressDialog = new ProgressDialog(RegisteredUserReportView.this);
                                    progressDialog.setTitle("يتم الان تعديل بلاغك...");
                                    progressDialog.show();


                                    storageRef = storageReference.child("images/" + UUID.randomUUID().toString());

                                    storageRef.putFile(filePath).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        editInDatabase(uri.toString());

                                        progressDialog.dismiss();
                                    }));

                                } else {
                                    editInDatabase(null);
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


            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                update.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                addImg.setVisibility(View.VISIBLE);
                location.setVisibility(View.VISIBLE);
                LocationText.setVisibility(View.GONE);
                mapDescription.setVisibility(View.VISIBLE);
                locDesLable.setVisibility(View.VISIBLE);

//                cancel.setVisibility(View.VISIBLE);
                title.setEnabled(true);
                description.setEnabled(true);
                mapDescription.setEnabled(true);
                editedTitle = title.getText().toString();
                editedDesc = description.getText().toString();
                editedLocation = mapDescription.getText().toString();
                title.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editedTitle = title.getText().toString();
                        if (validateN()) {
                            save.setEnabled(true);
                            save.setAlpha(1f);
                        } else {
                            save.setEnabled(false);
                            save.setAlpha(0.6f);
                        }


                    }
                });
                mapDescription.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editedLocation = mapDescription.getText().toString();

                    }
                });


                description.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editedDesc = description.getText().toString();
                        if (validateN()) {
                            save.setEnabled(true);
                            save.setAlpha(1f);
                        } else {
                            save.setEnabled(false);
                            save.setAlpha(0.6f);
                        }


                    }
                });

            }
        });

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
            photo.setImageURI(filePath);
            flag = true;

        } else {
            if (filePath != null || sFlag) {
                photo.setImageURI(filePath);
            }
        }
        if (requestCode == REQUEST_PLACE_PICKER_CODE) {
            if (resultCode == RESULT_OK) {
                mapDescription.setVisibility(View.VISIBLE);
                noLocLable.setVisibility(View.GONE);



                // Get String data from Intent
                latitude = data.getStringExtra("Latitude");
                longitude = data.getStringExtra("Longitude");
                address1 = data.getStringExtra("Address");
                tvaddress.setText(editedAddress.trim().replaceAll(" +", " "));
                mMap.clear();

                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.setMapType(mMap.MAP_TYPE_HYBRID);
                markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_location));
                markerOptions.getPosition();
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));



            }
        }
    }




    private void editInDatabase(String link) {
        if (link == null) {
            report.setPhoto(reportImg);
//            link = "https://i-love-png.com/images/no-image_7299.png";
        } else{
            report.setPhoto(link);
        }

        report.setLostTitle(editedTitle);
        report.setLostDescription(editedDesc);
        report.setLatitude(latitude);
        report.setLongitude(longitude);
        report.setAddress(address1);
        report.setLocation(editedLocation);


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
    public void pickPlace(View view) {
        startActivityForResult(new Intent(getApplicationContext(), PickLocationActivity.class), REQUEST_PLACE_PICKER_CODE);
    }
    private boolean validateN(){
        if (editedTitle.isEmpty() || editedDesc.isEmpty()) {
            return false;
        }
        return true;
    }




}