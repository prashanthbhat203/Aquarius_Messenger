package com.example.aquariusmessenger.api;

import com.example.aquariusmessenger.models.notificationModel.Sender;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAD0ykyds:APA91bHHDteuaF6WKdfBv-FnRzHHfsv9A1H6Z8aq83dlRP3AXLCSUvdkR2Xq-igXjHgft2ziBi05mfmpWgjrx_7c3cirQNtBalVriw3Zp2I6Ao2ZyQzxSHwCRg9UKS5Xbqw9P1pVaO87"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
