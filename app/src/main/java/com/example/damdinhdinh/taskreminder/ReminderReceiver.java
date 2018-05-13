package com.example.damdinhdinh.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    public ReminderReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(intent);
    }
}
