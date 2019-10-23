package com.example.qurrah.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qurrah.R;
import com.example.qurrah.Model.Report;
import com.example.qurrah.UI.RegisteredUserReportView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyReportAdapter extends RecyclerView.Adapter<MyReportAdapter.ViewHolder> {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    Context context;
    ArrayList<Report> reports;
    String userID = mAuth.getUid();
    String date1,date2;
    DatabaseReference databaseReferenceUserReport = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Report");

    public MyReportAdapter(Context context, ArrayList<Report> reports) {

        this.context = context;
        this.reports = reports;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_report_card_view, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Report report = reports.get(position);
        holder.lostTitle.setText(reports.get(position).getLostTitle());
        holder.address.setText(reports.get(position).getAddress());
        if (holder.address.getText().equals("") || holder.address.getText().equals("الموقع"))
                 holder.imgAddress.setVisibility(View.GONE);

        //holder.lostDate.setText(reports.get(position).getDate());
        //--------------------------------------handling date ------------------------------
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
        String date = dateFormat.format(new Date());
        if (reports.get(position).getDate().substring(0,10).equals(date.substring(0,10))){
            if (reports.get(position).getDate().substring(reports.get(position).getDate().length()-2).equalsIgnoreCase("pm"))
                date2 ="م";
            else
                date2 = "ص";
            date1 = reports.get(position).getDate().substring(11,16)+" "+date2;

            holder.lostDate.setText(date1);
        }else if (reports.get(position).getDate().substring(0,7).equals(date.substring(0,7))){
            int diff = Integer.parseInt(date.substring(8,10))-Integer.parseInt(reports.get(position).getDate().substring(8,10));
            if (diff>=1 && diff <=7){
                // Toast.makeText(context,String.valueOf(diff),Toast.LENGTH_SHORT).show();
                DateFormat format2=new SimpleDateFormat("EEEE", Locale.forLanguageTag("ar-SA"));
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, -diff);
                holder.lostDate.setText(format2.format(cal.getTime()));
            }
            else {
                holder.lostDate.setText(reports.get(position).getDate().substring(0,10));
            }
        }




//---------------------------------------------------------------------------------
        holder.updateButton.setVisibility(View.GONE);
        String status = reports.get(position).getReportStatus();
        holder.status.setText("نشط");
        holder.status.setChecked(true);
        holder.status.setEnabled(true);

        if (status.equals("مغلق")) {
            holder.status.setText("مغلق");
            holder.status.setChecked(false);
            holder.status.setEnabled(false);
            holder.updateButton.setVisibility(View.GONE);
        }

        Picasso.get().load(reports.get(position).getPhoto()).into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.img.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.INVISIBLE);

            }
        });




//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
//                builder1.setMessage("سوف يتم حذف بلاغك، هل انت متأكد؟");
//                builder1.setCancelable(true);
//
//                builder1.setPositiveButton(
//                        "نعم",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                // dialog.cancel();
//
//                                databaseReferenceUserReport.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                            Report rep = snapshot.getValue(Report.class);
//                                            if (report.getDate() == rep.getDate()) {
//
//                                                databaseReferenceUserReport.child(snapshot.getKey()).removeValue();
//                                                reports.remove(position);
//                                                notifyDataSetChanged();
//                                            }
//
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                                Toast.makeText(context, "تم حذف بلاغك", Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        });
//
//                builder1.setNegativeButton(
//                        "إلغاء الامر",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//                AlertDialog alert11 = builder1.create();
//
//                alert11.show();
//
//            }
//        });

        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                builder1.setMessage("هل أنت متأكد أنه تم ايجاد المفقود وتريد إغلاق طلبك ؟");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "نعم",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // dialog.cancel();

                                databaseReferenceUserReport.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Report rep = snapshot.getValue(Report.class);
                                            if (report.getDate() == rep.getDate()) {

                                                databaseReferenceUserReport.child(snapshot.getKey()).child("ReportStatus").setValue("مغلق");
                                                notifyDataSetChanged();
                                                holder.status.setText("مغلق");
                                                holder.status.setChecked(false);
                                                holder.status.setEnabled(false);
                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                Toast.makeText(context, "تم إغلاق بلاغك", Toast.LENGTH_SHORT).show();

                            }

                        });

                builder1.setNegativeButton(
                        "إلغاء الامر",
                        (dialog, id) -> {
                            holder.status.setChecked(true);
                            dialog.cancel();
                        });

                AlertDialog alert11 = builder1.create();

                alert11.show();
                alert11.setCanceledOnTouchOutside(false);

            }
        });




        holder.linear.setOnClickListener(view -> {

            Intent intent = new Intent(context, RegisteredUserReportView.class);
//                    intent.putExtra("Report", (Parcelable) reports.get(position));
            intent.putExtra("Image", report.getPhoto());
            intent.putExtra("Title", report.getLostTitle());
            intent.putExtra("Description", report.getLostDescription());
            intent.putExtra("report", report);
            intent.putExtra("lat", report.getLatitude());
            intent.putExtra("lon", report.getLongitude());


            context.startActivity(intent);
        });






//        holder.updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//
//                databaseReferenceUserReport.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            Report rep = snapshot.getValue(Report.class);
//                            if (report.getDate().equals(rep.getDate())) {
//                                reference = databaseReferenceUserReport.child(snapshot.getKey()).getRef();
//                                Intent intent = new Intent(context, EditReportActivity.class);
//                                intent.putExtra("lostTitle", report.getLostTitle());
//                                intent.putExtra("lostDescription", report.getLostDescription());
//                                intent.putExtra("categoryOption", report.getCategoryOption());
//                                intent.putExtra("ReportTypeOption", report.getReportTypeOption());
//                                intent.putExtra("photo", report.getPhoto());
//                                intent.putExtra("location", report.getLocation());
//                                intent.putExtra("key", snapshot.getKey());
//                                intent.putExtra("date", report.getDate());
//                                context.startActivity(intent);
//
//
////                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
////                                builder.setMessage("وصف البلاغ");
////
////// Set up the input
////                                final EditText input = new EditText(context);
////                                input.setSingleLine(false);  //add this
////                                input.setLines(4);
////                                input.setMaxLines(5);
////                                input.setGravity(Gravity.RIGHT | Gravity.TOP);
////                                input.setHorizontalScrollBarEnabled(false); //this
////                                input.setText(reports.get(position).getLostDescription());
////                                builder.setView(input);
////
////                                builder.setPositiveButton("تعديل", new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        m_Text =  input.getText().toString();
////                                        //Toast.makeText(context, m_Text, Toast.LENGTH_SHORT).show();
////                                        reference.child("lostDescription").setValue("yes");
////
////
////                                    }
////                                });
////                                builder.setNegativeButton("الغاء الامر", new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        dialog.cancel();
////                                    }
////                                });
////
////                                builder.show();
//
//                            }
//
//                        }
//
//                    }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return reports.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        Button deleteButton, updateButton;
        ImageView img;
        TextView lostTitle, lostDate;
        ProgressBar progressBar;
        Switch status;
        LinearLayout linear;
        TextView address;
        ImageView imgAddress;


        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            progressBar  = itemView.findViewById(R.id.progressbarImg);
            lostTitle = itemView.findViewById(R.id.textView1);
            lostDate = itemView.findViewById(R.id.textView2);
            status = itemView.findViewById(R.id.status);
            deleteButton = itemView.findViewById(R.id.delete);
            updateButton = itemView.findViewById(R.id.update);
            linear = itemView.findViewById(R.id.linearLayout);
            address = itemView.findViewById(R.id.address);
            imgAddress = itemView.findViewById(R.id.imageAddress);

        }
    }
    public void updateList(ArrayList<Report> newList) {
        reports = new ArrayList<>();
        reports.addAll(newList);
        notifyDataSetChanged();


    }

}





