package com.example.qurrah.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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

import com.google.android.material.navigation.NavigationView;
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

public class ChatActivity extends HomeActivity implements SearchView.OnQueryTextListener {

    CircleImageView profile_image;
    TextView username, noChats, Chats, MessagesNo;

    DatabaseReference reference;
    private RecyclerView recyclerView;
    SearchView searchView;

    private UserAdapter userAdapter;
    private List<UserProfile> mUsers;

    FirebaseUser fuser;

    private List<Chatlist> usersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatnav);
        updateItemColor(R.id.Chat);

        //----------------------------------------------------------------
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("محادثاتي");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(false);
        //----------------------------------------------------------------;
        setupUI(findViewById(R.id.parent));

        recyclerView = findViewById(R.id.recycler_view);
        noChats = findViewById(R.id.noChats);
        MessagesNo = findViewById(R.id.MessagesNo);

        Chats = findViewById(R.id.Chats);
        Chats.setText("المحادثات");
        findViewById(R.id.noMatchUsers).setVisibility(View.GONE);

        searchView = findViewById(R.id.search_view);

        searchView.setOnClickListener(v -> {
            findViewById(R.id.bar_layout).setVisibility(View.GONE);
            searchView.setIconified(false);
        });

        searchView.setOnQueryTextListener(this);




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
                    MessagesNo.setTextColor(Color.parseColor("#1683DA"));
                    MessagesNo.setText("(" + unread + ")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String newToken = FirebaseInstanceId.getInstance().getToken();
        updateToken(newToken);


        final DrawerLayout navDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view5);
        View header = navigationView.getHeaderView(0);
        username = header.findViewById(R.id.Username);


//---------------------------------------------------

        NavigationView mNavigationView = findViewById(R.id.nav_view5);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
//---------------------------------------------------
        bottomAppBar = findViewById(R.id.bottomAppBar);

        menuBottomAppBar = bottomAppBar.getMenu();
//---------------------------------------------------
        bottomAppBar.setNavigationOnClickListener(v -> {

            if (!navDrawer.isDrawerOpen(GravityCompat.START))
                navDrawer.openDrawer(GravityCompat.START);

            else
                navDrawer.closeDrawer(GravityCompat.END);

        });
//---------------------------------------------------

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference().child("Users"); //.child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.child(userId).getValue(UserProfile.class);
                username.setText(userProfile.getUserName());

                reportsList.clear();
                userList.clear();
                phones.clear();
                IdList.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile userInfo = snapshot.getValue(UserProfile.class);
                    String ID = userInfo.getId();
                    String userName = userInfo.getUserName();
                    String No = userInfo.getPhone();
                    String allowPhoneAccess = userInfo.getAllowPhone();


                    for (DataSnapshot ds : snapshot.child("Report").getChildren()) {
                        Report report = ds.getValue(Report.class);
                        if (!(report.getLatitude().equals("")) && report.getReportStatus().equals("نشط")) {
                            reportsList.add(report);
                            IdList.add(ID);
                            userList.add(userName);
                            if (allowPhoneAccess.equals("true")) {
                                phones.add(No);
                            } else {
                                phones.add("0");
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//---------------------------------------------------

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.nav_changePassword:
                startActivity(new Intent(getApplicationContext(), UpdatePassword.class));
                break;
            case R.id.nav_my_report:
                startActivity(new Intent(getApplicationContext(), MyReport.class));
                break;
//            case R.id.nav_privacyAndSecurity:
//                startActivity(new Intent(HomeActivity.this, privacyAndSecurity.class));
//                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                break;
        }
        return false;
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
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
                recyclerView.setAdapter(userAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); }catch(Exception e){}

    }


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

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

@Override
public void onBackPressed() {
    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
    intent.putExtra("from", "ChatActivity");
    startActivityForResult(intent, 0);
    overridePendingTransition(0,0); //0 for no animation
    finish();
}

    @Override
    public boolean onQueryTextSubmit(String query) {
        findViewById(R.id.bar_layout).setVisibility(View.VISIBLE);
        searchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<UserProfile> newList = new ArrayList<>();

        for(UserProfile user: mUsers){
            if(user.getUserName().toLowerCase().contains(userInput)){
                newList.add(user);
                findViewById(R.id.noMatchUsers).setVisibility(View.GONE);

            }
            if(newList.isEmpty()){
                findViewById(R.id.noMatchUsers).setVisibility(View.VISIBLE);
            }
        }
        userAdapter.updateList(newList);
        return true;
    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
            builder1.setMessage("هل أنت متأكد من حذف هذه المحادثة ؟");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "نعم",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
            final int position = viewHolder.getAdapterPosition();
            UserProfile deletedChat = mUsers.get(position);
            ArrayList<UserProfile> mUsers2 = new ArrayList<>();
            System.out.println("Here"+position);
            reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chatlist chat = snapshot.getValue(Chatlist.class);
                        if (deletedChat.getId().equals(chat.getId())) {
                            reference.child(snapshot.getKey()).removeValue();
                            mUsers.remove(position);
                            userAdapter.notifyItemRangeChanged(position,mUsers.size());
                            mUsers2.addAll(mUsers);
                            userAdapter.updateList(mUsers2);

                            final Snackbar snackBar = Snackbar.make(recyclerView, "تم الحذف", Snackbar.LENGTH_LONG);
                            snackBar.setActionTextColor(getResources().getColor(R.color.colorPrimary));

                            reference = FirebaseDatabase.getInstance().getReference("Chats");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        Chat chat = snapshot.getValue(Chat.class);
                                        if (chat.getReceiver().equals(deletedChat.getId()) && chat.getSender().equals(fuser.getUid())){
                                            chat.setSenderDelete(true);
                                            snapshot.getRef().setValue(chat);
                                        }else if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(deletedChat.getId())){
                                           chat.setReceiverDelete(true);
                                            snapshot.getRef().setValue(chat);
                                        }if(chat.isSenderDelete() && chat.isReceiverDelete()){
                                            snapshot.getRef().removeValue();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }



                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });}});
            builder1.setNegativeButton(
                    "إلغاء الامر",
                    (dialog, id) -> {
                        userAdapter.updateList((ArrayList<UserProfile>) mUsers);
                        dialog.cancel();
                    });

            AlertDialog alert11 = builder1.create();

            alert11.show();
            alert11.setCanceledOnTouchOutside(false);



            }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ChatActivity.this, R.color.darkRed))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightLabel("حذف")
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void setupUI(View view) {

        if(!(view instanceof SearchView)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    searchView.onActionViewCollapsed();
                    searchView.clearFocus();
                    findViewById(R.id.bar_layout).setVisibility(View.VISIBLE);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }
}


