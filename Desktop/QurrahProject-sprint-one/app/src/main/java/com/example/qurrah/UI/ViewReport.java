package com.example.qurrah.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class ViewReport extends AppCompatActivity implements OnMapReadyCallback {

private TextView title , description , name ;
private ImageView photo;
private String reporTitle , reportDescription , reportUser , reportWhatsApp  ;
private String reportImg;
private Button whatsapp , chatting;
private String  userID, latitude, longitude;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        // Add a marker in a location.
        // and move the map's camera to the same location.
        LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        googleMap.addMarker(new MarkerOptions().position(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.setMinZoomPreference(15);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        title= findViewById(R.id.reportTitle);
        description=findViewById(R.id.reportDes);
        photo = findViewById(R.id.reportImg);
        name = findViewById(R.id.reportUsername);
        whatsapp = findViewById(R.id.whatsapp);
        chatting = findViewById(R.id.chatting);

        // get values from the prev activity
        userID = getIntent().getStringExtra("userid");
        reporTitle =getIntent().getStringExtra("Title");
        reportDescription = getIntent().getStringExtra("Description");
        reportImg = getIntent().getStringExtra("Image");
        reportUser = getIntent().getStringExtra("UserName");
        reportWhatsApp = getIntent().getStringExtra("WhatsApp");
        latitude =  getIntent().getStringExtra("lat");
        longitude =  getIntent().getStringExtra("lon");

        // set values
        name.setText(reportUser);
        Picasso.get().load(reportImg).into(photo);
        title.setText(title.getText()+"\n"+reporTitle);
        description.setText(description.getText()+"\n"+reportDescription);



        whatsapp.setOnClickListener(view -> {

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
        });

        chatting.setOnClickListener(view -> {
            Intent intent = new Intent(ViewReport.this, MessageActivity.class);
            intent.putExtra("userid", userID);
            startActivity(intent);
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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


}
