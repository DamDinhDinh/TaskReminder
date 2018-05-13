package com.example.damdinhdinh.taskreminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.damdinhdinh.taskreminder.model.Task;

public class ReminderService extends IntentService {
    private Task task;
    private  Intent intent;
    public ReminderService(){
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        intent = new Intent(this, MainActivity.class);
        task = (Task) intent.getSerializableExtra("task");
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(task.getName());
        builder.setContentText(task.getDescribe());
        builder.setSmallIcon(R.drawable.icons8_to_do_48);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(task.getId(), notificationCompat);
    }
}
