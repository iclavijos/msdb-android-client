package com.icesoft.msdb.android.service;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icesoft.msdb.android.client.MSDBAPIClient;
import com.icesoft.msdb.android.model.UpcomingSession;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BackendService {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.motorsports-database.racing")
            // .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)))
            .build();

    private final MSDBAPIClient msdbAPIClient = retrofit.create(MSDBAPIClient.class);

    public List<UpcomingSession> getUpcomingSessions() {
        Call<List<UpcomingSession>> msdbCall = msdbAPIClient.getUpcomingSessions();

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

    public Void registerToken(String accessToken, String deviceToken) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), deviceToken);
        Call<Void> msdbCall = msdbAPIClient.registerToken("Bearer " + accessToken, body);
        Response<Void> response = null;
        try {
            response = msdbCall.execute();
            if (!response.isSuccessful()) {
                Log.e("MSDBService", "Couldn't retrieve data");
            }
        } catch (IOException e) {
            Log.e("MSDBService", "Couldn't retrieve data", e);
        }

        return null;
    }
}
