package com.xmstr.electrocount;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

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
        if (intent.getAction().equals("android.intent.action.AIRPLANE_MODE")) {
            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            System.out.println("RECEIVER ACTION OK");
            Intent intent1 = new Intent(context, AlarmService.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            //calendar.set(Calendar.HOUR_OF_DAY, 6);
            //calendar.set(Calendar.MINUTE, 11);
            long futureInMillis = SystemClock.elapsedRealtime() + 5000;
            //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            //        AlarmManager.INTERVAL_DAY, alarmIntent);
            //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            //        1000 * 60 * 1, alarmIntent);
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
        }
    }
}