package com.example.qurrah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyReport extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Report> list, newList;
    MyReportAdapter adapter;
    Button allbtn,missingbtn, findingbtn;
    Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout);
//------------------------------------------------
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        newList = new ArrayList<>();
//------------------------------------------------
        // inputs
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
// second filter
        allbtn=(Button) findViewById(R.id.all);
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

                            final Snackbar snackBar = Snackbar.make(recyclerView, deletedReport.getLostTitle(), Snackbar.LENGTH_LONG);
                            snackBar.setAction("تراجع", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    snackBar.dismiss();
                                    findViewById(R.id.noReports).setVisibility(View.GONE);
                                    reference.child(snapshot.getKey()).setValue(deletedReport);
                                    newList.add(position, deletedReport);
                                    adapter.notifyItemInserted(position);
                                    adapter.updateList(newList);




                                }
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
}




