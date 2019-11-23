
package com.example.qurrah.UI;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView title , description , name , yourReport, locDesLable, locDes ;
    private ImageView photo;
    private String reporTitle , reportDescription , reportUser , reportWhatsApp  ;
    private String reportImg, UserType;
    private LinearLayout chatting , whatsapp ,map ,noMap;
    private String  userID, latitude, longitude, locationDes;
    private static final String TAG = "viewReport";
    private FloatingActionButton fab_contact, fab_whatsapp, fab_dChat;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    Boolean isOpen = true;




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

        yourReport = findViewById(R.id.yourReport);
//        whatsapp = findViewById(R.id.whatsapp);
//        chatting = findViewById(R.id.chatting);
        map = findViewById(R.id.mapLayout);
        noMap =findViewById(R.id.noMapLayout);
        //-----------------------------------
        // Floating buttons

        fab_contact = findViewById(R.id.fab);
        fab_dChat = findViewById(R.id.fab1);
        fab_whatsapp = findViewById(R.id.fab2);


//        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
//        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
//        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_clock);
//        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_anticlock);




        fab_contact.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {


                if(isOpen){

//                    fab_dChat.setVisibility(View.VISIBLE);
//                    fab_whatsapp.setVisibility(View.VISIBLE);
//                    fab_dChat.startAnimation(fab_open);
//                    fab_whatsapp.startAnimation(fab_open);
//                    fab_contact.startAnimation(fab_clock);

                    fab_contact.setVisibility(View.VISIBLE);
                    fab_dChat.setVisibility(View.VISIBLE);
                    fab_whatsapp.setVisibility(View.VISIBLE);


                    fab_dChat.setClickable(true);
                    fab_whatsapp.setClickable(true);
                    isOpen=false;
                }
                else{

//                    fab_dChat.setVisibility(View.INVISIBLE);
//                    fab_whatsapp.setVisibility(View.INVISIBLE);
//                    fab_dChat.startAnimation(fab_close);
//                    fab_whatsapp.startAnimation(fab_close);
//                    fab_contact.startAnimation(fab_anticlock);

                    fab_contact.setVisibility(View.VISIBLE);
                    fab_dChat.setVisibility(View.INVISIBLE);
                    fab_whatsapp.setVisibility(View.INVISIBLE);

                    fab_dChat.setClickable(false);
                    fab_whatsapp.setClickable(false);
                    isOpen=true;

                }


            }
        });




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
        if (UserType == null || UserType.equals("none")) {
            UserType = "guest";
        } else if (UserType.equals("current")) {
//            whatsapp.setVisibility(View.GONE);
//            chatting.setVisibility(View.GONE);
            fab_whatsapp.setVisibility(View.GONE);
            fab_dChat.setVisibility(View.GONE);
            fab_contact.setVisibility(View.GONE);

            name.setText("لقد قمت بنشر هذا البلاغ");
//            yourReport.setVisibility(View.VISIBLE);
        } else {
            name.setText(reportUser);
        }

        if (reportWhatsApp.equals("0")) {
//            whatsapp.setVisibility(View.GONE);
            fab_whatsapp.setVisibility(View.GONE);
        }
        Log.d(TAG, "phone: " + reportWhatsApp);

        if(!(locationDes.equals(""))|| !(locationDes.equals(null))) {
            locDesLable.setVisibility(View.VISIBLE);
            locDes.setVisibility(View.VISIBLE);
            locDes.setText(locationDes);
        }


        // set values
        Picasso.get().load(reportImg).into(photo);
        title.setText(reporTitle);
        description.setText(reportDescription);

        // try to divide users

        fab_whatsapp.setOnClickListener(view -> {
//            if(reportWhatsApp.equals("0")){
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewReport.this);
//                builder1.setMessage("صاحب البلاغ لا يفضل التواصل عن طريق الواتساب");
//                builder1.setCancelable(true);
//                builder1.setPositiveButton(
//                        "حسناً",
//                        (dialog, id) -> dialog.cancel());
//                AlertDialog alert11 = builder1.create();
//                alert11.show();
//            }
//            else {
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
                .findFragmentById(R.id.map);
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

}

