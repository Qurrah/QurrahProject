package com.example.qurrah.UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.qurrah.Constants.ERROR_DIALOG_REQUEST;

public class UnregisteredUserSecondActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    CardView people_card,animal_card,device_card,other_card;
    ArrayList<Report> reportsList;  // array of reports that contain a location
    ArrayList<String> userList, phones;
    DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private static final String TAG = "UnregisteredSecondActivity";
    BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unregistered_user_second_page);
        firebaseAuth = FirebaseAuth.getInstance();

        reportsList = new ArrayList<>();
        userList = new ArrayList<>();
        phones = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");   //.child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                reportsList.clear();
                userList.clear();
                phones.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile userInfo = snapshot.getValue(UserProfile.class);
                    String userName = userInfo.getUserName();
                    String No = userInfo.getPhone();


                    for (DataSnapshot ds : snapshot.child("Report").getChildren()) {
//                        if(ds.getChildrenCount() > 0) {
                        Report report = ds.getValue(Report.class);
                        if (!(report.getLatitude().equals("")) && report.getReportStatus().equals("نشط")) {
                            reportsList.add(report);
                            userList.add(userName);
                            phones.add(No);
                        }
//                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });



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

        // bottom app bar input
        bottomAppBar = findViewById(R.id.bottomAppBar);
        //setSupportActionBar(bottomAppBar);
        bottomAppBar.replaceMenu(R.menu.map_menu);

        fab =  findViewById(R.id.addReport);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpRequest();
            }
        });



        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.map:{
                        if(isServicesOK()){
                            Intent intent = new Intent(UnregisteredUserSecondActivity.this, MapActivity.class);
                            intent.putStringArrayListExtra("userList" , userList);
                            intent.putStringArrayListExtra("phoneNumbers" , phones);
                            intent.putParcelableArrayListExtra("reportsLoc", (ArrayList) reportsList);
                            startActivity(intent);
                            //             intent.putStringArrayListExtra("Lat", LatitudeList);
//                            intent.putStringArrayListExtra("Long",LongitudeList);
                        }

                        break;
                    }



                }
                return false;
            }
        });
    }
    //------------------------------------------------------------------------------------------------------
    @SuppressLint("LongLogTag")
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UnregisteredUserSecondActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UnregisteredUserSecondActivity.this, available, ERROR_DIALOG_REQUEST);
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


    private void SignUpRequest() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(UnregisteredUserSecondActivity.this);
        builder1.setMessage("يلزمك التسجيل لإضافة بلاغ، هل تود التسجيل الآن؟");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(UnregisteredUserSecondActivity.this, MainActivity.class));
                    }

                });

        builder1.setNegativeButton(
                "إلغاء الامر",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();

        alert11.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.signUp_menu: {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(UnregisteredUserSecondActivity.this, MainActivity.class));
                break;
            }}
        return super.onOptionsItemSelected(item);
    }
}
