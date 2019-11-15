package com.example.qurrah.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.qurrah.Model.Chat;
import com.example.qurrah.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        String type = chat.getMessageType();

        if(type.equals("text")){
            holder.image.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_message.setText(chat.getMessage());

        }else if(type.equals("image")){
            holder.show_message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get().load(chat.getMessage()).into(holder.image);

        }

//        if (imageurl.equals("default")){
//            holder.profile_image.setImageResource(R.drawable.ic_account_circle_black_24dp);
//        } else {
//            Picasso.get().load(imageurl).into(holder.profile_image);
//        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                if(type.equals("text")){
                    holder.txt_seen2.setVisibility(View.GONE);
                    holder.txt_seen.setText("قرِئت");

                }
                else if(type.equals("image")){
                    holder.txt_seen2.setVisibility(View.VISIBLE);
                    holder.txt_seen.setVisibility(View.GONE);
                    holder.txt_seen2.setText("قرِئت");}
            } else {
                if(type.equals("text")) {
                    holder.txt_seen2.setVisibility(View.GONE);
                    holder.txt_seen.setText("مرسلة");
                }else if (type.equals("image")){
                    holder.txt_seen2.setVisibility(View.VISIBLE);
                    holder.txt_seen.setVisibility(View.GONE);
                    holder.txt_seen2.setText("مرسلة");
                }
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView image;
        public TextView txt_seen, txt_seen2;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            image = itemView.findViewById(R.id.imageMessage);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            txt_seen2 = itemView.findViewById(R.id.txt_seen2);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}