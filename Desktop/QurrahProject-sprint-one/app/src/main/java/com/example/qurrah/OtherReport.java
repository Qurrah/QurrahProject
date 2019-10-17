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
    ArrayList<String> userList, phones , id;
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
        allbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
        allbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                missingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
                findingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
                SecondFilter("all");
            }
        });
        missingbtn=(Button) findViewById(R.id.missing);
        missingbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                missingbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                allbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
                findingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
                SecondFilter("missing");

            }
        });
        findingbtn=(Button) findViewById(R.id.finding);
        findingbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findingbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                missingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
                allbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
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

        allbtn.setVisibility(View.GONE);
        findingbtn.setVisibility(View.GONE);
        missingbtn.setVisibility(View.GONE);

        list = new ArrayList<>();
        userList = new ArrayList<>();
        phones= new ArrayList<>();
        id= new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                userList.clear();
                phones.clear();
                id.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    String userName = userProfile.getUserName();
                    String No = userProfile.getPhone();
                    String Id = userProfile.getId();

                    for (DataSnapshot ds: snapshot.child("Report").getChildren()) {
                        if(ds.getChildrenCount() > 0) {
                            findViewById(R.id.progressbar).setVisibility(View.GONE);

                            allbtn.setVisibility(View.VISIBLE);
                            findingbtn.setVisibility(View.VISIBLE);
                            missingbtn.setVisibility(View.VISIBLE);
                        }

                        Report report = ds.getValue(Report.class);
                        if (report.getCategoryOption().equals("اخرى") && report.getReportStatus().equals("نشط")){
                            list.add(report);
                            userList.add(userName);
                            phones.add(No);
                            id.add(Id);

                        }
                    }
                }
                adapter = new ReportCategoriesAdapter(OtherReport.this, list , userList, phones,id);
                recyclerView.setAdapter(adapter);
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                allbtn.setVisibility(View.VISIBLE);
                findingbtn.setVisibility(View.VISIBLE);
                missingbtn.setVisibility(View.VISIBLE);
                if(list.isEmpty()){
                    findViewById(R.id.noReports).setVisibility(View.VISIBLE);

                    allbtn.setVisibility(View.GONE);
                    findingbtn.setVisibility(View.GONE);
                    missingbtn.setVisibility(View.GONE);
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
    if(newList.isEmpty()){
        findViewById(R.id.noReports).setVisibility(View.VISIBLE);

    }else{
        findViewById(R.id.noReports).setVisibility(View.GONE);
    }
    adapter.updateList(newList);
    recyclerView.scrollToPosition(adapter.getItemCount()-1);
}
//----------------------------------------------------------


}



