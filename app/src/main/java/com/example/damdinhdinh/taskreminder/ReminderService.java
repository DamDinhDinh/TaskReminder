package com.example.damdinhdinh.taskreminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.Task;

public class ReminderService extends IntentService {
    private Task task;
    private  Intent intent;
    private DatabaseSQLite database;
    public ReminderService(){
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        database = new DatabaseSQLite(this, "task.sqlite", null, 1);
        int id = intent.getExtras().getInt("task_id");
        String name = intent.getExtras().getString("task_name");
        String describe = intent.getExtras().getString("task_describe");
        String date = intent.getExtras().getString("task_date");
        String time = intent.getExtras().getString("task_time");
        int repeat = intent.getExtras().getInt("task_repeat");
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setContentTitle(name);
//        builder.setContentText(describe);
//        builder.setSmallIcon(R.drawable.icons8_to_do_48);
//        Intent notifyIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        Notification notificationCompat = builder.build();
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        managerCompat.notify(id, notificationCompat);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_custom);
        remoteViews.setTextViewText(R.id.tv_notification_title, name);
        remoteViews.setTextViewText(R.id.tv_notification_describe, describe);
        remoteViews.setTextViewText(R.id.tv_notification_date, date);
        remoteViews.setTextViewText(R.id.tv_notification_time, time);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(name);
        builder.setContentText(describe);
        builder.setSmallIcon(R.drawable.icons8_to_do_48);
        builder.setContent(remoteViews);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(id, notification);
        if (repeat == 0){
            String sql = "UPDATE task SET task_notify = 0 WHERE task_id = "+id;
            database.queryData(sql);
        }
    }
}
