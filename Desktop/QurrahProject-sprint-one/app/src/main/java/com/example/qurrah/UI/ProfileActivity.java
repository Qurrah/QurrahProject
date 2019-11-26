package com.example.qurrah.UI;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePic;
    private EditText profileName, profileEmail,profilePhone;
    private ImageView editProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Switch allowPhoneaccess ;
    private TextView changePassword;
    private Button update;

//    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.ivProfilePic);
        profileName = findViewById(R.id.tvProfileName);
        profileEmail = findViewById(R.id.tvProfileEmail);
        profilePhone = findViewById(R.id.tvProfilePhone);
        allowPhoneaccess=findViewById(R.id.accessPhoneNo);
        changePassword=findViewById(R.id.editPassword);
        editProfile= findViewById(R.id.edit_profile);
        update= findViewById(R.id.update);

    //----------------------------------------------------------------
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("الملف الشخصي");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        //----------------------------------------------------------------

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseStorage = FirebaseStorage.getInstance();
        String userId=firebaseAuth.getCurrentUser().getUid();

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(userId);

//        StorageReference storageReference = firebaseStorage.getReference();
//        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).fit().centerCrop().into(profilePic);
//            }
//        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profilePhone.setText("" + userProfile.getPhone());
                profileName.setText("" + userProfile.getUserName());
                profileEmail.setText("" + userProfile.getUserEmail());
                String isChecked =userProfile.getAllowPhone();
                allowPhoneaccess.setChecked(isChecked.equals("true"));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        allowPhoneaccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!allowPhoneaccess.isChecked()){
                    databaseReference.child("allowPhone").setValue("false");

                }
                else{
                    databaseReference.child("allowPhone").setValue("true");

                }
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editProfile.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
                profileName.setEnabled(true);
                profileEmail.setEnabled(true);
                profilePhone.setEnabled(true);

            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, UpdatePassword.class));
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                update.setVisibility(View.GONE);
                editProfile.setVisibility(View.VISIBLE);
                profileName.setEnabled(false);
                profileEmail.setEnabled(false);
                profilePhone.setEnabled(false);

            }
        });


//        profileUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(ProfileActivity.this, UpdateProfile.class));
//            }
//        });
//
//        changePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(ProfileActivity.this, UpdatePassword.class));
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
