package com.nextgen.medvault.Services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.nextgen.medvault.R;

public class MedicineReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String medicine = intent.getStringExtra("medicine");
        String dosage = intent.getStringExtra("dosage");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "MED_REMINDER")
                        .setSmallIcon(R.drawable.alarm_24)
                        .setContentTitle("Medicine Reminder")
                        .setContentText(medicine + " - " + dosage)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}