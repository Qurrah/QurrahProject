package com.example.qurrah.GoogleNotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.qurrah.LocationTrackingServices.NotificationBroadcastReceiver;
import com.example.qurrah.Model.Report;
import com.example.qurrah.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.qurrah.App.App.CHANNEL_1_ID;
import static com.example.qurrah.App.App.CHANNEL_2_ID;


public class NearbyReportsNotification extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    static int id = 1;
    DatabaseReference reference;
    ArrayList<Report> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // to clear all notifications once the app open.
//        editTextTitle = findViewById(R.id.edit_text_title);
//        editTextMessage = findViewById(R.id.edit_text_message);
        if (id > 1){
            id =1;
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
        id =1;
    }

    public void sendOnChannel1(View v) {
        if (id == 1 || id % 5 == 0 ){
            String title = editTextTitle.getText().toString();
            String message = editTextMessage.getText().toString();
           Intent intent = new Intent(this, NearbyReportsNotification.class);
            PendingIntent pendingIntent =  PendingIntent.getActivity(this, 1,intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_message_black_24dp)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDeleteIntent(getDeleteIntent())
                    .build();

            notificationManager.notify(id++, notification);
        }else {
            sendOnChannel2();
        }
    }


    public void sendOnChannel2() {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();
        Intent intent = new Intent(this, NearbyReportsNotification.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 1,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setAutoCancel(true)
                .setDeleteIntent(getDeleteIntent())
                .setContentIntent(pendingIntent)
                .build();


        notificationManager.notify(id++, notification);


    }
    protected PendingIntent getDeleteIntent () {
        Intent intent = new Intent(NearbyReportsNotification. this, NotificationBroadcastReceiver.class ) ;
        intent.setAction( "notification_cancelled" ) ;
        return PendingIntent. getBroadcast (NearbyReportsNotification. this, 0 , intent , PendingIntent. FLAG_CANCEL_CURRENT );
    }
    public  ArrayList<Report>  findReportsWithin5Km(){
        ArrayList<Report> list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot ds: snapshot.child("Report").getChildren()) {
                        if(ds.getChildrenCount() > 0) {

                        }
                        Report report = ds.getValue(Report.class);
                        if (report.getCategoryOption().equals(getString(R.string.animal)) && report.getReportStatus().equals("نشط")){
                            list.add(report);


                        }
                    }
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Whoops!", Toast.LENGTH_SHORT).show();

            }
        });
        if(list.isEmpty()){
            return null;

        }
        return list;
    }
   public boolean isReportNearbyUserLocation(){
        return true;
    }

}
