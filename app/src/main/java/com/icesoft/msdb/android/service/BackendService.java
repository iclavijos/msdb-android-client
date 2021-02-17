package com.icesoft.msdb.android.service;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icesoft.msdb.android.client.MSDBAPIClient;
import com.icesoft.msdb.android.model.ActiveSeries;
import com.icesoft.msdb.android.model.UpcomingSession;
import com.icesoft.msdb.android.model.UserSubscription;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BackendService {

    private static final BackendService _instance = new BackendService();

    private BackendService() {}

    public static BackendService getInstance() {
        return _instance;
    }

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.motorsports-database.racing")
            //.baseUrl("http://10.0.2.2:8080/")
            //.baseUrl("http://192.168.1.185:8080")
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
                Log.e("MSDBService", "Couldn't retrieve upcoming sessions: " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("MSDBService", "Couldn't process upcoming sessions request", e);
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
                Log.e("MSDBService", "Couldn't register token " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("MSDBService", "Register token request not processed", e);
        }

        return null;
    }

    public List<ActiveSeries> getActiveSeries() {
        Call<List<ActiveSeries>> msdbCall = msdbAPIClient.getActiveSeries();

        Response<List<ActiveSeries>> response = null;
        try {
            response = msdbCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("MSDBService", "Couldn't retrieve active series: " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("MSDBService", "Couldn't process get active series request", e);
        }

        return null;
    }

    public List<UserSubscription> getUserSubscriptions(String accessToken) {
        Call<List<UserSubscription>> msdbCall = msdbAPIClient.getUserSubscriptions("Bearer " + accessToken);

        Response<List<UserSubscription>> response = null;
        try {
            response = msdbCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("MSDBService", "Couldn't retrieve user subscriptions: " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("MSDBService", "Couldn't process get user subscriptions request", e);
        }

        return null;
    }

    public Void updateUserSubscriptions(String accessToken, List<UserSubscription> userSubscriptions) {
        Call<Void> msdbCall = msdbAPIClient.updateSubscriptions(accessToken, userSubscriptions);
        Response<Void> response = null;
        try {
            response = msdbCall.execute();
            if (!response.isSuccessful()) {
                Log.e("MSDBService", "Couldn't update user subscriptions " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("MSDBService", "User subscriptions update not processed", e);
        }
        return null;
    }
}
