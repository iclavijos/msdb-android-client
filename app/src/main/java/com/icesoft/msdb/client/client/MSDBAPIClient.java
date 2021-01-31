package com.icesoft.msdb.client.client;

import com.icesoft.msdb.client.model.UpcomingSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MSDBAPIClient {

    @GET("api/home/calendar")
    public Call<List<UpcomingSession>> getUpcomingSessions();
}
