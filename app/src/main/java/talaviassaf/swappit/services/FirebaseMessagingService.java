package talaviassaf.swappit.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.messaging.RemoteMessage;

import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (Application.sharedPreferences.getBoolean("Notifications", true)) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {

                RemoteMessage.Notification notification = remoteMessage.getNotification();

                if (notification != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        notificationManager.notify(0, new Notification.Builder(this).setContentText(notification.getBody())
                                .setSmallIcon(R.drawable.icon).build());
                    else
                        notificationManager.notify(0, new Notification.Builder(this).setContentText(notification.getBody())
                                .setSmallIcon(R.drawable.icon).getNotification());
                }
            }
        }
    }
}
