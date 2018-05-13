package com.example.damdinhdinh.taskreminder;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damdinhdinh.taskreminder.adapter.GroupTaskAdapter;
import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.GroupTask;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imgNewGroupTask;
    private TextView tvNewGroupTask;
    private ListView lvGroupTask;
    private DatabaseSQLite database;
    private ArrayList<GroupTask> arrGroupTask;
    private GroupTaskAdapter groupTaskAdapter;
    private AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgNewGroupTask = findViewById(R.id.img_icon_add_new_group);
        tvNewGroupTask = findViewById(R.id.tv_add_new_group);
        lvGroupTask = findViewById(R.id.lv_group_task);

        arrGroupTask = new ArrayList<>();

        database = new DatabaseSQLite(MainActivity.this, "task.sqlite", null, 1);
        String createTableGroupTask = "CREATE TABLE IF NOT EXISTS groupTask(groupTask_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "groupTask_name VARCHAR(200), groupTask_iconIndex INTEGER)";
        database.queryData(createTableGroupTask);
        String createTableTask = "CREATE TABLE IF NOT EXISTS task(task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_name VARCHAR(200), task_describe VARCHAR (200), task_day INTEGER(2), task_month INTEGER(2), " +
                "task_year INTEGER, task_hour INTEGER(2), task_minute INTEGER(2), task_repeat INTEGER(1), " +
                "task_notify INTEGER(1), groupTask_id INTEGER, FOREIGN KEY(groupTask_id) REFERENCES groupTask(groupTask_id))";
        database.queryData(createTableTask);

        updateListViewGroupTask();


        tvNewGroupTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCreateGroupTask();
            }
        });
        lvGroupTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentListTaskActivity = new Intent(MainActivity.this, ListTaskActivity.class);
                intentListTaskActivity.putExtra("groupTask_id", arrGroupTask.get(i).getId());
                startActivity(intentListTaskActivity);
            }
        });
        lvGroupTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_group_task, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.menu_delete_group:
                                    String sql = "DELETE FROM groupTask WHERE groupTask_id ="+ arrGroupTask.get(i).getId();
                                    database.queryData(sql);
                                    updateListViewGroupTask();
                                    return true;
                                case R.id.menu_edit_group:
                                    dialogEditGroupTask(arrGroupTask.get(i));
                                    return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                return true;
            }
        });

    }

    void dialogCreateGroupTask() {
         final Dialog dialog = new Dialog(this);
         dialog.setContentView(R.layout.dialog_create_group_task);
         dialog.setCanceledOnTouchOutside(false);
         dialog.setTitle("Create Group Task");

         Window dialogWindow = dialog.getWindow();
         WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
         DisplayMetrics displayMetrics = new DisplayMetrics();
         MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         int height = displayMetrics.heightPixels;
         int width = displayMetrics.widthPixels;
         layoutParams.height = (int) (height * 0.6);
         layoutParams.width  = (int) (width * 0.9);
         dialogWindow.setAttributes(layoutParams);

         final EditText edtGroupName = dialog.findViewById(R.id.edt_group_name);
         final ImageView imgGroupIcon = dialog.findViewById(R.id.img_group_icon);

         imgGroupIcon.setTag(R.drawable.icons8_tasklist_48);

         Button btnDone = dialog.findViewById(R.id.btn_done);
         Button btnCancel = dialog.findViewById(R.id.btn_cancel);

         btnDone.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String groupName = edtGroupName.getText().toString().trim();
                 int groupIconIndex = (int) imgGroupIcon.getTag();
                 if (groupName.length() <= 0){
                     Toast.makeText(MainActivity.this, "Empty name!", Toast.LENGTH_SHORT).show();
                 }else{
                     String insertGroupTask = "INSERT INTO groupTask VALUES(NULL, '"+ groupName +"',"+ groupIconIndex +")";
                     database.queryData(insertGroupTask);
                     updateListViewGroupTask();
                     dialog.dismiss();
                 }
             }
         });

         btnCancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog.cancel();
             }
         });

         dialog.show();
    }
    void dialogEditGroupTask(final GroupTask groupTask) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_group_task);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Edit Group Task");

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        layoutParams.height = (int) (height * 0.6);
        layoutParams.width  = (int) (width * 0.9);
        dialogWindow.setAttributes(layoutParams);

        final EditText edtGroupName = dialog.findViewById(R.id.edt_group_name);
        final ImageView imgGroupIcon = dialog.findViewById(R.id.img_group_icon);
        edtGroupName.setText(groupTask.getName());

        imgGroupIcon.setTag(R.drawable.icons8_tasklist_48);

        Button btnDone = dialog.findViewById(R.id.btn_done);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = edtGroupName.getText().toString().trim();
                int groupIconIndex = (int) imgGroupIcon.getTag();
                if (groupName.length() <= 0){
                    Toast.makeText(MainActivity.this, "Empty name!", Toast.LENGTH_SHORT).show();
                }else{
                    String insertGroupTask = "UPDATE groupTask SET groupTask_name = '"+groupName+ "', groupTask_iconIndex = "+groupIconIndex+" WHERE groupTask_id ="+groupTask.getId();
                    database.queryData(insertGroupTask);
                    updateListViewGroupTask();
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void updateListViewGroupTask(){
        arrGroupTask = new ArrayList<>();
        Cursor dataGroupTask = database.getData("SELECT * FROM groupTask");
        while (dataGroupTask.moveToNext()){
            int groupID = dataGroupTask.getInt(0);
            String groupName = dataGroupTask.getString(1);
            int iconIndex = dataGroupTask.getInt(2);
            GroupTask groupTask = new GroupTask(groupID, groupName, iconIndex, new ArrayList<Task>());
            Cursor dataTask = database.getData("SELECT * FROM task WHERE groupTask_id = "+ groupID);
            while (dataTask.moveToNext()) {
                int id = dataTask.getInt(0);
                String name = dataTask.getString(1);
                String describe = dataTask.getString(2);
                int day = dataTask.getInt(3);
                int month = dataTask.getInt(4);
                int year = dataTask.getInt(5);
                int hour = dataTask.getInt(6);
                int minute = dataTask.getInt(7);
                int repeat = dataTask.getInt(8);
                boolean notify = (dataTask.getInt(9) == 1);

                Task task = new Task(id, name, describe, day, month, year, hour, minute, repeat, notify);
                groupTask.getArrTask().add(task);
            }
            arrGroupTask.add(groupTask);
        }
        setTaskReminderAlarmManager();
        groupTaskAdapter = new GroupTaskAdapter(this, R.layout.item_group_task, arrGroupTask);
        lvGroupTask.setAdapter(groupTaskAdapter);
    }
    void setTaskReminderAlarmManager(){
        for (int i =0; i < arrGroupTask.size(); i++){
            for (int j = 0; j < arrGroupTask.get(i).getArrTask().size(); j++){
                Task task = arrGroupTask.get(i).getArrTask().get(j);
                if (task.isNotification()){
                    setNotification(task);
                }
            }
        }
    }

    void setNotification(Task task){
        Intent notifyIntent = new Intent(this, ReminderReceiver.class);
        int id = task.getId();
        String name = task.getName();
        String describe = task.getDescribe();
        String date = task.getDateYearMonth();
        String time = task.getTime24Hour();

        notifyIntent.putExtra("task_id", id);
        notifyIntent.putExtra("task_name", name);
        notifyIntent.putExtra("task_describe", describe);
        notifyIntent.putExtra("task_date", date);
        notifyIntent.putExtra("task_time", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        if (task.getRepeat() == 0){
            alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), pendingIntent);
            startService(notifyIntent);
        }else{
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), calculateTimeRepeat(task), pendingIntent);
            startService(notifyIntent);
        }
    }

    long getTimeInMillis(Task task){
        Calendar calendar = Calendar.getInstance();
        calendar.set(task.getYear(), task.getMonth() - 1, task.getDay(), task.getHour(), task.getMinute(), 0);
        return  calendar.getTimeInMillis();
    }
    long calculateTimeRepeat(Task task){
        final long TIME_OF_DAY = 24*60*60*1000;
        switch (task.getRepeat()){
            case 1: return TIME_OF_DAY;
            case 2: return TIME_OF_DAY * 7;
            default: return 0;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListViewGroupTask();
    }

    public void showPopupMenu(Context context, View view, final int i){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_group_task, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_delete_group:
                        String sql = "DELETE FROM groupTask WHERE groupTask_id ="+ arrGroupTask.get(i).getId();
                        database.queryData(sql);
                        updateListViewGroupTask();
                        return true;
                    case R.id.menu_edit_group:
                        dialogEditGroupTask(arrGroupTask.get(i));
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
