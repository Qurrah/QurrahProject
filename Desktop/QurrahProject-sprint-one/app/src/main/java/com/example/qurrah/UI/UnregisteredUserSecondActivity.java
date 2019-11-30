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
import de.hdodenhof.circleimageview.CircleImageView;

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
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Objects;

import static com.example.qurrah.Constants.ERROR_DIALOG_REQUEST;
import static com.example.qurrah.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class UnregisteredUserSecondActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    protected FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    CardView people_card, animal_card, device_card, other_card;
    protected BottomAppBar bottomAppBar;
    protected TextView username, test;
    private CircleImageView profilePic;
    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference databaseReference;
    private static final String TAG = "HomeActivity";
    //    ArrayList<String> LatitudeList;
    //    ArrayList<String> LongitudeList;
    protected ArrayList<Report> reportsList;  // array of reports that contain a location
    protected ArrayList<String> userList, phones, IdList, allowWhats;
    Intent intent;
    protected Menu menuBottomAppBar;
    DrawerLayout navDrawer;
    public static String userId;
    NavigationView mNavigationView;
    String type = "none", CU = "none";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_unregestered_usernav);
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
        profilePic = (CircleImageView) header.findViewById(R.id.imageView);


        reportsList = new ArrayList<>();
        userList = new ArrayList<>();
        phones = new ArrayList<>();
        IdList = new ArrayList<>();
        allowWhats = new ArrayList<>();
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
        bottomAppBar.setNavigationOnClickListener(v -> {
            //   Toast.makeText(getApplicationContext(),"nav clicked",Toast.LENGTH_SHORT).show();
            // If navigation drawer is not open yet, open it else close it.
            if (!navDrawer.isDrawerOpen(GravityCompat.START))
                navDrawer.openDrawer(GravityCompat.START);

            else
                navDrawer.closeDrawer(GravityCompat.END);
        });
//---------------------------------------------------

        try {
            CU = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        } catch (Exception e) {
            type = "guest";
        }

        if (!(type.equals("guest"))) {
            bottomAppBar.setNavigationOnClickListener(v -> {
                //   Toast.makeText(getApplicationContext(),"nav clicked",Toast.LENGTH_SHORT).show();
                // If navigation drawer is not open yet, open it else close it.
                if (!navDrawer.isDrawerOpen(GravityCompat.START))
                    navDrawer.openDrawer(GravityCompat.START);

                else
                    navDrawer.closeDrawer(GravityCompat.END);
            });

        }
//---------------------------------------------------

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
//        try {
//            userId = firebaseAuth.getCurrentUser().getUid();
//        }catch (Exception e){
//
//        }
        databaseReference = firebaseDatabase.getReference().child("Users"); //.child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
//                    UserProfile userProfile = dataSnapshot.child(userId).getValue(UserProfile.class);
//                    username.setText(userProfile.getUserName());
//                    if(!userProfile.getImageURL().equals("default"))
//                        Picasso.get().load(userProfile.getImageURL()).into(profilePic);
                    reportsList.clear();
                    userList.clear();
                    phones.clear();
                    IdList.clear();
                    allowWhats.clear();


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
                                phones.add(No);
                                allowWhats.add(allowPhoneAccess);


//                                    if (allowPhoneAccess.equals("true")) {
//                                        phones.add(No);
//                                    } else {
//                                        phones.add("0");
//                                    }
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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UnregisteredUserSecondActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UnregisteredUserSecondActivity.this, available, ERROR_DIALOG_REQUEST);
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
                startActivity(new Intent(UnregisteredUserSecondActivity.this, HumanReport.class));
                break;
            case R.id.animal_card:
                startActivity(new Intent(UnregisteredUserSecondActivity.this, AnimalReport.class));
                break;
            case R.id.device_card:
                startActivity(new Intent(UnregisteredUserSecondActivity.this, DeviceReport.class));
                break;
            case R.id.other_card:
                startActivity(new Intent(UnregisteredUserSecondActivity.this, OtherReport.class));
                break;
            default:
                break;

        }

    }


//    protected void logout() {
//
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
//        builder1.setMessage("سوف يتم يتم تسجيل خروجك، هل انت متأكد؟");
//        builder1.setCancelable(true);
//
//        builder1.setPositiveButton(
//                "نعم",
//                (dialog, id) -> {
//                    firebaseAuth.signOut();
//                    finish();
//                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
//                });
//
//        builder1.setNegativeButton(
//                "إلغاء الامر",
//                (dialog, id) -> dialog.cancel());
//
//        AlertDialog alert11 = builder1.create();
//
//        alert11.show();
//
//    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        int id = menuItem.getItemId();
//        switch (id) {
//            case R.id.nav_profile:
//                navDrawer.closeDrawers();
//                startActivity(new Intent(this, ProfileActivity.class));
//                break;
//            case R.id.nav_my_report:
//                navDrawer.closeDrawers();
//                startActivity(new Intent(this, MyReport.class).putExtra("from", "HomeIcon"));
//                break;
//            case R.id.nav_help:
//                navDrawer.closeDrawers();
//                startActivity(new Intent(this, helpActivity.class));
//                break;
//
//            case R.id.nav_logout:
//                logout();
//                break;
//            default:
//                break;
//        }
//        return false;
//    }

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

        AlertDialog.Builder builder1 = new AlertDialog.Builder(UnregisteredUserSecondActivity.this);
        builder1.setMessage(request);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                (dialog, id) -> {
                    firebaseAuth.signOut();
//                    finish();
                    startActivity(new Intent(UnregisteredUserSecondActivity.this, MainActivity.class));
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
            Intent intent = new Intent(UnregisteredUserSecondActivity.this, BottomAppBarHandlerActivity.class);
            intent.putExtra("fromHomeActivity", "GoingToChatFragment");
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }


    public void updateItemColor(int id) {
        ImageButton button;
        if (id == R.id.addReport) {
            findViewById(id).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            button = findViewById(R.id.Home);
            button.setColorFilter(getColor(R.color.white));
            button = findViewById(R.id.Chat);
            button.setColorFilter(getColor(R.color.white));
            button = findViewById(R.id.Map);
            button.setColorFilter(getColor(R.color.white));

        } else {
            findViewById(R.id.addReport).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightGrey)));
            switch (id) {
                case R.id.Chat: {
                    button = findViewById(R.id.Chat);
                    button.setColorFilter(getColor(R.color.colorPrimary));
                    button = findViewById(R.id.Home);
                    button.setColorFilter(getColor(R.color.white));
                    button = findViewById(R.id.Map);
                    button.setColorFilter(getColor(R.color.white));
                    break;
                }
                case R.id.Map: {
                    button = findViewById(R.id.Map);
                    button.setColorFilter(getColor(R.color.colorPrimary));
                    button = findViewById(R.id.Chat);
                    button.setColorFilter(getColor(R.color.white));
                    button = findViewById(R.id.Home);
                    button.setColorFilter(getColor(R.color.white));
                    break;
                }
                case R.id.Home: {
                    button = findViewById(R.id.Home);
                    button.setColorFilter(getColor(R.color.colorPrimary));
                    button = findViewById(R.id.Chat);
                    button.setColorFilter(getColor(R.color.white));
                    button = findViewById(R.id.Map);
                    button.setColorFilter(getColor(R.color.white));
                    break;
                }
            }
        }


    }

    public void goToChatActivity(View view) {
        SignUpRequest(getString(R.string.ChatRequest));
    }

    public void goToMapActivity(View view) {
        updateDataOnMapClick();

    }

    public void goToHomeActivity(View view) {
        updateDataOnHomeClick();

    }

    public void goToReportActivity(View view) {
        SignUpRequest(getString(R.string.AddReportRequest));
    }


    protected void updateDataOnHomeClick() {
        updateItemColor(R.id.Home);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    protected void updateDataOnMapClick() {

        if (isServicesOK()) {
            updateItemColor(R.id.Map);
            Intent intent = new Intent(UnregisteredUserSecondActivity.this, MapActivity.class);
            intent.putStringArrayListExtra("userList", userList);
            intent.putStringArrayListExtra("IDsList", IdList);
            intent.putStringArrayListExtra("phoneNumbers", phones);
            intent.putStringArrayListExtra("allowWhats", allowWhats);
            intent.putParcelableArrayListExtra("reportsLoc", (ArrayList) reportsList);
            finish();
            startActivity(intent);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {

            case R.id.nav_help:
                navDrawer.closeDrawers();
                startActivity(new Intent(this, helpActivity.class));
                break;

            case R.id.nav_login:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
        return false;
    }
}

































//
//
//package com.example.qurrah.UI;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
////import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.cardview.widget.CardView;
//import androidx.core.app.ActivityCompat;
//
//import com.example.qurrah.LocationTrackingServices.LocationJobService;
//import com.example.qurrah.LocationTrackingServices.LocationTracking;
//import com.example.qurrah.Model.Report;
//import com.example.qurrah.Model.UserProfile;
//import com.example.qurrah.UI.ReportTypes.AnimalReport;
//import com.example.qurrah.UI.ReportTypes.DeviceReport;
//import com.example.qurrah.UI.ReportTypes.HumanReport;
//import com.example.qurrah.UI.ReportTypes.OtherReport;
//import com.example.qurrah.R;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.material.bottomappbar.BottomAppBar;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//import static com.example.qurrah.Constants.ERROR_DIALOG_REQUEST;
//import static com.example.qurrah.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
//
//public class UnregisteredUserSecondActivity extends HomeActivity {
//
//    private FirebaseAuth firebaseAuth;
//    private FloatingActionButton fab;
//    CardView people_card, animal_card, device_card, other_card;
//    ArrayList<Report> reportsList;  // array of reports that contain a location
//    ArrayList<String> userList, phones, IdList;
//    DatabaseReference databaseReference;
//    private FirebaseDatabase firebaseDatabase;
//    private static final String TAG = "UnregisteredSecondActivity";
//    BottomAppBar bottomAppBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.unregistered_user_second_page);
//        firebaseAuth = FirebaseAuth.getInstance();
//        updateItemColor(R.id.Home);
//
//        reportsList = new ArrayList<>();
//        userList = new ArrayList<>();
//        phones = new ArrayList<>();
//        IdList = new ArrayList<>();
//        showPermissionDialog();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference().child("Users");   //.child(userId);
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                reportsList.clear();
//                userList.clear();
//                phones.clear();
//                IdList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserProfile userInfo = snapshot.getValue(UserProfile.class);
//                    String userName = userInfo.getUserName();
//                    String ID = userInfo.getId();
//                    String phoneIsAllowed = userInfo.getAllowPhone();
//                    String No = "0";
//
//                    if (phoneIsAllowed.equals("true")) {
//                        No = userInfo.getPhone();
//                    }
//
//
//                    for (DataSnapshot ds : snapshot.child("Report").getChildren()) {
////                        if(ds.getChildrenCount() > 0) {
//                        Report report = ds.getValue(Report.class);
//                        if (!(report.getLatitude().equals("")) && report.getReportStatus().equals("نشط")) {
//                            reportsList.add(report);
//                            userList.add(userName);
//                            userList.add(userName);
//                            if (phoneIsAllowed.equals("true")) {
//                                phones.add(No);
//                            } else {
//                                phones.add("0");
//                            }
//                            IdList.add(ID);
//                        }
////                        }
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//
//        });
//
//
//        // cardView inputs
//        people_card = findViewById(R.id.people_card);
//        animal_card = findViewById(R.id.animal_card);
//        device_card = findViewById(R.id.device_card);
//        other_card = findViewById(R.id.other_card);
//
//        // add click listener to the cards
//        people_card.setOnClickListener(this);
//        animal_card.setOnClickListener(this);
//        device_card.setOnClickListener(this);
//        other_card.setOnClickListener(this);
//
//        // bottom app bar input
//        bottomAppBar = findViewById(R.id.bottomAppBar);
//        //setSupportActionBar(bottomAppBar);
//        //     bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);
//
//        fab = findViewById(R.id.addReport);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SignUpRequest(getString(R.string.AddReportRequest));
//            }
//        });
//
//
//        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//                switch (id) {
//                    case R.id.Map: {
//                        if (isServicesOK()) {
//                            Intent intent = new Intent(UnregisteredUserSecondActivity.this, MapActivity.class);
//                            intent.putStringArrayListExtra("userList", userList);
//                            intent.putStringArrayListExtra("IDsList", IdList);
//                            intent.putStringArrayListExtra("phoneNumbers", phones);
//                            intent.putParcelableArrayListExtra("reportsLoc", (ArrayList) reportsList);
//                            startActivity(intent);
//                            //             intent.putStringArrayListExtra("Lat", LatitudeList);
////                            intent.putStringArrayListExtra("Long",LongitudeList);
//                        }
//
//                        break;
//                    }
//
//
//                }
//                return false;
//            }
//        });
//    }
//
//    //------------------------------------------------------------------------------------------------------
//    @SuppressLint("LongLogTag")
//    public boolean isServicesOK() {
//        Log.d(TAG, "isServicesOK: checking google services version");
//
//        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UnregisteredUserSecondActivity.this);
//
//        if (available == ConnectionResult.SUCCESS) {
//            //everything is fine and the user can make map requests
//            Log.d(TAG, "isServicesOK: Google Play Services is working");
//            return true;
//        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
//            //an error occurred but we can resolve it
//            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
//            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UnregisteredUserSecondActivity.this, available, ERROR_DIALOG_REQUEST);
//            dialog.show();
//        } else {
//            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }
//
//    //------------------------------------------------------------------------------------------------------
//
//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.people_card:
//                startActivity(new Intent(this, HumanReport.class));
//                break;
//            case R.id.animal_card:
//                startActivity(new Intent(this, AnimalReport.class));
//                break;
//            case R.id.device_card:
//                startActivity(new Intent(this, DeviceReport.class));
//                break;
//            case R.id.other_card:
//                startActivity(new Intent(this, OtherReport.class));
//                break;
//            default:
//                break;
//
//        }
//
//    }
//
//
//    protected void SignUpRequest(String request) {
//
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(UnregisteredUserSecondActivity.this);
//        builder1.setMessage(request);
//        builder1.setCancelable(true);
//
//        builder1.setPositiveButton(
//                "نعم",
//                (dialog, id) -> {
//                    firebaseAuth.signOut();
//                    finish();
//                    startActivity(new Intent(UnregisteredUserSecondActivity.this, MainActivity.class));
//                });
//
//        builder1.setNegativeButton(
//                "إلغاء الامر",
//                (dialog, id) -> dialog.cancel());
//
//        AlertDialog alert11 = builder1.create();
//
//        alert11.show();
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.signup_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.signUp_menu: {
//                firebaseAuth.signOut();
//                finish();
//                startActivity(new Intent(UnregisteredUserSecondActivity.this, MainActivity.class));
//                break;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //  Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, LocationTracking.class).putExtra("FROM_ACTIVITY", "UnregisteredUserSecondActivity"));
//            } else {
//                //   Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void showPermissionDialog() {
//        if (!LocationJobService.checkPermission(this)) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    @Override
//    public void goToChatActivity(View view) {
//        SignUpRequest(getString(R.string.ChatRequest));
//    }
//
//    @Override
//    protected void updateDataOnHomeClick() {
//        super.updateDataOnHomeClick();
//        updateItemColor(R.id.Home);
//        startActivity(getIntent());
//        finish();
//        overridePendingTransition(0, 0);
//    }
//}
