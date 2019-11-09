package com.example.qurrah.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.qurrah.Adapters.UserAdapter;
import com.example.qurrah.Model.Chat;
import com.example.qurrah.Model.Chatlist;
import com.example.qurrah.Model.Report;
import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.FirebaseNotifications.Token;

import com.example.qurrah.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ChatActivity extends AppCompatActivity {
String raghad;
    CircleImageView profile_image;
    TextView username, noChats, Chats;

    DatabaseReference reference;
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<UserProfile> mUsers;

    FirebaseUser fuser;

    private List<Chatlist> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chats);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        recyclerView = findViewById(R.id.recycler_view);
        noChats = findViewById(R.id.noChats);
        Chats = findViewById(R.id.Chats);
        Chats.setText("المحادثات");


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        noChats.setVisibility(View.INVISIBLE);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


            reference = FirebaseDatabase.getInstance().getReference("Chats");
            reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int unread = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Chat chat = snapshot.getValue(Chat.class);
                assert chat != null;
                if (chat.getReceiver().equals(fuser.getUid()) && !chat.isIsseen()) {
                    unread++;
                }
            }

            if (unread != 0) {
                Chats.setText("("+unread+") المحادثات");
            }
    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        String newToken =FirebaseInstanceId.getInstance().getToken();
        updateToken(newToken);

    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        try{
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile user = snapshot.getValue(UserProfile.class);
                    for (Chatlist chatlist : usersList) {
                        if (user.getId().equals(chatlist.getId())) {
                            mUsers.add(user);
                        }
                    }
                }
                if (mUsers.isEmpty())
                    noChats.setVisibility(View.VISIBLE);
                userAdapter = new UserAdapter(ChatActivity.this, mUsers, true);
                recyclerView.setAdapter(userAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); }catch(Exception e){}

    }


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//
//        profile_image = findViewById(R.id.profile_image);
//        username = findViewById(R.id.username2);
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                UserProfile user = dataSnapshot.getValue(UserProfile.class);
//                username.setText(user.getUserName());
//                if (user.getImageURL().equals("default")) {
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
//                } else {
//
//                    //change this
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        final TabLayout tabLayout = findViewById(R.id.tab_layout);
//        final ViewPager viewPager = findViewById(R.id.view_pager);
//
//    }
//    reference = FirebaseDatabase.getInstance().getReference("Chats");
//    reference.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//            int unread = 0;
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                Chat chat = snapshot.getValue(Chat.class);
//                if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
//                    unread++;
//                }
//            }
//
//            if (unread == 0) {
//                viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
//            } else {
//                viewPagerAdapter.addFragment(new ChatsFragment(), "(" + unread + ") Chats");
//            }
//
//            viewPagerAdapter.addFragment(new UsersFragment(), "Users");
//            viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
//
//            viewPager.setAdapter(viewPagerAdapter);
//
//            tabLayout.setupWithViewPager(viewPager);
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    });



//    class ViewPagerAdapter extends FragmentPagerAdapter {
//
//        private ArrayList<Fragment> fragments;
//        private ArrayList<String> titles;
//
//        ViewPagerAdapter(FragmentManager fm){
//            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//            this.fragments = new ArrayList<>();
//            this.titles = new ArrayList<>();
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        void addFragment(Fragment fragment, String title){
//            fragments.add(fragment);
//            titles.add(title);
//        }
//
//        // Ctrl + O
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles.get(position);
//        }
//    }

    private void status(String status){

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        status("online");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        status("offline");
//    }
@Override
public void onBackPressed() {
    Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
    startActivity(intent);
    finish();
}


//    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
//            final int position = viewHolder.getAdapterPosition();
//            deletedReport = newList.get(position);
//            System.out.println("Here"+position);
//            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//
//
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Report rep = snapshot.getValue(Report.class);
//                        if (deletedReport.getDate() == rep.getDate()) {
//                            reference.child(snapshot.getKey()).removeValue();
//                            newList.remove(position);
//                            adapter.notifyItemRangeChanged(position,newList.size());
//                            adapter.updateList(newList);
//
//                            final Snackbar snackBar = Snackbar.make(recyclerView, "تم الحذف", Snackbar.LENGTH_LONG);
//                            snackBar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
//                            snackBar.setAction("تراجع", v -> {
//                                snackBar.dismiss();
//                                findViewById(R.id.noReports).setVisibility(View.GONE);
//                                reference.child(snapshot.getKey()).setValue(deletedReport);
//                                newList.add(position, deletedReport);
//                                adapter.notifyItemInserted(position);
//                                adapter.updateList(newList);
//
//
//
//
//                            }).show();
//
//
//                        }
//
//
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//
//            });
//        }
//
//        @Override
//        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MyReport.this, R.color.darkRed))
//                    .addActionIcon(R.drawable.ic_delete_black_24dp)
//                    .addSwipeRightLabel("حذف")
//                    .create()
//                    .decorate();
//
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
//    };


}


