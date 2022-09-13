package com.icesoft.msdb.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.Callback;
import com.auth0.android.result.Credentials;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.icesoft.msdb.android.activity.EventDetailsActivity;
import com.icesoft.msdb.android.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "NotificationService";

    private static final Random random = new Random();
    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");

        Boolean isRally = Boolean.parseBoolean(remoteMessage.getData().get("rally"));
        Boolean isRaid = Boolean.parseBoolean(remoteMessage.getData().get("raid"));

        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(Long.parseLong(remoteMessage.getData().get("startTime"))),
                ZoneId.systemDefault());

        auth0 = new Auth0(this);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("eventEditionId", Long.parseLong(remoteMessage.getData().get("eventEditionId")));
        intent.putExtra("eventName", remoteMessage.getData().get("eventName"));
        intent.putExtra("accessToken", "");

        Log.d(TAG, "onMessageReceived: countdown created");
        final CountDownLatch awaitCredentialsSignal = new CountDownLatch(1);

        credentialsManager.getCredentials(new Callback<>() {
            @Override
            public void onSuccess(@Nullable Credentials payload) {
                intent.putExtra("accessToken", payload.getAccessToken());
                awaitCredentialsSignal.countDown();
                Log.d(TAG, "onSuccess: countdown decreased");
            }

            @Override
            public void onFailure(@NonNull CredentialsManagerException error) {
                awaitCredentialsSignal.countDown();
                Log.d(TAG, "onSuccess: countdown decreased onFailure");
            }
        });

        try {
            Log.d(TAG, "onMessageReceived: awaiting...");
            awaitCredentialsSignal.await();
            Log.d(TAG, "onMessageReceived: proceeding...");
        } catch (InterruptedException e) {
            Log.e(TAG, "onMessageReceived: ", e);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews compactView = new RemoteViews(getPackageName(), R.layout.notification_compact_layout);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded_layout);

        String msg = getResources().getString(R.string.upcomingSessionMessage,
                remoteMessage.getData().get("sessionName"),
                remoteMessage.getData().get("eventName"),
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime));

        compactView.setTextViewText(R.id.notificationMessageTextView, msg);
        expandedView.setTextViewText(R.id.eventTextView, getResources().getString(R.string.event, remoteMessage.getData().get("eventName")));
        expandedView.setTextViewText(R.id.sessionTextView, getResources().getString(R.string.session, remoteMessage.getData().get("sessionName")));
        expandedView.setTextViewText(R.id.startTimeTextView, getResources().getString(R.string.startTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime)));

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(compactView)
                        .setCustomBigContentView(expandedView)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "MSDB notifications channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        String sessionId = remoteMessage.getData().get("sessionId");
        int notificationId = random.nextInt(Integer.MAX_VALUE);
        if (sessionId != null) {
            notificationId = Integer.parseInt(sessionId);
        }
        Notification notification = notificationBuilder.build();

        NotificationTarget notificationTargetCompact = new NotificationTarget(
                getApplicationContext(),
                R.id.seriesLogoCompactImageView,
                compactView,
                notification,
                notificationId);
        NotificationTarget notificationTargetSeriesLogoExpanded = new NotificationTarget(
                getApplicationContext(),
                R.id.seriesLogoExpandedImageView,
                expandedView,
                notification,
                notificationId);

        String logoUrl = remoteMessage.getData().get("seriesLogoUrl");
        if (isDarkMode()) {
            logoUrl = logoUrl.replace("image/upload", "image/upload/e_negate");
        }
        Glide.with(this)
                .asBitmap()
                .load(logoUrl)
                .fitCenter()
                .into(notificationTargetCompact);
        Glide.with(this)
                .asBitmap()
                .load(logoUrl)
                .fitCenter()
                .into(notificationTargetSeriesLogoExpanded);

        if (!isRally && !isRaid) {
            expandedView.setTextViewText(R.id.whereTextView, getResources().getString(R.string.where, remoteMessage.getData().get("racetrack")));
            LocalDateTime endTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(Long.parseLong(remoteMessage.getData().get("endTime"))),
                    ZoneId.systemDefault());
            expandedView.setTextViewText(R.id.endTimeTextView, getResources().getString(R.string.endTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(endTime)));

            NotificationTarget notificationTargetTrackLayoutExpanded = new NotificationTarget(
                    getApplicationContext(),
                    R.id.racetrackLayoutImageView,
                    expandedView,
                    notification,
                    notificationId);

            String racetrackLayoutUrl = remoteMessage.getData().get("racetrackLayoutUrl");
            if (isDarkMode()) {
                racetrackLayoutUrl = racetrackLayoutUrl.replace("image/upload", "image/upload/e_negate");
            }
            Glide.with(this)
                    .asBitmap()
                    .load(racetrackLayoutUrl)
                    .fitCenter()
                    .into(notificationTargetTrackLayoutExpanded);
        } else {
            expandedView.setTextViewText(R.id.endTimeTextView, getResources().getString(R.string.distance, remoteMessage.getData().get("distance")));
            expandedView.setViewVisibility(R.id.racetrackLayoutImageView, View.GONE);
            expandedView.setViewVisibility(R.id.whereTextView, View.GONE);
        }

        notificationManager.notify(notificationId, notification);
    }

    private boolean isDarkMode() {
        int uiMode = this.getBaseContext().getResources().getConfiguration().uiMode;

        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
