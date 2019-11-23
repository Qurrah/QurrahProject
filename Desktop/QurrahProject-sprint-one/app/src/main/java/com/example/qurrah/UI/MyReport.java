package com.example.qurrah.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
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

public class MyReport extends AppCompatActivity implements SearchView.OnQueryTextListener {

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Report> list, newList;
    MyReportAdapter adapter;
    Report report;
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

//------------------------------------------------
        //----------------------------------------------------------------
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("بلاغاتي");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        //----------------------------------------------------------------
        newList = new ArrayList<>();
//------------------------------------------------
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyReport.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);


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


                    }

                    report = ds.getValue(Report.class);
                    list.add(report);

                }
                newList.addAll(list);
                adapter = new MyReportAdapter(MyReport.this, list);
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
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




