package com.example.qurrah.UI;




import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ViewReport extends AppCompatActivity {

private TextView title , description , name ;
private ImageView photo;
private String reporTitle , reportDescription , reportUser , reportWhatsApp  ;
private String reportImg;
private Button whatsapp , chatting;
private FirebaseAuth firebaseAuth ;
private FirebaseDatabase firebaseDatabase;
private DatabaseReference reference;
private String uid , userID;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        uid ="First";
        firebaseAuth = FirebaseAuth.getInstance();
        userID = getIntent().getStringExtra("userid");
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("Users");

        title= findViewById(R.id.reportTitle);
        description=findViewById(R.id.reportDes);
        photo = findViewById(R.id.reportImg);
        name = findViewById(R.id.reportUsername);
        whatsapp = findViewById(R.id.whatsapp);
        chatting = findViewById(R.id.chatting);



        reporTitle =getIntent().getStringExtra("Title");
        reportDescription = getIntent().getStringExtra("Description");
        reportImg = getIntent().getStringExtra("Image");
        reportUser = getIntent().getStringExtra("UserName");
        reportWhatsApp = getIntent().getStringExtra("WhatsApp");


        name.setText(reportUser);
        Picasso.get().load(reportImg).into(photo);
        title.setText(title.getText()+"\n"+reporTitle);
        description.setText(description.getText()+"\n"+reportDescription);



        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {


                    Uri uri = Uri.parse("smsto:" + reportWhatsApp);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(i, ""));
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = "YOUR TEXT HERE";


                } catch (Exception e) {
                    Toast.makeText(ViewReport.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewReport.this, MessageActivity.class);
                intent.putExtra("userid", userID);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
