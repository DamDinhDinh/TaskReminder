package com.example.damdinhdinh.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

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
        int groupTaskId = intent.getExtras().getInt("group_task_id");
        Intent notifyIntent = new Intent(context, ReminderService.class);
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        Log.e("screen on", ""+isScreenOn);
        if(isScreenOn==false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

            wl_cpu.acquire(10000);
        }
        notifyIntent.putExtra("task_id", id);
        notifyIntent.putExtra("task_name", name);
        notifyIntent.putExtra("task_describe", describe);
        notifyIntent.putExtra("task_date", date);
        notifyIntent.putExtra("task_time", time);
        notifyIntent.putExtra("task_repeat", repeat);
        notifyIntent.putExtra("group_task_id", groupTaskId);
        context.startService(notifyIntent);
    }
}
