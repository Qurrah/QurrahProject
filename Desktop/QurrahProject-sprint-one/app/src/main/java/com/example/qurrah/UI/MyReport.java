package com.example.qurrah.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qurrah.Adapters.MyReportAdapter;
import com.example.qurrah.R;
import com.example.qurrah.Model.Report;
import com.example.qurrah.ReportTypesWithTabs.All_Reports;
import com.example.qurrah.ReportTypesWithTabs.Found_reports;
import com.example.qurrah.ReportTypesWithTabs.Missing_reports;
import com.example.qurrah.ReportTypesWithTabs.main.SectionsPagerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyReport extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Report> list, newList;
    MyReportAdapter adapter;
//    Button allbtn,missingbtn, findingbtn;
    Report report;

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

//------------------------------------------------
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        newList = new ArrayList<>();
//------------------------------------------------
        // inputs
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();




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






//
//// second filter
//        allbtn= findViewById(R.id.all);
//        allbtn.setOnClickListener(v -> {
//            allbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
//            missingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//            findingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//            SecondFilter("all");
//
//        });
//        missingbtn= findViewById(R.id.missing);
//        missingbtn.setOnClickListener(v -> {
//            missingbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
//            allbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//            findingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//            SecondFilter("missing");
//        });
//        findingbtn=findViewById(R.id.finding);
//        findingbtn.setOnClickListener(v -> {
//            findingbtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
//            missingbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//            allbtn.setBackgroundColor(getResources().getColor(R.color.lightGrey1));
//            SecondFilter("finding");
//        });
        //

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyReport.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
//        allbtn.setVisibility(View.GONE);
//        findingbtn.setVisibility(View.GONE);
//        missingbtn.setVisibility(View.GONE);

        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Report");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                newList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(ds.getChildrenCount() > 0) {
                        findViewById(R.id.progressbar).setVisibility(View.GONE);

//                        allbtn.setVisibility(View.VISIBLE);
//                        findingbtn.setVisibility(View.VISIBLE);
//                        missingbtn.setVisibility(View.VISIBLE);

                    }

                    report = ds.getValue(Report.class);
                    list.add(report);

                }
                newList.addAll(list);
                adapter = new MyReportAdapter(MyReport.this, list);
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
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


    }
    //-------------------------Second Filter Method---------------------------------
    public void SecondFilter(String flag){

        newList.clear();
        switch(flag){
            case "all":
                newList.addAll(list);
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
    //
    Report deletedReport = null;
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            deletedReport = newList.get(position);
            System.out.println("Here"+position);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Report rep = snapshot.getValue(Report.class);
                        if (deletedReport.getDate() == rep.getDate()) {
                            reference.child(snapshot.getKey()).removeValue();
                            newList.remove(position);
                            adapter.notifyItemRangeChanged(position,newList.size());
                            adapter.updateList(newList);

                            final Snackbar snackBar = Snackbar.make(recyclerView, "تم الحذف", Snackbar.LENGTH_LONG);
                            snackBar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
                            snackBar.setAction("تراجع", v -> {
                                snackBar.dismiss();
                                findViewById(R.id.noReports).setVisibility(View.GONE);
                                reference.child(snapshot.getKey()).setValue(deletedReport);
                                newList.add(position, deletedReport);
                                adapter.notifyItemInserted(position);
                                adapter.updateList(newList);




                            }).show();


                        }



                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MyReport.this, R.color.darkRed))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightLabel("حذف")
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

// ------------------Tabs------------------------
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Found_reports(), "المعثورات");
        adapter.addFragment(new Missing_reports() , "المفقودات");
        adapter.addFragment(new All_Reports(), "الكل");
        viewPager.setAdapter(adapter);
    }



}




