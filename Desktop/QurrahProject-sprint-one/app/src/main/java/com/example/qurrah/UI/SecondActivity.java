package com.example.qurrah.UI;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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


import static com.example.qurrah.Constants.ERROR_DIALOG_REQUEST;


public class SecondActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    CardView people_card,animal_card,device_card,other_card;
    BottomAppBar bottomAppBar;
    TextView username;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private static final String TAG = "SecondActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        final DrawerLayout navDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        username = header.findViewById(R.id.Username);
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
        databaseReference = firebaseDatabase.getReference().child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                username.setText(userProfile.getUserName());
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
                        startActivity(intent);
                    }

                    break;
                }

                    case R.id.chats:{
                        Intent intent =new Intent(SecondActivity.this, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
            case R.id.nav_logout:
                Logout();
                break;
            default:
                break;
        }
        return false;
    }

}
