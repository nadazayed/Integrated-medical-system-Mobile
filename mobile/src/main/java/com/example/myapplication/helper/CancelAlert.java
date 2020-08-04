package com.example.myapplication.helper;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CancelAlert extends BroadcastReceiver
{
    int idAplicacion,dose;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        idAplicacion = intent.getIntExtra("ID", 0);

        Log.e("TESTING", "the id is " + String.valueOf(idAplicacion));
        clearNotification(context);
    }

    public void clearNotification(Context context)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(idAplicacion);
    }
}