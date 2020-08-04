package com.example.myapplication.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.MedicationActivity;

public class Alert extends BroadcastReceiver
{
    int idAplicacion,dose;
    String name, desc, msg;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        idAplicacion = intent.getIntExtra("ID", 0);
        name = intent.getStringExtra("NAME");
        dose = intent.getIntExtra("DOSE", 0);
        desc = intent.getStringExtra("DESC");

        msg = "Take "+dose+" from "+name;

        Log.e("TESTING", "the id is " + String.valueOf(idAplicacion));
        Log.e("MSG", msg);
        createNotification(context, "WeCare", msg);
    }

    public void createNotification(Context context, String title, String msgText)
    {

        Intent i = new Intent(context, MedicationActivity.class);
        i.putExtra("NAME", name);
        i.putExtra("DOSE", dose);
        i.putExtra("DESC", desc);

        PendingIntent notificIntent = PendingIntent.getActivity(context,idAplicacion, i ,PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder mBuilder = new
                NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(msgText)
                .setSmallIcon(android.R.drawable.ic_dialog_info);


        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        mBuilder.setDefaults(NotificationCompat.PRIORITY_HIGH);
//        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Log.d("Hay8","DCM10");
            mBuilder.setChannelId("com.example.wecareboth");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Log.d("Hay8","DCM11");
            NotificationChannel channel = new NotificationChannel(
                    "com.example.wecareboth",
                    "WeCareBoth",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (mNotificationManager != null)
            {
                Log.d("Hay8","DCM12");
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        mNotificationManager.notify(idAplicacion, mBuilder.build());

    }
}