package com.icesoft.msdb.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

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
import java.util.Optional;
import java.util.Random;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "NotificationService";

    private static final Random random = new Random();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");

        boolean isRally = Boolean.parseBoolean(remoteMessage.getData().get("rally"));
        boolean isRaid = Boolean.parseBoolean(remoteMessage.getData().get("raid"));

        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(Long.parseLong(remoteMessage.getData().get("startTime"))),
                ZoneId.systemDefault());

        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("eventEditionId", Long.parseLong(remoteMessage.getData().get("eventEditionId")));
        intent.putExtra("eventName", remoteMessage.getData().get("eventName"));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
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
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Integer notificationId = Optional.ofNullable(remoteMessage.getData().get("sessionId"))
                .map(sessionId -> Integer.parseInt(sessionId))
                .orElse(random.nextInt(Integer.MAX_VALUE));

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
        String logoUrl250 = logoUrl
                .replace("image/upload", "image/upload/w_250,c_scale")
                .replace(".png", ".jpg");

        Glide.with(this)
                .asBitmap()
                .load(logoUrl250)
                .fitCenter()
                .into(notificationTargetCompact);
        Glide.with(this)
                .asBitmap()
                .load(logoUrl250.replace("w_250", "w_2048"))
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
}
