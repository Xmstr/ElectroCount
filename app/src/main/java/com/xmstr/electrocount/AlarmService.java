package com.xmstr.electrocount;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

/**
 * Created by Xmstr. Thanks to Dany Win
 */
public class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager nManager;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("SERVICE STARTTED");
        createAndShowNoti();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createAndShowNoti() {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_power_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_power_white_36dp))
                .setTicker("Снять показания")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Элекросчетчик")
                .setContentText("Пришло время снять показания!"); // Текст уведомления
        Notification notification = builder.build();
        nManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, notification);
    }
}