package com.example.mradmin.androidtestapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mrAdmin on 16.08.2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationMessage = remoteMessage.getNotification().getBody();

        String clickAction = remoteMessage.getNotification().getClickAction();

        String fromUserId = remoteMessage.getData().get("from_user_id");

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_app)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage)
                        .setSound(soundUri);

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("user_id", fromUserId);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);

        int notificationId = (int) System.currentTimeMillis();

        NotificationManager notifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyManager.notify(notificationId, builder.build());
    }
}
