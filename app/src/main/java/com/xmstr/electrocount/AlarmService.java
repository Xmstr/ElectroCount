package com.xmstr.electrocount;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;

/**
 * Created by Xmstr. Thanks to Dany Win
 */
public class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager nManager;
    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("SERVICE STARTTED");
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.advice_icon)
                        // большая картинка
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.hungrycat))
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("Уведомление")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Напоминание")
                        //.setContentText(res.getString(R.string.notifytext))
                .setContentText("ШИНДОВС 10"); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();

        nManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, notification);

        return super.onStartCommand(intent, flags, startId);
    }
}