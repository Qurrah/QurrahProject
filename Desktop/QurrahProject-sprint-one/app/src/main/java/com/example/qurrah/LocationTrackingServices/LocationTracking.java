package com.example.qurrah.LocationTrackingServices;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.qurrah.Model.Report;
import com.example.qurrah.R;
import com.example.qurrah.UI.SecondActivity;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static com.example.qurrah.App.App.CHANNEL_1_ID;


public class LocationTracking extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationProviderClient;

    boolean registered = false;
    public static final String JOB_STATE_CHANGED = "jobStateChanged";
    public static final String LOCATION_ACQUIRED = "locAcquired";
    public static double currentLatitude, currentLongitude, firstSeenLatitude = 0, firstSeenLongitude = 0;
    public static NotificationManagerCompat notificationManager;
    DatabaseReference reference;
    public static int counter =0 ,id = 1;
    public static ArrayList<String> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = NotificationManagerCompat.from(this);
        startBackgroundService();

        if (id > 1) {
            id = 1;
        }
        startActivity(new Intent(this, SecondActivity.class));

    }

    private BroadcastReceiver jobStateChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            if (intent.getAction().equals(JOB_STATE_CHANGED)) {
            } else if (intent.getAction().equals(LOCATION_ACQUIRED)) {
                if (intent.getExtras() != null) {
                    Bundle b = intent.getExtras();
                    Location l = b.getParcelable("location");
                    currentLatitude = l.getLatitude();
                    currentLongitude = l.getLongitude();
                    try {
                        TimeUnit.SECONDS.sleep(1); // sleep for 10 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    findReportsWithin5Km();
                    Toast.makeText(getApplicationContext(), currentLatitude + " and " + currentLongitude, Toast.LENGTH_LONG).show();

                    if (firstSeenLatitude == 0 && firstSeenLongitude == 0){
                        // first time seen latitude and longitude
                        firstSeenLatitude = currentLatitude;
                        firstSeenLongitude = currentLongitude;
                    }else if (!isUserLocationWithin5kmOfFirstSeenLocation(currentLatitude, currentLongitude)){
                      Toast.makeText(getApplicationContext(),"users leaves his geoFence, starting a new one", Toast.LENGTH_LONG).show();
                            firstSeenLatitude = 0;
                            firstSeenLongitude = 0;
                             ids.removeAll(ids);
                        }


                } else {
                    Log.d("intent", "null");
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        notificationManager = NotificationManagerCompat.from(this);

    }

    public void stopBackgroundService() {
        if (getSharedPreferences("track", MODE_PRIVATE).getBoolean("isServiceStarted", false)) {
            Log.d("registered", " on stop service");
            Intent stopJobService = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                stopJobService = new Intent(LocationJobService.ACTION_STOP_JOB);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(stopJobService);
            } else {
                Toast.makeText(getApplicationContext(), "yet to be coded - stop service", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startBackgroundService() {
        if (!registered) {
            IntentFilter i = new IntentFilter(JOB_STATE_CHANGED);
            i.addAction(LOCATION_ACQUIRED);
            LocalBroadcastManager.getInstance(this).registerReceiver(jobStateChanged, i);
        }
        JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        jobScheduler.schedule(new JobInfo.Builder(LocationJobService.LOCATION_SERVICE_JOB_ID,
                new ComponentName(this, LocationJobService.class))
                .setOverrideDeadline(3000)
                .setPersisted(true)
                .setRequiresDeviceIdle(false)
                .build());
    }

    @Override
    protected void onDestroy() {
        try {
            if (registered) {
                unregisterReceiver(jobStateChanged);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    // TODO: Stay the photo here
    public void sendOnChannel1(String title, String description, String photo) {
        Intent intent = new Intent(this, SecondActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDeleteIntent(getDeleteIntent())
                .build();

        notificationManager.notify(id++, notification);
    }


    public void findReportsWithin5Km(){
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : snapshot.child("Report").getChildren()) {

                        Report report = ds.getValue(Report.class);
                        if (report.getReportStatus().equals("نشط")) {

                            if (!report.getLatitude().isEmpty() && !report.getLongitude().isEmpty()) {
                               if(isReportWithin5km(Double.parseDouble(report.getLatitude()), Double.parseDouble(report.getLongitude()))) {
                                   Log.d("e", "onDataChange:" + ds.getRef().getKey());
                                   if (!isReportAlreadyNotified(report.getLostTitle() +" " +report.getLostDescription()+" "+report.getDate())) {
                                           ids.add(report.getLostTitle() +" " +report.getLostDescription()+" "+report.getDate());
                                        //   Log.d("e", "onDataChange:" +"HERE"+ids);
                                       sendOnChannel1(report.getLostTitle(),report.getLostDescription(), report.getPhoto());
                                   }

                               }
                            }

                            }
                        }
                    }
                }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean isReportWithin5km(double ReportLatitude , double ReportLongitude){
        float[] results = new float[1];
        Location.distanceBetween(currentLatitude, currentLongitude, ReportLatitude, ReportLongitude, results);
        float distanceInMeters = results[0];
        boolean isWithin5km = distanceInMeters <=5000;
        if (isWithin5km){
           return true;
        }
           return false;

    }
    private boolean isReportAlreadyNotified(String id){
        boolean test = false;
        if ( ids.contains(id) ) {
           test = true;
        }
        return test;
    }

    //To improve performance purpose only...

    private boolean isUserLocationWithin5kmOfFirstSeenLocation(double UserLatitude, double UserLongitude){
        float[] results = new float[1];
        Location.distanceBetween(firstSeenLatitude, firstSeenLongitude, UserLatitude, UserLongitude, results);
        float distanceInMeters = results[0];
        boolean isWithin5km = distanceInMeters <=5000;
        if (isWithin5km){
            return true;
        }
        return false;
    }
    protected PendingIntent getDeleteIntent () {
        Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
        intent.setAction("notification_cancelled");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
