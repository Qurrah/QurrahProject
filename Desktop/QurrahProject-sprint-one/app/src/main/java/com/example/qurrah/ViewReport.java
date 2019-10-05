package com.example.qurrah;




import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewReport extends AppCompatActivity {

private TextView title , description , name ;
private ImageView photo;
private String reporTitle , reportDescription , reportUser , reportWhatsApp  ;
private String reportImg;
private Button whatsapp ;
private FirebaseAuth firebaseAuth ;
private FirebaseDatabase firebaseDatabase;
private DatabaseReference reference;
private String uid , userID;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_page);


        uid ="First";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("Users");

        title= findViewById(R.id.reportTitle);
        description=findViewById(R.id.reportDes);
        photo = findViewById(R.id.reportImg);
        name = findViewById(R.id.reportUsername);
        whatsapp = findViewById(R.id.whatsapp);


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




    }
}
