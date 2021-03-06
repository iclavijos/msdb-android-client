package com.icesoft.msdb.android.client;

import com.icesoft.msdb.android.model.ActiveSeries;
import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.model.EventSession;
import com.icesoft.msdb.android.model.UpcomingSession;
import com.icesoft.msdb.android.model.UserSubscription;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MSDBAPIClient {

    @GET("api/home/calendar")
    Call<List<UpcomingSession>> getUpcomingSessions();

    @POST("api/account/device")
    Call<Void> registerToken(@Header("Authorization") String accessToken, @Body RequestBody deviceId);

    @DELETE("api/account/device/{deviceId}")
    Call<Void> removeToken(@Header("Authorization") String accessToken, @Path("deviceId") String deviceId);

    @GET("api/series-editions/active")
    Call<List<ActiveSeries>> getActiveSeries();

    @GET("api/account/subscriptions")
    Call<List<UserSubscription>> getUserSubscriptions(@Header("Authorization") String accessToken);

    @PUT("api/account/subscriptions")
    Call<Void> updateSubscriptions(@Header("Authorization") String accessToken, @Body List<UserSubscription> userSubscriptions);

    @GET("api/event-editions/{id}")
    Call<EventEdition> getEventDetails(@Header("Authorization") String accessToken, @Path("id") Long eventEditionId);

    @GET("api/event-editions/{id}/sessions")
    Call<List<EventSession>> getEventSessions(@Header("Authorization") String accessToken, @Path("id") Long eventEditionId);
}
