package com.example.qurrah.UI.ReportTypes;

import android.content.Intent;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.example.qurrah.UI.HomeActivity;
import com.example.qurrah.UI.MyReport;
import com.example.qurrah.UI.ProfileActivity;
import com.example.qurrah.UI.UpdatePassword;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class OtherReport extends HomeActivity implements SearchView.OnQueryTextListener {

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Report> list;
    ArrayList<String> userList, phones , id;
    ReportCategoriesAdapter adapter;
    TextView noReports , noMatchReports;
//    Button allbtn,missingbtn, findingbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layoutnav);
        updateItemColor(R.id.Home);
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
        textviewTitle.setText("بلاغات أخرى");
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















        // second filter
//        allbtn=(Button) findViewById(R.id.all);
//        allbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
//        allbtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                allbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
//                missingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//                findingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//                SecondFilter("all");
//            }
//        });
//        missingbtn=(Button) findViewById(R.id.missing);
//        missingbtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                missingbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
//                allbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//                findingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//                SecondFilter("missing");
//
//            }
//        });
//        findingbtn=(Button) findViewById(R.id.finding);
//        findingbtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                findingbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
//                missingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//                allbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//                SecondFilter("finding");
//
//            }
//        });
        //

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(OtherReport.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

//        allbtn.setVisibility(View.GONE);
//        findingbtn.setVisibility(View.GONE);
//        missingbtn.setVisibility(View.GONE);

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

//                            allbtn.setVisibility(View.VISIBLE);
//                            findingbtn.setVisibility(View.VISIBLE);
//                            missingbtn.setVisibility(View.VISIBLE);
                        }

                        Report report = ds.getValue(Report.class);
                        if (report.getCategoryOption().equals("اخرى") && report.getReportStatus().equals("نشط")){
                            list.add(report);
                            sortByDate(list);
                            userList.add(userName);
                            userList.add(userName);
                            if(allowPhoneAccess.equals("true")){
                                phones.add(No);
                            }else{
                                phones.add("0");
                            }
                            id.add(Id);

                        }
                    }
                }
                adapter = new ReportCategoriesAdapter(OtherReport.this, list , userList, phones,id);
                recyclerView.setAdapter(adapter);
                findViewById(R.id.progressbar).setVisibility(View.GONE);
//                allbtn.setVisibility(View.VISIBLE);
//                findingbtn.setVisibility(View.VISIBLE);
//                missingbtn.setVisibility(View.VISIBLE);
                if(list.isEmpty()){
                    findViewById(R.id.noReports).setVisibility(View.VISIBLE);

//                    allbtn.setVisibility(View.GONE);
//                    findingbtn.setVisibility(View.GONE);
//                    missingbtn.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Whoops!", Toast.LENGTH_SHORT).show();

            }
        });

        final DrawerLayout navDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view6);
        View header = navigationView.getHeaderView(0);
        username = header.findViewById(R.id.Username);


//---------------------------------------------------

        NavigationView mNavigationView = findViewById(R.id.nav_view6);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
//---------------------------------------------------
        bottomAppBar = findViewById(R.id.bottomAppBar);

        menuBottomAppBar = bottomAppBar.getMenu();
//---------------------------------------------------
        bottomAppBar.setNavigationOnClickListener(v -> {

            if (!navDrawer.isDrawerOpen(GravityCompat.START))
                navDrawer.openDrawer(GravityCompat.START);

            else
                navDrawer.closeDrawer(GravityCompat.END);

        });
//---------------------------------------------------

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference().child("Users"); //.child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.child(userId).getValue(UserProfile.class);
                username.setText(userProfile.getUserName());

                reportsList.clear();
                userList.clear();
                phones.clear();
                IdList.clear();


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
                            if (allowPhoneAccess.equals("true")) {
                                phones.add(No);
                            } else {
                                phones.add("0");
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//---------------------------------------------------

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.nav_changePassword:
                startActivity(new Intent(getApplicationContext(), UpdatePassword.class));
                break;
            case R.id.nav_my_report:
                startActivity(new Intent(this, MyReport.class).putExtra("from", "HomeIcon"));
                break;
//            case R.id.nav_privacyAndSecurity:
//                startActivity(new Intent(HomeActivity.this, privacyAndSecurity.class));
//                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                break;
        }
        return false;
    }


    //----------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
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

        for(Report rep: list){
            if(rep.getLostTitle().toLowerCase().contains(userInput)){
                newList.add(rep);
                findViewById(R.id.noMatchReports).setVisibility(View.GONE);


            }
            else{
                findViewById(R.id.noMatchReports).setVisibility(View.VISIBLE);
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



