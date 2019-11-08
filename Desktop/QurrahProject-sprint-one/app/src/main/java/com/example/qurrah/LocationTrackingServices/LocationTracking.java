package com.example.qurrah.LocationTrackingServices;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.qurrah.Model.Report;
import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.R;
import com.example.qurrah.UI.MainActivity;
import com.example.qurrah.UI.MapActivity;
import com.example.qurrah.UI.SecondActivity;
import com.example.qurrah.UI.ViewReport;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static com.example.qurrah.App.App.CHANNEL_1_ID;
import static com.example.qurrah.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class LocationTracking extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationProviderClient;

    boolean registered = false;
    public static final String JOB_STATE_CHANGED = "jobStateChanged";
    public static final String LOCATION_ACQUIRED = "locAcquired";
    public static double currentLatitude, currentLongitude, firstSeenLatitude = 0, firstSeenLongitude = 0;
    public static NotificationManagerCompat notificationManager;
    String type="none", CU="none";
    DatabaseReference reference;
    public static int counter =0 ,id = 1;
    public static ArrayList<String> ids = new ArrayList<>();
    String greeting="اهلين ";
    String lostmsg=" فيه احد يحتاج فزعتك ";
    String foundmsg =" فيه شي ضايع لك ؟ ";
    String msg="";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = NotificationManagerCompat.from(this);
        startBackgroundService();
        if (id > 1) {
            id = 1;
        }
        startActivity(new Intent(this, MainActivity.class));

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
                    findReportsWithin5Km();
                    Toast.makeText(getApplicationContext(), currentLatitude + " and " + currentLongitude, Toast.LENGTH_LONG).show();

                    if (firstSeenLatitude == 0 && firstSeenLongitude == 0){
                        // first time seen latitude and longitude
                        firstSeenLatitude = currentLatitude;
                        firstSeenLongitude = currentLongitude;
                    }else if (!isUserLocationWithin5kmOfFirstSeenLocation(currentLatitude, currentLongitude)){
                        Toast.makeText(getApplicationContext(),"user leaves his geoFence, starting a new one", Toast.LENGTH_LONG).show();
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
    public void sendOnChannel1(String userid, String title, String description,String lat,String lon, String photo,String address, String location,String username,String phoneNo, String type,String message) {
        Intent intent = new Intent(this, ViewReport.class);
//                    intent.putExtra("Report", (Parcelable) reports.get(position));
        intent.putExtra("Image", photo);
        intent.putExtra("Title",title);
        intent.putExtra("Description",description);
        intent.putExtra("UserName", username);
        intent.putExtra("userid" , userid);
        intent.putExtra("WhatsApp",phoneNo);
        intent.putExtra("lat",lat);
        intent.putExtra("lon",lon);
        intent.putExtra("locationDescription", location);
        intent.putExtra("address",address);
        intent.putExtra("userType",type);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.qurrah)
                .setContentTitle(message)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(title)
                        .addLine(description))
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
        try {
            CU = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }catch (Exception e){
            type="guest";
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    UserProfile userInfo = snapshot.getValue(UserProfile.class);
                    String userName = userInfo.getUserName();
                    String userID=userInfo.getId();
                    String phoneNo = userInfo.getPhone();
                    String currentUserName="";


                    if(CU.equals(userID)) {
                        type = "current";
                        currentUserName = dataSnapshot.child(CU).getValue(UserProfile.class).getUserName();

                    }else if(!CU.equals(userID) && type.equals("none")){
                        type = "notCurrent";
                        currentUserName = dataSnapshot.child(CU).getValue(UserProfile.class).getUserName();

                    }

                    for (DataSnapshot ds : snapshot.child("Report").getChildren()) {

                        Report report = ds.getValue(Report.class);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String date = dateFormat.format(new Date());
                        if (report.getReportStatus().equals("نشط")&&report.getDate().contains(date)) {

                            if (!report.getLatitude().isEmpty() && !report.getLongitude().isEmpty()) {
                                if(isReportWithin5km(Double.parseDouble(report.getLatitude()), Double.parseDouble(report.getLongitude()))) {
                                    Log.d("e", "onDataChange:" + ds.getRef().getKey());
                                    if (!isReportAlreadyNotified(report.getLostTitle() +" " +report.getLostDescription()+" "+report.getDate())) {
                                        ids.add(report.getLostTitle() +" " +report.getLostDescription()+" "+report.getDate());
                                        //   Log.d("e", "onDataChange:" +"HERE"+ids);
                                        if(report.getReportTypeOption().equals("فاقد"))
                                        {
                                            msg=greeting+currentUserName+lostmsg;
                                        }
                                        else{
                                            msg=greeting+currentUserName+foundmsg;
                                        }
                                        sendOnChannel1(
                                                userID,
                                                report.getLostTitle(),
                                                report.getLostDescription(),
                                                report.getLatitude(),
                                                report.getLongitude(),
                                                report.getPhoto(),
                                                report.getAddress(),
                                                report.getLocation(),
                                                userName,
                                                phoneNo,
                                                type,
                                                msg);
                                    }

                                }
                            }

                        }
                    }//end report for
                }//end for dataSnapshot

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
    private void showPermissionDialog() {
        if (!LocationJobService.checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

}

