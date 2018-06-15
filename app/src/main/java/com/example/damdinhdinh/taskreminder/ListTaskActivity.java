package com.example.damdinhdinh.taskreminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.damdinhdinh.taskreminder.adapter.TaskAdapter;
import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListTaskActivity extends AppCompatActivity {
    private ImageView imgNewReminder;
    private TextView tvNewReminder;
    private ListView lvReminder;
    private ArrayList<Task> arrTask;
    private TaskAdapter taskAdapter;
    private int groupTaskID;
    private DatabaseSQLite database;
    private TextView tvTimeCreateDialog;
    private TextView tvDateCreateDialog;
    private TextView tvRepeatType;
    private ArrayList<String> arrRepeat;
    private int repeatType = 0;
    private static AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);

        imgNewReminder = findViewById(R.id.img_icon_add_new_reminder);
        tvNewReminder = findViewById(R.id.tv_add_new_reminder);
        lvReminder = findViewById(R.id.lv_reminder);

        arrTask = new ArrayList<>();
        Intent intent = getIntent();
        groupTaskID = intent.getIntExtra("groupTask_id", -1);

        database = new DatabaseSQLite(ListTaskActivity.this, "task.sqlite", null, 1);
        Cursor dataTask = database.getData("SELECT * FROM task WHERE groupTask_id = "+groupTaskID);
        while (dataTask.moveToNext()){
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
            int groupTaskId = dataTask.getInt(10);
            boolean isSet = (dataTask.getInt(11) == 1);
            Task task = new Task(id, name, describe, day, month, year, hour, minute, repeat, notify, isSet, groupTaskId);
            Log.d("AAAAA",task.getName()+" "+task.getId()+" "+task.isSet());
            arrTask.add(task);
        }
        taskAdapter = new TaskAdapter(this, R.layout.item_task, arrTask);
        lvReminder.setAdapter(taskAdapter);
        arrRepeat = new ArrayList<>();
        arrRepeat.add("Does not repeat");
        arrRepeat.add("Every day");
        arrRepeat.add("Every week");
//        arrRepeat.add("Week day");
//        arrRepeat.add("Every month");
//        arrRepeat.add("Every year");

        tvNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCreateTask();
            }
        });



    }

    public void dialogCreateTask() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_task);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Create Task");

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ListTaskActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
//        layoutParams.height = (int) (height * 0.9);
//        layoutParams.width  = (int) (width * 0.9);
        dialogWindow.setAttributes(layoutParams);

        final EditText edtName = dialog.findViewById(R.id.edt_set_reminder_name);
        final EditText edtDescribe = dialog.findViewById(R.id.edt_set_reminder_describe);
        tvTimeCreateDialog = dialog.findViewById(R.id.edt_set_time_reminder);
        tvDateCreateDialog = dialog.findViewById(R.id.edt_set_date_reminder);
        tvRepeatType = dialog.findViewById(R.id.edt_set_repeat_type_reminder);
        final CheckBox cbNotify = dialog.findViewById(R.id.cb_notify);

        Button btnDone = dialog.findViewById(R.id.btn_done_create_reminder);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_create_reminder);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String describe = edtDescribe.getText().toString().trim();
                String time = tvTimeCreateDialog.getText().toString().trim();
                String date = tvDateCreateDialog.getText().toString().trim();
                boolean notify = cbNotify.isChecked();
                if (name.length() <= 0){
                    Toast.makeText(ListTaskActivity.this, "Empty name!", Toast.LENGTH_SHORT).show();
                }else{
                    int hour;
                    int minute;
                    int day;
                    int month;
                    int year;
                    int intNotify = (notify == true)? 1:0;
                    if (!time.equals("Enter time")){
                        String arr[] = time.split(":");
                        hour = Integer.parseInt(arr[0]);
                        minute = Integer.parseInt(arr[1]);
                    }else{
                        hour = 0;
                        minute = 0;
                    }
                    if (!date.equals("Enter date")){
                        String arr[] = date.split("/");
                        day = Integer.parseInt(arr[0]);
                        month = Integer.parseInt(arr[1]);
                        year = Integer.parseInt(arr[2]);
                    }else{
                        day = 0;
                        month = 0;
                        year = 0;
                    }

                    String insertTask = "INSERT INTO task VALUES(NULL, '"+ name +"', '"+ describe +"', "+ day +","+ month +
                    ", "+ year +", " +hour+ ", "+ minute +", "+ repeatType +", "+ intNotify +", "+ groupTaskID +", 0)";
                    database.queryData(insertTask);
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

        tvTimeCreateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog();
            }
        });

        tvDateCreateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog();
            }
        });

        tvRepeatType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRepeatType();
            }
        });
        dialog.show();
    }
    public void dialogEditTask(final Task task) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_task);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Edit Task");

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ListTaskActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
//        layoutParams.height = (int) (height * 0.9);
//        layoutParams.width  = (int) (width);
        dialogWindow.setAttributes(layoutParams);

        final EditText edtName = dialog.findViewById(R.id.edt_set_reminder_name);
        final EditText edtDescribe = dialog.findViewById(R.id.edt_set_reminder_describe);
        tvTimeCreateDialog = dialog.findViewById(R.id.edt_set_time_reminder);
        tvDateCreateDialog = dialog.findViewById(R.id.edt_set_date_reminder);
        tvRepeatType = dialog.findViewById(R.id.edt_set_repeat_type_reminder);
        final CheckBox cbNotify = dialog.findViewById(R.id.cb_notify);

        edtName.setText(task.getName());
        edtDescribe.setText(task.getDescribe());
        tvTimeCreateDialog.setText(task.getHour()+":"+task.getMinute());
        tvDateCreateDialog.setText(task.getDay()+"/"+task.getMonth()+"/"+task.getYear());
        cbNotify.setChecked(task.isNotification());

        Button btnDone = dialog.findViewById(R.id.btn_done_create_reminder);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_create_reminder);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String describe = edtDescribe.getText().toString().trim();
                String time = tvTimeCreateDialog.getText().toString().trim();
                String date = tvDateCreateDialog.getText().toString().trim();
                boolean notify = cbNotify.isChecked();
                if (name.length() <= 0){
                    Toast.makeText(ListTaskActivity.this, "Empty name!", Toast.LENGTH_SHORT).show();
                }else{
                    int hour;
                    int minute;
                    int day;
                    int month;
                    int year;
                    int intNotify = (notify == true)? 1:0;
                    if (!time.equals("Enter time")){
                        String arr[] = time.split(":");
                        hour = Integer.parseInt(arr[0]);
                        minute = Integer.parseInt(arr[1]);
                    }else{
                        hour = 0;
                        minute = 0;
                    }
                    if (!date.equals("Enter date")){
                        String arr[] = date.split("/");
                        day = Integer.parseInt(arr[0]);
                        month = Integer.parseInt(arr[1]);
                        year = Integer.parseInt(arr[2]);
                    }else{
                        day = 0;
                        month = 0;
                        year = 0;
                    }
                    String updateTask = "UPDATE task SET task_name = '"+ name +"', task_describe = '"+ describe +"', task_day = "+ day +", task_month = "+month+", task_year = "+year+"," +
                            "task_hour = "+ hour+", task_minute = "+minute+", task_repeat = "+repeatType+", task_notify = "+intNotify+" WHERE task_id = "+task.getId();
                    database.queryData(updateTask);
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

        tvTimeCreateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog();
            }
        });

        tvDateCreateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog();
            }
        });

        tvRepeatType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRepeatType();
            }
        });
        dialog.show();
    }

    public void updateListViewGroupTask() {
        arrTask = new ArrayList<>();
        Cursor dataTask = database.getData("SELECT * FROM task WHERE groupTask_id = " + groupTaskID);
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
            int groupTaskId = dataTask.getInt(10);
            boolean isSet = (dataTask.getInt(11) == 1);
            Task task = new Task(id, name, describe, day, month, year, hour, minute, repeat, notify, isSet, groupTaskId);
            Log.d("AAAAA",task.isNotification()+" "+!task.isSet()+" "+isSet+dataTask.getInt(10)+dataTask.getInt(10));
            arrTask.add(task);
        }
        setTaskReminderAlarmManager();
        taskAdapter = new TaskAdapter(this, R.layout.item_task, arrTask);
        lvReminder.setAdapter(taskAdapter);
    }

    public void datePickerDialog(){
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                tvDateCreateDialog.setText(simpleDateFormat.format(calendar.getTime()));
            }
        },year, month, date);
        datePickerDialog.show();
    }

    void timePickerDialog(){
        final Calendar calendar = Calendar.getInstance();
        final int minute = calendar.get(Calendar.MINUTE);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(0, 0, 0, i, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                tvTimeCreateDialog.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    void dialogRepeatType(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_repeat_type);
        dialog.setTitle("Select repeat type");
        ListView lvRepeatType = dialog.findViewById(R.id.lvRepeatType);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrRepeat);
        lvRepeatType.setAdapter(arrayAdapter);

        lvRepeatType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                repeatType = i;
                tvRepeatType.setText(arrRepeat.get(i));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListViewGroupTask();
    }

    public void showPopupMenu(Context context, View view, final int i){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_group_task_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_delete_group:
                        String sql = "DELETE FROM task WHERE task_id ="+ arrTask.get(i).getId();
                        database.queryData(sql);
                        updateListViewGroupTask();
                        return true;
                    case R.id.menu_edit_group:
                        dialogEditTask(arrTask.get(i));
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    void setTaskReminderAlarmManager(){
        for (int i =0; i < arrTask.size(); i++){
                Task task = arrTask.get(i);
                //Log.d("AAAAA",task.isNotification()+" "+!task.isSet());
                if (task.isNotification() && !task.isSet()){
                    setNotification(task);
                    String updateTask = "UPDATE task SET  task_is_set=1 WHERE task_id = "+task.getId();
                    database.queryData(updateTask);
                    task.setSet(true);
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
        int repeatType = task.getRepeat();
        notifyIntent.putExtra("task_id", id);
        notifyIntent.putExtra("task_name", name);
        notifyIntent.putExtra("task_describe", describe);
        notifyIntent.putExtra("task_date", date);
        notifyIntent.putExtra("task_time", time);
        notifyIntent.putExtra("task_repeat", repeatType);
        notifyIntent.putExtra("group_task_id", task.getGroupTaskId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        if (task.getRepeat() == 0){
            alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), pendingIntent);
            startService(notifyIntent);
        }else{
            if (task.getRepeat() == 1){
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), AlarmManager.INTERVAL_DAY, pendingIntent);
                startService(notifyIntent);
            }
            if (task.getRepeat() == 2) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), 7 * AlarmManager.INTERVAL_DAY, pendingIntent);
                startService(notifyIntent);
            }
        }
    }

    long getTimeInMillis(Task task){
        Calendar calendar = Calendar.getInstance();
        calendar.set(task.getYear(), task.getMonth() - 1, task.getDay(), task.getHour(), task.getMinute(), 0);
        return  calendar.getTimeInMillis();
    }
}

