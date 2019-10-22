package com.example.qurrah.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.qurrah.UI.ReportTypes.AnimalReport;
import com.example.qurrah.UI.ReportTypes.DeviceReport;
import com.example.qurrah.UI.ReportTypes.HumanReport;
import com.example.qurrah.UI.ReportTypes.OtherReport;
import com.example.qurrah.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class UnregisteredUserSecondActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    CardView people_card,animal_card,device_card,other_card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unregistered_user_second_page);
        firebaseAuth = FirebaseAuth.getInstance();

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
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        //setSupportActionBar(bottomAppBar);
        bottomAppBar.replaceMenu(R.menu.map_menu);

        fab =  findViewById(R.id.addReport);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpRequest();
            }
        });
    }


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
