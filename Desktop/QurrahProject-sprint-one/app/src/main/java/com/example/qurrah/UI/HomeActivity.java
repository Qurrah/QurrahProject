package com.example.qurrah.UI;

import android.Manifest;
import android.app.Dialog;

import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;

import android.util.Log;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import static com.example.qurrah.Constants.ERROR_DIALOG_REQUEST;
import static com.example.qurrah.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    protected FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    CardView people_card, animal_card, device_card, other_card;
    protected BottomAppBar bottomAppBar;
    protected TextView username, test;
    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference databaseReference;
    private static final String TAG = "HomeActivity";
    //    ArrayList<String> LatitudeList;
    //    ArrayList<String> LongitudeList;
    protected ArrayList<Report> reportsList;  // array of reports that contain a location
    protected ArrayList<String> userList, phones, IdList;
    Intent intent;
    protected Menu menuBottomAppBar;
     DrawerLayout navDrawer;
    public static String userId;
    NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homenav);
        findViewById(R.id.Home).setEnabled(false);
        findViewById(R.id.Home).setClickable(false);
        fab = findViewById(R.id.addReport);
        // requestLocationPermission();
        showPermissionDialog();
        intent = getIntent();
        String activity = intent.getStringExtra("from");
        if (activity != null && activity.equalsIgnoreCase("FirstPage")) {
            Animation hold = AnimationUtils.loadAnimation(this, R.anim.translate_scale);
            findViewById(R.id.CategoryGroup).startAnimation(hold);
        }

        navDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        username = header.findViewById(R.id.Username);

        reportsList = new ArrayList<>();
        userList = new ArrayList<>();
        phones = new ArrayList<>();
        IdList = new ArrayList<>();
//---------------------------------------------------

         mNavigationView = findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
//---------------------------------------------------
        bottomAppBar = findViewById(R.id.bottomAppBar);
        //setSupportActionBar(bottomAppBar);
        //  bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);
        menuBottomAppBar = bottomAppBar.getMenu();
        updateItemColor(R.id.Home);

//---------------------------------------------------
        bottomAppBar.setNavigationOnClickListener(v -> {
            //   Toast.makeText(getApplicationContext(),"nav clicked",Toast.LENGTH_SHORT).show();
            // If navigation drawer is not open yet, open it else close it.
            if (!navDrawer.isDrawerOpen(GravityCompat.START))
                navDrawer.openDrawer(GravityCompat.START);

            else
                navDrawer.closeDrawer(GravityCompat.END);

        });
//---------------------------------------------------

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        try {
             userId = firebaseAuth.getCurrentUser().getUid();
        }catch (Exception e){

        }
        databaseReference = firebaseDatabase.getReference().child("Users"); //.child(userId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        UserProfile userProfile = dataSnapshot.child(userId).getValue(UserProfile.class);
                        username.setText(userProfile.getUserName());

                        reportsList.clear();
                        userList.clear();
                        phones.clear();
                        IdList.clear();


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserProfile userInfo = snapshot.getValue(UserProfile.class);
                            String ID = userInfo.getId();
                            String userName = userInfo.getUserName();
                            String No = userInfo.getPhone();
                            String allowPhoneAccess = userInfo.getAllowPhone();


                            for (DataSnapshot ds : snapshot.child("Report").getChildren()) {
                                Report report = ds.getValue(Report.class);
                                if (!(report.getLatitude().equals("")) && report.getReportStatus().equals("نشط")) {
                                    reportsList.add(report);
                                    IdList.add(ID);
                                    userList.add(userName);
                                    if (allowPhoneAccess.equals("true")) {
                                        phones.add(No);
                                    } else {
                                        phones.add("0");
                                    }
                                }
                            }
                        }


                    } catch (NullPointerException e) {
                        System.out.println("Unregistered User, Cannot complete operation");
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

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.people_card:
                startActivity(new Intent(HomeActivity.this, HumanReport.class));
                break;
            case R.id.animal_card:
                startActivity(new Intent(HomeActivity.this, AnimalReport.class));
                break;
            case R.id.device_card:
                startActivity(new Intent(HomeActivity.this, DeviceReport.class));
                break;
            case R.id.other_card:
                startActivity(new Intent(HomeActivity.this, OtherReport.class));
                break;
            default:
                break;

        }

    }


    protected void logout() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
        builder1.setMessage("سوف يتم يتم تسجيل خروجك، هل انت متأكد؟");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                (dialog, id) -> {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
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
                navDrawer.closeDrawers();
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_changePassword:
                navDrawer.closeDrawers();
                startActivity(new Intent(this, UpdatePassword.class));
                break;
            case R.id.nav_my_report:
                navDrawer.closeDrawers();
                startActivity(new Intent(this, MyReport.class).putExtra("from", "HomeIcon"));
                break;
//            case R.id.nav_privacyAndSecurity:
//                startActivity(new Intent(HomeActivity.this, privacyAndSecurity.class));
//                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationTracking.id = 1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
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

    protected void SignUpRequest(String request) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
        builder1.setMessage(request);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                (dialog, id) -> {
                    firebaseAuth.signOut();
//                    finish();
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
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
            Intent intent = new Intent(HomeActivity.this, BottomAppBarHandlerActivity.class);
            intent.putExtra("fromHomeActivity", "GoingToChatFragment");
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    public void updateItemColor(int id) {
        ImageButton button;
        if (id == R.id.addReport){
             findViewById(id).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
             button =findViewById(R.id.Home);
             button.setColorFilter(getColor(R.color.white));
             button =findViewById(R.id.Chat);
             button.setColorFilter(getColor(R.color.white));
             button =findViewById(R.id.Map);
             button.setColorFilter(getColor(R.color.white));

        }else {
            findViewById(R.id.addReport).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightGrey)));
            switch (id){
                case R.id.Chat:{
                    button =findViewById(R.id.Chat);
                    button.setColorFilter(getColor(R.color.colorPrimary));
                    button =findViewById(R.id.Home);
                    button.setColorFilter(getColor(R.color.white));
                    button =findViewById(R.id.Map);
                    button.setColorFilter(getColor(R.color.white));
                    break;
                }
                case R.id.Map:{
                    button =findViewById(R.id.Map);
                    button.setColorFilter(getColor(R.color.colorPrimary));
                    button =findViewById(R.id.Chat);
                    button.setColorFilter(getColor(R.color.white));
                    button =findViewById(R.id.Home);
                    button.setColorFilter(getColor(R.color.white));
                    break;
                }
                case R.id.Home:{
                    button =findViewById(R.id.Home);
                    button.setColorFilter(getColor(R.color.colorPrimary));
                    button =findViewById(R.id.Chat);
                    button.setColorFilter(getColor(R.color.white));
                    button =findViewById(R.id.Map);
                    button.setColorFilter(getColor(R.color.white));
                    break;
                }
            }
        }


    }

    public void goToChatActivity(View view) {

             updateDataOnChatClick();
    }

    public void goToMapActivity(View view) {
         updateDataOnMapClick();

    }

    public void goToHomeActivity(View view) {
            updateDataOnHomeClick();

    }

    public void goToReportActivity(View view) {
        updateDataOnAddReportClick();
    }



   protected void updateDataOnChatClick(){

       updateItemColor(R.id.Chat);
       Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
       intent.putExtra("fromHomeActivity", "GoingToChatFragment");
       startActivity(intent);
       finish();
       overridePendingTransition(0, 0);

    }
    protected void updateDataOnAddReportClick(){
        updateItemColor(R.id.addReport);
        startActivity(new Intent(getApplicationContext(), ReportActivity.class));
        finish();
        overridePendingTransition(0, 0);
    }
    protected void updateDataOnHomeClick(){
        updateItemColor(R.id.Home);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
    protected void updateDataOnMapClick(){

        if (isServicesOK()) {
            updateItemColor(R.id.Map);
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            intent.putStringArrayListExtra("userList", userList);
            intent.putStringArrayListExtra("IDsList", IdList);
            intent.putStringArrayListExtra("phoneNumbers", phones);
            intent.putParcelableArrayListExtra("reportsLoc", (ArrayList) reportsList);
            finish();
            startActivity(intent);
        }

    }

}