package com.nextgen.medvault.Services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.nextgen.medvault.R;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String descp = intent.getStringExtra("descp");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "REMINDER")
                        .setSmallIcon(R.drawable.alarm_24)
                        .setContentTitle(title)
                        .setContentText(descp)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}