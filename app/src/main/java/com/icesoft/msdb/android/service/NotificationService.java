package com.icesoft.msdb.android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.icesoft.msdb.android.HomeActivity;
import com.icesoft.msdb.android.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Random;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "NotificationService";

    private static final Random random = new Random();
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");

        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(Long.parseLong(remoteMessage.getData().get("startTime"))),
                ZoneId.systemDefault());

        String msg = getResources().getString(R.string.upcomingSessionMessage,
                remoteMessage.getData().get("sessionName"),
                remoteMessage.getData().get("eventName"),
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime));
        sendNotification(msg);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, "onNewToken: " + s);
        super.onNewToken(s);
    }

    @Override
    public void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent");
        super.handleIntent(intent);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle(getString(R.string.upcomingSession))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
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

        notificationManager.notify(random.nextInt(Integer.MAX_VALUE), notificationBuilder.build());
    }
}
