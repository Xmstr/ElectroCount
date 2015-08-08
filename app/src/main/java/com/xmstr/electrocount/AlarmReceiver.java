package com.xmstr.electrocount;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Xmstr. Thanks to Dany Win
 */
public class AlarmReceiver extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("RECEIVER STARTED");
        if (checkForDay()) {
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent serviceIntent = new Intent(context, AlarmService.class);
            alarmIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 19);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            System.out.println("ALARM SET");
        } else {
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent broadcastIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent restartReceiverIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
            alarmMgr.cancel(restartReceiverIntent);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 15);
            calendar.add(Calendar.DATE, 1);
            // schedule the alarm
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), restartReceiverIntent);
            System.out.println("ALARM SET RECHECK NEX DAY");
        }
    }

    private boolean checkForDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (calendar.get(Calendar.DATE) == 26 && calendar.get(Calendar.HOUR_OF_DAY) <= 18) {
            return true;
        } else return false;
    }
}