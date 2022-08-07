package com.sohrab.obd.reader;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationRceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 0);
        NotificationManager manager= (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);


    }
}
