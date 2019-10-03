package com.example.qurrah;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private Button logout;
    private FloatingActionButton fab;
    CardView people_card,animal_card,device_card,other_card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
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


        fab = (FloatingActionButton) findViewById(R.id.addReport);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, ReportActivity.class));
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


    private void Logout() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(SecondActivity.this);
        builder1.setMessage("سوف يتم يتم تسجيل خروجك، هل انت متأكد؟");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "نعم",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(SecondActivity.this, MainActivity.class));
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
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logoutMenu: {
                Logout();
                break;
            }
            case R.id.profileMenu: {
                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                break;
            }
            case R.id.UpdatePasswordMenu: {
                startActivity(new Intent(SecondActivity.this, UpdatePassword.class));
                break;
            }
            case R.id.ReportViewMenu: {
                startActivity(new Intent(SecondActivity.this, MyReport.class));
                break;
            }}
        return super.onOptionsItemSelected(item);
    }
}
