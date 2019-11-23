package com.example.qurrah.UI.ReportTypes;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.qurrah.Adapters.ReportCategoriesAdapter;
import com.example.qurrah.R;
import com.example.qurrah.Model.Report;
import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.ReportTypesWithTabs.All_Reports;
import com.example.qurrah.ReportTypesWithTabs.Found_reports;
import com.example.qurrah.ReportTypesWithTabs.Missing_reports;
import com.example.qurrah.ReportTypesWithTabs.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class DeviceReport extends AppCompatActivity implements SearchView.OnQueryTextListener {

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Report> list;
    ArrayList<String> userList, phones , id;
    ReportCategoriesAdapter adapter;
    TextView noReports,noMatchReports;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout);

//---------------------Tabs---------------------------

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(2).select();

        //----------------------------------------------------------------
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("بلاغات الأجهزة");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        //----------------------------------------------------------------
        // inputs
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        noReports = findViewById(R.id.noReports);
        noReports.setText("لا يوجد بلاغات منشورة");
        noMatchReports = findViewById(R.id.noMatchReports);
        noMatchReports.setText("لا يوجد نتائج ");




        // tabs click

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        SecondFilter("finding");
                        break;
                    case 1:
                        SecondFilter("missing");
                        break;
                    case 2:
                        SecondFilter("all");

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DeviceReport.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);


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
                    String allowPhoneAccess=userProfile.getAllowPhone();

                    for (DataSnapshot ds: snapshot.child("Report").getChildren()) {
                        if(ds.getChildrenCount() > 0) {
                            findViewById(R.id.progressbar).setVisibility(View.GONE);

                        }

                        Report report = ds.getValue(Report.class);
                        if (report.getCategoryOption().equals(getString(R.string.devices)) && report.getReportStatus().equals("نشط")) {
                            list.add(report);
                            sortByDate(list);
                            userList.add(userName);
//                            userList.add(userName);
                            if(allowPhoneAccess.equals("true")){
                                phones.add(No);
                            }else{
                                phones.add("0");
                            }
                            id.add(Id);

                        }
                    }
                }
                adapter = new ReportCategoriesAdapter(DeviceReport.this, list, userList, phones,id);
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
        SearchView searchView = findViewById(R.id.action_search);
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);

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
        findViewById(R.id.noMatchReports).setVisibility(View.GONE);
        for(Report rep: list){
            if(rep.getLostTitle().toLowerCase().contains(userInput))
                newList.add(rep);}

        if(newList.isEmpty() && !(list.isEmpty()))
            findViewById(R.id.noMatchReports).setVisibility(View.VISIBLE);

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

// ------------------Tabs------------------------
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Found_reports(), "المعثورات");
        adapter.addFragment(new Missing_reports() , "المفقودات");
        adapter.addFragment(new All_Reports(), "الكل");
        viewPager.setAdapter(adapter);
    }
    public void sortByDate(ArrayList<Report> list){
        Collections.sort(list, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
    }

}



