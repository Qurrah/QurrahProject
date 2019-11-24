
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
import com.squareup.picasso.Picasso;

public class ViewReport extends AppCompatActivity implements OnMapReadyCallback {

    private TextView title , description , name , yourReport;
    private ImageView photo;
    private String reporTitle , reportDescription , reportUser , reportWhatsApp  ;
    private String reportImg, UserType;
    private LinearLayout chatting , whatsapp ,map ,noMap;
    private String  userID, latitude, longitude;
    private static final String TAG = "viewReport";




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


    @SuppressLint("WrongConstant")
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
//        name = findViewById(R.id.reportUsername);
        yourReport = findViewById(R.id.yourReport);
        whatsapp = findViewById(R.id.whatsapp);
        chatting = findViewById(R.id.chatting);
        map = findViewById(R.id.mapLayout);
        noMap =findViewById(R.id.noMapLayout);
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
        UserType = getIntent().getStringExtra("userType");
        if (UserType == null || UserType.equals("none")) {
            UserType = "guest";
        } else if (UserType.equals("current")) {
            whatsapp.setVisibility(View.GONE);
            chatting.setVisibility(View.GONE);
//            name.setVisibility(View.INVISIBLE);
            yourReport.setVisibility(View.VISIBLE);
        } else {
//            name.setText(reportUser);
        }

        if (reportWhatsApp.equals("0")) {
            whatsapp.setVisibility(View.GONE);
        }
        Log.d(TAG, "phone: " + reportWhatsApp);


        // set values
        Picasso.get().load(reportImg).into(photo);
        title.setText(reporTitle);
        description.setText(reportDescription);

        // try to divide users

        whatsapp.setOnClickListener(view -> {
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

        chatting.setOnClickListener(view -> {

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

}

