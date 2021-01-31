package com.icesoft.msdb.client.service;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icesoft.msdb.client.client.MSDBAPIClient;
import com.icesoft.msdb.client.model.UpcomingSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MSDBService {

    public List<UpcomingSession> getUpcomingSessions() {
        List<UpcomingSession> upcomingSessions = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                // .baseUrl("https://wwww.motorsports-database.com/")
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)))
                .build();

        MSDBAPIClient randomQuoteAPI = retrofit.create(MSDBAPIClient.class);
        Call<List<UpcomingSession>> msdbCall = randomQuoteAPI.getUpcomingSessions();

        Response<List<UpcomingSession>> response = null;
        try {
            response = msdbCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("MSDBService", "Couldn't retrieve data");
            }
        } catch (IOException e) {
            Log.e("MSDBService", "Couldn't retrieve data", e);
        }

        return null;
    }
}
