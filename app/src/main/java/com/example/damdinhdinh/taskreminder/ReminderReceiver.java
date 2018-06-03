package com.example.damdinhdinh.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.damdinhdinh.taskreminder.model.Task;

public class ReminderReceiver extends BroadcastReceiver {
    public ReminderReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getExtras().getInt("task_id");
        String name = intent.getExtras().getString("task_name");
        String describe = intent.getExtras().getString("task_describe");
        String date = intent.getExtras().getString("task_date");
        String time = intent.getExtras().getString("task_time");
        int repeat = intent.getExtras().getInt("task_repeat");
        Intent notifyIntent = new Intent(context, ReminderService.class);
        notifyIntent.putExtra("task_id", id);
        notifyIntent.putExtra("task_name", name);
        notifyIntent.putExtra("task_describe", describe);
        notifyIntent.putExtra("task_date", date);
        notifyIntent.putExtra("task_time", time);
        notifyIntent.putExtra("task_repeat", repeat);
        context.startService(notifyIntent);
    }
}
