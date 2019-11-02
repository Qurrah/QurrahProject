package com.example.qurrah.LocationTrackingServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context , Intent intent) {
        String action = intent.getAction() ;
        if (action.equals( "notification_cancelled" )) {
            Toast. makeText (context , "Notification Removed" , Toast. LENGTH_SHORT ).show() ;
           LocationTracking.id = 1;
        }
    }
}