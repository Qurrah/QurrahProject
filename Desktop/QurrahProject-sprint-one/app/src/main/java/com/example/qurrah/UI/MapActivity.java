package com.example.qurrah.UI;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Parcelable;

import com.example.qurrah.Model.Report;
import com.example.qurrah.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.qurrah.Constants.COURSE_LOCATION;
import static com.example.qurrah.Constants.DEFAULT_ZOOM;
import static com.example.qurrah.Constants.FINE_LOCATION;
import static com.example.qurrah.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback ,
        GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MapActivity";


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private DatabaseReference reference;
    ArrayList<Report> reportsList;
    Double Dlatitude, Dlongitude;
    ArrayList<String> userList, phones;


    @Override
    public void onMapReady(GoogleMap googleMap) {
      //  Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMapType(mMap.MAP_TYPE_HYBRID);


        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    COURSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


        }


//        Toast.makeText(this, reportsList.get(0).getLatitude() , Toast.LENGTH_SHORT).show();



        for(int i = 0 ; i<reportsList.size() ; i++) {
            Dlatitude = Double.parseDouble(reportsList.get(i).getLatitude());
            Dlongitude = Double.parseDouble(reportsList.get(i).getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Dlatitude, Dlongitude))
                    .title("Title: "+reportsList.get(i).getLostTitle())
                    .snippet("Category: "+reportsList.get(i).getCategoryOption()+"\nType: "+reportsList.get(i).getReportTypeOption())
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_location)));
        }


        mMap.setOnInfoWindowClickListener(this);


    }




    @Override
    public void onInfoWindowClick(Marker marker) {

        for (int i=0; i< reportsList.size(); i++) {

            LatLng latLng = new LatLng(Double.parseDouble(reportsList.get(i).getLatitude()) , Double.parseDouble(reportsList.get(i).getLongitude()));
           if(latLng.equals(marker.getPosition())){
                Intent intent = new Intent(MapActivity.this, ViewReport.class);
//                    intent.putExtra("Report", (Parcelable) reports.get(position));
                intent.putExtra("Image", reportsList.get(i).getPhoto());
                intent.putExtra("Title", reportsList.get(i).getLostTitle());
                intent.putExtra("Description", reportsList.get(i).getLostDescription());
                intent.putExtra("UserName", userList.get(i));
                intent.putExtra("WhatsApp", phones.get(i));
                intent.putExtra("lat",reportsList.get(i).getLatitude());
                intent.putExtra("lon",reportsList.get(i).getLongitude());
                startActivity(intent);
            }
//                intent.putExtra("userid" , id);


            }

        }


//        Toast.makeText(this, "Info window clicked",
//                Toast.LENGTH_SHORT).show();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        reportsList = (ArrayList) getIntent().getParcelableArrayListExtra("reportsLoc");
        userList = getIntent().getStringArrayListExtra("userList");
        phones = getIntent().getStringArrayListExtra("phoneNumbers");
//        Toast.makeText(this, reportsList.get(0).getLatitude() , Toast.LENGTH_SHORT).show();
        getLocationPermission();

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
//                mMap.clear();
                final Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("انت"));
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            }


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            //initialize our map
            initMap();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = true;

                }
            }
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
