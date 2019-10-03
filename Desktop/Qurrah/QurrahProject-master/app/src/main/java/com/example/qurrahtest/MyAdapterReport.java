package com.example.qurrahtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapterReport extends RecyclerView.Adapter<MyAdapterReport.ViewHolder>{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    Context context;
    ArrayList<Report> reports;
    private String m_Text = "";
    String userID = mAuth.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Reports");

    public MyAdapterReport(Context context, ArrayList<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Report report = reports.get(position);
        holder.lostTitle.setText(reports.get(position).getLostTitle());
        holder.lostDate.setText(reports.get(position).getDate());

        Picasso.get().load(reports.get(position).getPhoto()).into(holder.img);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                builder1.setMessage("سوف يتم حذف طلبك، هل انت متأكد؟");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "نعم",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // dialog.cancel();

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Report rep = snapshot.getValue(Report.class);
                                            if (report.getLostDescription().equals(rep.getLostDescription()) ) {
                                                databaseReference.child(snapshot.getKey()).removeValue();

                                                reports.remove(position);
                                                notifyDataSetChanged();
                                                if (reports.size() == 0) {
                                                    // MainActivity.textViewEmptyView.setVisibility(View.VISIBLE);
                                                }
                                                break;

                                            }

                                        }

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


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
        });

        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Report rep = snapshot.getValue(Report.class);
                            if (report.getLostDescription().equals(rep.getLostDescription()) ) {
                                reference=  databaseReference.child(snapshot.getKey()).getRef();
//
//
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("وصف البلاغ");

// Set up the input
                                final EditText input = new EditText(context);
                                input.setSingleLine(false);  //add this
                                input.setLines(4);
                                input.setMaxLines(5);
                                input.setGravity(Gravity.RIGHT | Gravity.TOP);
                                input.setHorizontalScrollBarEnabled(false); //this
                                input.setText(reports.get(position).getLostDescription());
                                builder.setView(input);

                                builder.setPositiveButton("تعديل", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_Text =  input.getText().toString();
                                        //Toast.makeText(context, m_Text, Toast.LENGTH_SHORT).show();
                                        reference.child("lostDescription").setValue(m_Text);


                                    }
                                });
                                builder.setNegativeButton("الغاء الامر", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();

                            }

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }


    @Override
    public int getItemCount() {
        return reports.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        Button deleteButton, updateButton;
        ImageView img;
        TextView lostTitle, lostDate;


        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            lostTitle = itemView.findViewById(R.id.textView1);
            lostDate = itemView.findViewById(R.id.textView2);
            deleteButton = itemView.findViewById(R.id.delete);
            updateButton = itemView.findViewById(R.id.update);
        }
    }


}



