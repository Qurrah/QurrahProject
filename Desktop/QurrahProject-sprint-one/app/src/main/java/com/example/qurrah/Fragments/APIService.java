package com.example.qurrah.Fragments;

import com.example.qurrah.FirebaseNotifications.MyResponse;
import com.example.qurrah.FirebaseNotifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAEog5PrE:APA91bEmhUPQ2bYVaRV6rvq6LEYCQg4VGYDgB11p4gU7o4BIznzjwCq8MDXbRcjWjdhQy0rW36NLMmaZN9VWNimGOPL5-fNvJjQK8ssv8m909vxpOXxmt7BJmUpexstkq3zKN8n6rRxr"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
