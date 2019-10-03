package com.example.qurrah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyReport extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Report> list;
    MyReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout);
//------------------------------------------------
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//------------------------------------------------
        // inputs
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyReport.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        list = new ArrayList<Report>();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Report");
        // Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Report");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(ds.getChildrenCount() > 0) {
                        findViewById(R.id.progressbar).setVisibility(View.GONE);
                    }

                    Report report = ds.getValue(Report.class);
                    list.add(report);

                }
                adapter = new MyReportAdapter(MyReport.this, list);
                recyclerView.setAdapter(adapter);
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                if(list.isEmpty()){
                    findViewById(R.id.noReports).setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Whoops!", Toast.LENGTH_SHORT).show();

            }
        });


    }


}



