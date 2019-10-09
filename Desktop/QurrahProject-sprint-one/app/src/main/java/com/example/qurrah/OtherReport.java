package com.example.qurrah;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OtherReport extends AppCompatActivity implements SearchView.OnQueryTextListener {

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Report> list;
    ArrayList<String> userList, phones;
    ReportCategoriesAdapter adapter;
    TextView noReports;
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
        noReports = findViewById(R.id.noReports);
        noReports.setText("لا يوجد بلاغات منشورة");
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(OtherReport.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        userList = new ArrayList<>();
        phones= new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                userList.clear();
                phones.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    String userName = userProfile.getUserName();
                    String No = userProfile.getPhone();
                    for (DataSnapshot ds: snapshot.child("Report").getChildren()) {
                        if(ds.getChildrenCount() > 0) {
                            findViewById(R.id.progressbar).setVisibility(View.GONE);
                        }

                        Report report = ds.getValue(Report.class);
                        if (report.getCategoryOption().equals("اخرى") && report.getReportStatus().equals("نشط")){
                            list.add(report);
                            userList.add(userName);
                            phones.add(No);
                        }
                    }
                }
                adapter = new ReportCategoriesAdapter(OtherReport.this, list , userList, phones);
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
    //----------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<Report> newList = new ArrayList<>();

        for(Report rep: list){
            if(rep.getLostTitle().toLowerCase().contains(userInput)){
                newList.add(rep);

            }
        }
        adapter.updateList(newList,userInput);
        return false;
    }
//----------------------------------------------------------
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
//----------------------------------------------------------


}


