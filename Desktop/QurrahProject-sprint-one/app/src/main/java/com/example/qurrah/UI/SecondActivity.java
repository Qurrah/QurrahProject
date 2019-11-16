package com.example.qurrah.UI;

import android.Manifest;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.qurrah.LocationTrackingServices.LocationJobService;
import com.example.qurrah.LocationTrackingServices.LocationTracking;
import com.example.qurrah.Model.Report;
import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.UI.ReportTypes.AnimalReport;
import com.example.qurrah.UI.ReportTypes.DeviceReport;
import com.example.qurrah.UI.ReportTypes.HumanReport;
import com.example.qurrah.UI.ReportTypes.OtherReport;
import com.example.qurrah.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;


import java.util.ArrayList;

import static com.example.qurrah.Constants.ERROR_DIALOG_REQUEST;
import static com.example.qurrah.Constants.FINE_LOCATION;
import static com.example.qurrah.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class SecondActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    CardView people_card,animal_card,device_card,other_card;
    BottomAppBar bottomAppBar;
    TextView username , test;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private boolean mLocationPermissionGranted = false;
    private static final String TAG = "SecondActivity";
    Intent intent;
    //    ArrayList<String> LatitudeList;
//    ArrayList<String> LongitudeList;
    ArrayList<Report> reportsList;  // array of reports that contain a location
    ArrayList<String> userList, phones , IdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nav);
        //overridePendingTransition(0, 0);
        // requestLocationPermission();
        intent = getIntent();
        String activity = intent.getStringExtra("from");
        if (activity.equalsIgnoreCase("FirstPage")) {
            Animation hold = AnimationUtils.loadAnimation(this, R.anim.translate_scale);
            findViewById(R.id.CategoryGroup).startAnimation(hold);
        }
        showPermissionDialog();

        final DrawerLayout navDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        username = (TextView) header.findViewById(R.id.Username);

        reportsList = new ArrayList<>();
        userList = new ArrayList<>();
        phones = new ArrayList<>();
        IdList=new ArrayList<>();
//---------------------------------------------------

        NavigationView mNavigationView =findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
//---------------------------------------------------
        bottomAppBar = findViewById(R.id.bottomAppBar);
        //setSupportActionBar(bottomAppBar);
        bottomAppBar.replaceMenu(R.menu.map_menu);
//---------------------------------------------------
        bottomAppBar.setNavigationOnClickListener(v -> {
            //   Toast.makeText(getApplicationContext(),"nav clicked",Toast.LENGTH_SHORT).show();
            // If navigation drawer is not open yet, open it else close it.
            if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(GravityCompat.START);
            else navDrawer.closeDrawer(GravityCompat.END);

        });
//---------------------------------------------------

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String userId=firebaseAuth.getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference().child("Users"); //.child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.child(userId).getValue(UserProfile.class);
                username.setText(userProfile.getUserName());

                reportsList.clear();
                userList.clear();
                phones.clear();
                IdList.clear();


                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    UserProfile userInfo = snapshot.getValue(UserProfile.class);
                    String ID = userInfo.getId();
                    String userName = userInfo.getUserName();
                    String No = userInfo.getPhone();
                    String allowPhoneAccess=userInfo.getAllowPhone();


                    for (DataSnapshot ds: snapshot.child("Report").getChildren()) {
                        Report report = ds.getValue(Report.class);
                        if(!(report.getLatitude().equals("")) && report.getReportStatus().equals("نشط")){
                            reportsList.add(report);
                            IdList.add(ID);
                            userList.add(userName);
                            if(allowPhoneAccess.equals("true")){
                                phones.add(No);
                            }else{
                                phones.add("0");
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//---------------------------------------------------
        // cardView inputs
        people_card = findViewById(R.id.people_card);
        animal_card = findViewById(R.id.animal_card);
        device_card = findViewById(R.id.device_card);
        other_card = findViewById(R.id.other_card);

        // add click listener to the cards
        people_card.setOnClickListener(this);
        animal_card.setOnClickListener(this);
        device_card.setOnClickListener(this);
        other_card.setOnClickListener(this);


        fab =  findViewById(R.id.addReport);

        fab.setOnClickListener(view -> startActivity(new Intent(SecondActivity.this, ReportActivity.class)));
        bottomAppBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch(id){
                case R.id.map:{
                    if(isServicesOK()){
                        Intent intent = new Intent(SecondActivity.this, MapActivity.class);
                        intent.putStringArrayListExtra("userList" , userList);
                        intent.putStringArrayListExtra("IDsList" , IdList);
                        intent.putStringArrayListExtra("phoneNumbers" , phones);
                        intent.putParcelableArrayListExtra("reportsLoc", (ArrayList) reportsList);
                        startActivity(intent);
                        //             intent.putStringArrayListExtra("Lat", LatitudeList);
//                            intent.putStringArrayListExtra("Long",LongitudeList);
                    }

                    break;
                }

                case R.id.chats:{
                    Intent intent =new Intent(SecondActivity.this, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(this, "chat clicked", Toast.LENGTH_SHORT).show();

                    break;
                }


            }
            return false;
        });
    }
    //------------------------------------------------------------------------------------------------------
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SecondActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SecondActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.people_card:
//                test.setText(reportsList.get(0).getLatitude());
                startActivity(new Intent(this, HumanReport.class));
                break;
            case R.id.animal_card:
                startActivity(new Intent(this, AnimalReport.class));
                break;
            case R.id.device_card:
                startActivity(new Intent(this, DeviceReport.class));
                break;
            case R.id.other_card:
                startActivity(new Intent(this, OtherReport.class));
                break;
            default: break;

        }

    }


    private void Logout() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(SecondActivity.this);
        builder1.setMessage("سوف يتم يتم تسجيل خروجك، هل انت متأكد؟");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                (dialog, id) -> {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(SecondActivity.this, MainActivity.class));
                });

        builder1.setNegativeButton(
                "إلغاء الامر",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();

        alert11.show();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_profile:
                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_changePassword:
                startActivity(new Intent(SecondActivity.this, UpdatePassword.class));
                break;
            case R.id.nav_my_report:
                startActivity(new Intent(SecondActivity.this, MyReport.class));
                break;
//            case R.id.nav_privacyAndSecurity:
//                startActivity(new Intent(SecondActivity.this, privacyAndSecurity.class));
//                break;
            case R.id.nav_logout:
                Logout();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocationTracking.notificationManager.cancelAll();
        LocationTracking.id = 1;
    }
//private void requestLocationPermission() {
//    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//            FINE_LOCATION)) {
//
//        new AlertDialog.Builder(this)
//                .setTitle("Permission needed")
//                .setMessage("This permission is needed because of this and that")
//                .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(SecondActivity.this,
//                        new String[] {FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION))
//                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                .create().show();
//
//    } else {
//        ActivityCompat.requestPermissions(this,
//                new String[] {FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//    }
//}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LocationTracking.class));
            } else {
                //   Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showPermissionDialog() {
        if (!LocationJobService.checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void SignUpRequest(String request) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(SecondActivity.this);
        builder1.setMessage(request);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                (dialog, id) -> {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(SecondActivity.this, MainActivity.class));
                });

        builder1.setNegativeButton(
                "إلغاء الامر",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();

        alert11.show();

    }

    public void onClickChats(MenuItem item) {
        if (firebaseAuth.getCurrentUser() == null)
            SignUpRequest(getString(R.string.ChatRequest));
        else {
            Intent intent =new Intent(SecondActivity.this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
