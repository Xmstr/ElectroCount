package com.xmstr.electrocount;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Xmstr. Thanks to Dany Win
 */
public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Receiver running", Toast.LENGTH_SHORT).show();
        Intent alarmIntent = new Intent(context, AlarmService.class);
        context.startService(alarmIntent);
    }
}