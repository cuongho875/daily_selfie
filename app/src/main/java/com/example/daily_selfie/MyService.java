package com.example.daily_selfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    private static final String CHANNEL_ID = "1";

    @Override
    public void onCreate() {
        super.onCreate();
        Timer timer = new Timer();
        long delay = TimeUnit.MINUTES.toMillis(2);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                createNotificationChannel();
            }
        }, delay, delay);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createNotificationChannel() {
// Build a new notification, which informs the user that the system
// handled their interaction with the previous notification.
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_camera);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_camera)
                    .setLargeIcon(bitmap)
                    .setContentTitle("Daily_Selfie")
                    .setContentText("Time for another selfie")
                    .build();

// Issue the new notification.
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = 1;
            notificationManager.notify(notificationId, notification);
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, 0);
        }
    }
}
