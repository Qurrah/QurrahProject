package com.example.qurrah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    Button allbtn,missingbtn, findingbtn;

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
// second filter
        allbtn=(Button) findViewById(R.id.all);
        allbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SecondFilter("all");
            }
        });
        missingbtn=(Button) findViewById(R.id.missing);
        missingbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SecondFilter("missing");
            }
        });
        findingbtn=(Button) findViewById(R.id.finding);
        findingbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SecondFilter("finding");
            }
        });
        //

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
    //-------------------------Second Filter Method---------------------------------
    public void SecondFilter(String flag){
        ArrayList<Report> newList = new ArrayList<>();

        switch(flag){
            case "all":
                for(Report rep: list)
                    newList.add(rep);
                break;
            case"missing":
                for(Report rep: list){
                    if(rep.getReportTypeOption().equals("فاقد")){
                        newList.add(rep);

                    }
                }
                break;
            case"finding":
                for(Report rep: list){
                    if(rep.getReportTypeOption().equals("معثور عليه")){
                        newList.add(rep);

                    }
                }
                break;
        }
        adapter.updateList(newList);

    }
//


}



