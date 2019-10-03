package com.example.qurrah;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportCategoriesAdapter extends RecyclerView.Adapter<ReportCategoriesAdapter.ViewHolder> {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    Context context;
    ArrayList<Report> reports;
    String searchString = "";

    public ReportCategoriesAdapter(Context context, ArrayList<Report> reports) {

        this.context = context;
        this.reports = reports;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_report_categories, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull  final ViewHolder holder, final int position) {
        final Report report = reports.get(position);
        holder.lostTitle.setText(reports.get(position).getLostTitle());
        holder.lostDate.setText(reports.get(position).getDate());

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

        if ((searchString!= null) && (!searchString.isEmpty())) {
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
        if(spannableStringSearch !=null){
            holder.lostTitle.setText(spannableStringSearch);
        }
        else{
            holder.lostTitle.setText(report.getLostTitle());
        }







    }


    @Override
    public int getItemCount() {
        return reports.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView lostTitle, lostDate;
        ProgressBar progressBar;


        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            progressBar  = itemView.findViewById(R.id.progressbarImg);
            lostTitle = itemView.findViewById(R.id.lostTitle);
            lostDate = itemView.findViewById(R.id.date);

        }
    }
    public void updateList(ArrayList<Report> newList, String searchString){
        this.searchString=searchString;
        reports = new ArrayList<>();
        reports.addAll(newList);
        notifyDataSetChanged();


    }


}



