package com.example.qurrah.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
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
import androidx.core.content.ContextCompat;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyReportAdapter extends RecyclerView.Adapter<MyReportAdapter.ViewHolder> {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    Context context;
    ArrayList<Report> reports;
    String userID = mAuth.getUid();
    String date1,date2;
    String searchString = "";
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
        } else {
            holder.lostDate.setText(reports.get(position).getDate().substring(0,10));
        }

//---------------------------------Report type--------------------

        if(reports.get(position).getReportTypeOption().equals("فاقد"))
            holder.type.setText("فاقد");
        else if (reports.get(position).getReportTypeOption().equals("معثور عليه"))
            holder.type.setText("معثور عليه");


//---------------------------------------------------------------------------------
//        holder.updateButton.setVisibility(View.GONE);
        String status = reports.get(position).getReportStatus();
        holder.status.setText("نشط");
        holder.status.setChecked(true);
        holder.status.setEnabled(true);

        if (status.equals("مغلق")) {
            holder.status.setText("مغلق");
            holder.status.setChecked(false);
            holder.status.setEnabled(false);
//            holder.updateButton.setVisibility(View.GONE);
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



        SpannableString spannableStringSearch = null;

        if ((searchString != null) && (!searchString.isEmpty())) {
            spannableStringSearch = new SpannableString(report.getLostTitle());
            Pattern pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(report.getLostTitle());
            while (matcher.find()) {
                spannableStringSearch.setSpan(new BackgroundColorSpan(
                                ContextCompat.getColor(context, R.color.yellow)),
                        matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (spannableStringSearch != null) {
            holder.lostTitle.setText(spannableStringSearch);
        } else {
            holder.lostTitle.setText(report.getLostTitle());
        }




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
                                                holder.status.setAlpha((float) 0.5);


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
            intent.putExtra("locationDescription", report.getLocation());
            intent.putExtra("address", report.getAddress());

            context.startActivity(intent);
        });



    }


    @Override
    public int getItemCount() {
        return reports.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//        Button deleteButton, updateButton;
        ImageView img;
        TextView lostTitle, lostDate , type;
        ProgressBar progressBar;
        Switch status;
        LinearLayout linear;
        TextView address;
        ImageView imgAddress;


        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            progressBar  = itemView.findViewById(R.id.progressbarImg);
            lostTitle = itemView.findViewById(R.id.lostTitle);
            lostDate = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
//            deleteButton = itemView.findViewById(R.id.delete);
//            updateButton = itemView.findViewById(R.id.update);
            linear = itemView.findViewById(R.id.linearLayout);
            address = itemView.findViewById(R.id.address);
            imgAddress = itemView.findViewById(R.id.imageAddress);
            type = itemView.findViewById(R.id.rType);

        }
    }

    public void updateList(ArrayList<Report> newList, String searchString) {
        this.searchString = searchString;
        reports = new ArrayList<>();
        reports.addAll(newList);
        notifyDataSetChanged();
    }


    public void updateList(ArrayList<Report> newList) {
        reports = new ArrayList<>();
        reports.addAll(newList);
        notifyDataSetChanged();


    }

}





