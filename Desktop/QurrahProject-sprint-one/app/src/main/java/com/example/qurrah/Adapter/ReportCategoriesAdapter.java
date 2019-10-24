package com.example.qurrah.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qurrah.R;
import com.example.qurrah.Model.Report;
import com.example.qurrah.UI.ViewReport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportCategoriesAdapter extends RecyclerView.Adapter<ReportCategoriesAdapter.ViewHolder> {

    DatabaseReference reference;
    Context context;
    ArrayList<Report> reports;
    ArrayList<String> users;
    ArrayList<String> phones;
    ArrayList<String> Id;
    String searchString = "";
    String date1, date2, type="none", CU="none";



    public ReportCategoriesAdapter(Context context, ArrayList<Report> reports , ArrayList<String> users , ArrayList<String> phones, ArrayList<String> Id) {

        this.Id=Id;
        this.users = users;
        this.context = context;
        this.reports = reports;
        this.phones = phones;

    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_report_categories, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            CU = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }catch (Exception e){
            type="guest";
        }
        final Report report = reports.get(position);
        final String userName = users.get(position);
        final String id = Id.get(position);
        if(CU.equals(id)) {
            type = "current";
        }
        else if(!CU.equals(id) && type.equals("none")){
            type = "notCurrent";
        }

        holder.lostTitle.setText(reports.get(position).getLostTitle());
        holder.address.setText(reports.get(position).getAddress());
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

//        report OnClick

        String finalType = type;
        holder.linear.setOnClickListener(view -> {
            String No = phones.get(position);
            Intent intent = new Intent(context, ViewReport.class);
//                    intent.putExtra("Report", (Parcelable) reports.get(position));
            intent.putExtra("Image", report.getPhoto());
            intent.putExtra("Title", report.getLostTitle());
            intent.putExtra("Description", report.getLostDescription());
            intent.putExtra("UserName", userName);
            intent.putExtra("WhatsApp" , No);
            intent.putExtra("userid" , id);
            intent.putExtra("userType" , finalType);
            intent.putExtra("lat",report.getLatitude());
            intent.putExtra("lon",report.getLongitude());
            intent.putExtra("locationDescription", report.getLocation());
            intent.putExtra("address", report.getAddress());



            context.startActivity(intent);
        });



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

//        holder.whatsApp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//
//
//                try {
//                    String No = phones.get(position);
//
//                    Uri uri = Uri.parse("smsto:" + No);
//                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//                    i.setPackage("com.whatsapp");
//                    context.startActivity(Intent.createChooser(i, ""));
//                    Intent waIntent = new Intent(Intent.ACTION_SEND);
//                    waIntent.setType("text/plain");
//                    String text = "YOUR TEXT HERE";
//
//
//                } catch (Exception e) {
//                    Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
//                            .show();
//                }
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
        ImageView img;
        TextView lostTitle, lostDate;
        ProgressBar progressBar;
        LinearLayout linear;
        TextView address;



        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            progressBar = itemView.findViewById(R.id.progressbarImg);
            lostTitle = itemView.findViewById(R.id.lostTitle);
            lostDate = itemView.findViewById(R.id.date);
            linear = itemView.findViewById(R.id.linear);
            address = itemView.findViewById(R.id.address);


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





