package com.example.damdinhdinh.taskreminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.damdinhdinh.taskreminder.adapter.GroupTaskAdapter;
import com.example.damdinhdinh.taskreminder.adapter.TaskAdapter;
import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.GroupTask;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        String createTable = "CREATE TABLE IF NOT EXISTS task(task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_name VARCHAR(200), task_describe VARCHAR (200), task_day INTEGER(2), task_month INTEGER(2), " +
                "task_year INTEGER, task_hour INTEGER(2), task_minute INTEGER(2), task_repeat INTEGER(1), " +
                "task_notify INTEGER(1), groupTask_id INTEGER, FOREIGN KEY(groupTask_id) REFERENCES groupTask(groupTask_id))";
        database.queryData(createTable);
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

            Task task = new Task(id, name, describe, day, month, year, hour, minute, repeat, notify);
            arrTask.add(task);
        }
        taskAdapter = new TaskAdapter(this, R.layout.item_task, arrTask);
        lvReminder.setAdapter(taskAdapter);
        arrRepeat = new ArrayList<>();
        arrRepeat.add("Does not repeat");
        arrRepeat.add("Every day");
        arrRepeat.add("Every week");
        arrRepeat.add("Week day");
        arrRepeat.add("Every month");
        arrRepeat.add("Every year");

        tvNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCreateTask();
            }
        });



    }

    void dialogCreateTask() {
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
        layoutParams.height = (int) (height * 0.9);
        layoutParams.width  = (int) (width);
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

                    String insertGroupTask = "INSERT INTO task VALUES(NULL, '"+ name +"', '"+ describe +"', "+ day +","+ month +
                    ", "+ year +", " +hour+ ", "+ minute +", "+ repeatType +", "+ intNotify +", "+ groupTaskID +")";
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

    void updateListViewGroupTask() {
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

            Task task = new Task(id, name, describe, day, month, year, hour, minute, repeat, notify);
            arrTask.add(task);
        }
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
        dialog.setContentView(R.layout.dia_log_repeat_type);
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
}
