package com.example.qurrah;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.qurrah.Fragments.ChatsFragment;
import com.example.qurrah.Fragments.ProfileFragment;
import com.example.qurrah.Fragments.UsersFragment;
import com.example.qurrah.Model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    try{

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");

    profile_image = findViewById(R.id.profile_image);
    username = findViewById(R.id.username2);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            UserProfile user = dataSnapshot.getValue(UserProfile.class);
            username.setText(user.getUserName());
            if (user.getImageURL().equals("default")) {
                profile_image.setImageResource(R.mipmap.ic_launcher);
            } else {

                //change this
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    final TabLayout tabLayout = findViewById(R.id.tab_layout);
    final ViewPager viewPager = findViewById(R.id.view_pager);


    reference = FirebaseDatabase.getInstance().getReference("Chats");
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            int unread = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Chat chat = snapshot.getValue(Chat.class);
                if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                    unread++;
                }
            }

            if (unread == 0) {
                viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
            } else {
                viewPagerAdapter.addFragment(new ChatsFragment(), "(" + unread + ") Chats");
            }

            viewPagerAdapter.addFragment(new UsersFragment(), "Users");
            viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

            viewPager.setAdapter(viewPagerAdapter);

            tabLayout.setupWithViewPager(viewPager);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}
    catch (Exception e){e.getStackTrace();}
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status){

        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
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
}


