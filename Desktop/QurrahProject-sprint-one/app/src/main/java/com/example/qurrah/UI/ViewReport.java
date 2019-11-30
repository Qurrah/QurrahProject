
package com.example.qurrah.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.qurrah.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class ViewReport extends AppCompatActivity implements OnMapReadyCallback {

    private TextView title , description , name , yourReport, locDesLable, locDes , rType ;
    private ImageView photo;
    private String reporTitle , reportDescription , reportUser , reportWhatsApp , reportType ;
    private String reportImg, UserType, allowPhone;
    private LinearLayout chatting , whatsapp ,map ,noMap;
    private String  userID, latitude, longitude, locationDes;
    private static final String TAG = "viewReport";
    private FloatingActionButton fab_contact;    // , fab_whatsapp, fab_dChat;
    private FrameLayout fab_whatsapp, fab_dChat;

    Boolean isOpen = false;





    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        // Add a marker in a location.
        // and move the map's camera to the same location.
        if(!latitude.equals("") && !longitude.equals("")){
            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

            googleMap.addMarker(new MarkerOptions().position(location).icon(bitmapDescriptorFromVector(this,R.drawable.ic_location)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            googleMap.setMinZoomPreference(15);
            googleMap.setMaxZoomPreference(15);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }


    }


    @SuppressLint({"WrongConstant", "RestrictedApi"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_page);
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


        title = findViewById(R.id.reportTitle);
        description = findViewById(R.id.reportDes);
        photo = findViewById(R.id.reportImg);

        locDes = findViewById(R.id.locationDes);
        locDesLable = findViewById(R.id.locationDesLable);
        name = findViewById(R.id.reportOwner);
        rType = findViewById(R.id.reportType);

        yourReport = findViewById(R.id.yourReport);
        map = findViewById(R.id.mapLayout);
        noMap =findViewById(R.id.noMapLayout);


        //-----------------------------------


        // Floating buttons

        fab_contact = findViewById(R.id.fab);
        fab_dChat = findViewById(R.id.fab1);
        fab_whatsapp = findViewById(R.id.fab2);
//
//        fab_contact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isOpen) {
//                    showFABMenu();
//                } else {
//                    closeFABMenu();
//                }
//            }
//        });


        //-----------------------------------



        // get values from the prev activity
        userID = getIntent().getStringExtra("userid");
        reporTitle = getIntent().getStringExtra("Title");
        reportDescription = getIntent().getStringExtra("Description");
        reportImg = getIntent().getStringExtra("Image");
        reportUser = getIntent().getStringExtra("UserName");
        reportWhatsApp = getIntent().getStringExtra("WhatsApp");
        latitude = getIntent().getStringExtra("lat");
        longitude = getIntent().getStringExtra("lon");
        locationDes = getIntent().getStringExtra("locationDescription");
        UserType = getIntent().getStringExtra("userType");
        reportType = getIntent().getStringExtra("reportType");
        allowPhone = getIntent().getStringExtra("allowPhone");

        Toast.makeText(getApplicationContext(), "allow: "+ reportUser,Toast.LENGTH_SHORT).show();

        if (UserType == null || UserType.equals("none")) {
            UserType = "guest";
        } else if (UserType.equals("current")) {
            fab_whatsapp.setVisibility(View.GONE);
            fab_dChat.setVisibility(View.GONE);
            fab_contact.setVisibility(View.GONE);

            name.setText("لقد قمت بنشر هذا البلاغ");

        } else {
            name.setText(reportUser);
        }

//        || allowPhone.equals("false")
// reportWhatsApp.equals("0") ||

//        Toast.makeText(getApplicationContext(), "allow: "+ allowPhone,Toast.LENGTH_SHORT).show();

        if (allowPhone.equals("false")) {
            fab_whatsapp.setVisibility(View.GONE);
            fab_dChat.setVisibility(View.GONE);

            fab_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (UserType.equals("notCurrent")) {
                        Intent intent = new Intent(ViewReport.this, MessageActivity.class);
                        intent.putExtra("userid", userID);
                        startActivity(intent);
                    } else if (UserType.equals("guest")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewReport.this);
                        builder1.setMessage("يلزمك التسجيل لإجراء هذه المحادثة، هل تود التسجيل الآن؟");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "نعم",
                                (dialog, id) -> {
                                    finish();
                                    startActivity(new Intent(ViewReport.this, MainActivity.class));
                                });

                        builder1.setNegativeButton(
                                "إلغاء الامر",
                                (dialog, id) -> dialog.cancel());

                        AlertDialog alert11 = builder1.create();

                        alert11.show();
                    }
                }
            });
        }
        else{


            fab_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isOpen) {
                        showFABMenu();
                    } else {
                        closeFABMenu();
                    }
                }
            });

        }



        Log.d(TAG, "phone: " + reportWhatsApp);

        if(!(locationDes.equals("")) && !(locationDes.equals(null))) {
            locDesLable.setVisibility(View.VISIBLE);
            locDes.setVisibility(View.VISIBLE);
            locDes.setText(locationDes);
        }
        else if(locationDes.equals("") || locationDes.equals(null))
            locDesLable.setVisibility(View.GONE);





        // set values
        Picasso.get().load(reportImg).into(photo);
        title.setText(reporTitle);
        description.setText(reportDescription);
        rType.setText(reportType);

        // try to divide users

        fab_whatsapp.setOnClickListener(view -> {

            try {
                Uri uri = Uri.parse("smsto:" + reportWhatsApp);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(i, ""));
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text = "YOUR TEXT HERE";

            } catch (Exception e) {
                Toast.makeText(ViewReport.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
//            }

        });

        fab_dChat.setOnClickListener(view -> {

            if (UserType.equals("notCurrent")) {
                Intent intent = new Intent(ViewReport.this, MessageActivity.class);
                intent.putExtra("userid", userID);
                startActivity(intent);
            } else if (UserType.equals("guest")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewReport.this);
                builder1.setMessage("يلزمك التسجيل لإجراء هذه المحادثة، هل تود التسجيل الآن؟");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "نعم",
                        (dialog, id) -> {
                            finish();
                            startActivity(new Intent(ViewReport.this, MainActivity.class));
                        });

                builder1.setNegativeButton(
                        "إلغاء الامر",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();

                alert11.show();
            }
        });

        if(!latitude.equals("") && !longitude.equals("")){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.Map);
        mapFragment.getMapAsync(this);
        }else{
            map.setVisibility(View.GONE);
            noMap.setVisibility(View.VISIBLE);

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    // Floating buttons methods

    private void showFABMenu(){
        isOpen=true;
        fab_dChat.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab_whatsapp.animate().translationY(-getResources().getDimension(R.dimen.standard_105));

    }

    private void closeFABMenu(){
        isOpen=false;
        fab_dChat.animate().translationY(0);
        fab_whatsapp.animate().translationY(0);

    }



}

