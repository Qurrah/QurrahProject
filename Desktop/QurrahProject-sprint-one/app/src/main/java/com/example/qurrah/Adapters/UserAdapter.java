package com.example.qurrah.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qurrah.Model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.qurrah.UI.MessageActivity;
import com.example.qurrah.Model.Chat;
import com.example.qurrah.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserProfile> mUsers;
    private boolean ischat;

    String theLastMessage, theLastMessageType;

    public UserAdapter(Context mContext, List<UserProfile> mUsers, boolean ischat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final UserProfile user = mUsers.get(position);
        holder.username.setText(user.getUserName());
        if (user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Picasso.get().load(user.getImageURL()).into(holder.profile_image);
            //Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat){
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                   if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            theLastMessageType = chat.getMessageType();

                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("لاتوجد رسائل");
                        break;

                    default:
//                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
//                        String date = dateFormat.format(new Date());
//                        if (reports.get(position).getDate().substring(0,10).equals(date.substring(0,10))){
//                            if (reports.get(position).getDate().substring(reports.get(position).getDate().length()-2).equalsIgnoreCase("pm"))
//                                date2 ="م";
//                            else
//                                date2 = "ص";
//                            date1 = reports.get(position).getDate().substring(11,16)+" "+date2;
//
//                            holder.lostDate.setText(date1);
//                        }else if (reports.get(position).getDate().substring(0,7).equals(date.substring(0,7))){
//                            int diff = Integer.parseInt(date.substring(8,10))-Integer.parseInt(reports.get(position).getDate().substring(8,10));
//                            if (diff>=1 && diff <=7){
//                                // Toast.makeText(context,String.valueOf(diff),Toast.LENGTH_SHORT).show();
//                                DateFormat format2=new SimpleDateFormat("EEEE", Locale.forLanguageTag("ar-SA"));
//                                Calendar cal = Calendar.getInstance();
//                                cal.setTime(new Date());
//                                cal.add(Calendar.DATE, -diff);
//                                holder.lostDate.setText(format2.format(cal.getTime()));
//                            }
//                            else {
//                                holder.lostDate.setText(reports.get(position).getDate().substring(0,10));
//                            }
                        if(theLastMessageType.equals("text"))
                            last_msg.setText(theLastMessage);
                        else if(theLastMessageType.equals("image"))
                            last_msg.setText("صورة");
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateList(ArrayList<UserProfile> newList) {
        mUsers = new ArrayList<>();
        mUsers.addAll(newList);
        notifyDataSetChanged();


    }
}
