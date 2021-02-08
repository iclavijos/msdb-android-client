package com.icesoft.msdb.android.client;

import com.icesoft.msdb.android.model.UpcomingSession;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MSDBAPIClient {

    @GET("api/home/calendar")
    public Call<List<UpcomingSession>> getUpcomingSessions();

    @POST("api/account/device")
    public Call<Void> registerToken(@Header("Authorization") String accessToken, @Body RequestBody deviceId);
}
