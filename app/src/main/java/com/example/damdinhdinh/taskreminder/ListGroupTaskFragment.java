package com.example.damdinhdinh.taskreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListGroupTaskFragment extends Fragment implements ShowPopup{

    private ImageView imgNewGroupTask;
    private TextView tvNewGroupTask;
    private ListView lvGroupTask;
    private DatabaseSQLite database;
    private ArrayList<GroupTask> arrGroupTask;
    private GroupTaskAdapter groupTaskAdapter;
    private AlarmManager alarmManager;
    final int RECORD_REQUEST_CODE = 5;
    private Context mContext;

    public ListGroupTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        database = new DatabaseSQLite(context, "task.sqlite", null, 1);
//        String sql = "DROP TABLE groupTask";
//        String sql1 = "DROP TABLE task";
        String createTableGroupTask = "CREATE TABLE IF NOT EXISTS groupTask(groupTask_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "groupTask_name VARCHAR(200), groupTask_iconIndex INTEGER)";
        database.queryData(createTableGroupTask);
        String createTableTask = "CREATE TABLE IF NOT EXISTS task(task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_name VARCHAR(200), task_describe VARCHAR (200), task_day INTEGER(2), task_month INTEGER(2), " +
                "task_year INTEGER, task_hour INTEGER(2), task_minute INTEGER(2), task_repeat INTEGER(1), " +
                "task_notify INTEGER(1), groupTask_id INTEGER, task_is_set INTEGER(1), FOREIGN KEY(groupTask_id) REFERENCES groupTask(groupTask_id))";
        database.queryData(createTableTask);
//        database.queryData(sql);
//        database.queryData(sql1);


        SharedPreferences sharedPre = context.getSharedPreferences("setting", 0);

        if (sharedPre.getBoolean("first_time_open_app", true)) {
            String sql = "INSERT INTO groupTask VALUES(NULL, 'Today'," + 0 + ")";
            database.queryData(sql);
            sql = "INSERT INTO groupTask VALUES(NULL, 'Exercise'," + 0 + ")";
            database.queryData(sql);
            sql = "INSERT INTO groupTask VALUES(NULL, 'Work'," + 0 + ")";
            database.queryData(sql);
            sharedPre.edit().putBoolean("first_time_open_app", false).commit();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_group_task, container, false);

        imgNewGroupTask = view.findViewById(R.id.img_icon_add_new_group);
        tvNewGroupTask = view.findViewById(R.id.tv_add_new_group);
        lvGroupTask = view.findViewById(R.id.lv_group_task);

        arrGroupTask = new ArrayList<>();

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
                Bundle bundle = new Bundle();
                bundle.putInt("groupTask_id", arrGroupTask.get(i).getId());
                Navigation.findNavController(view).navigate(R.id.action_listGroupTaskFragment_to_listTaskFragment, bundle);
            }
        });

        lvGroupTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_group_task_item, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_delete_group:
                                String sql = "DELETE FROM groupTask WHERE groupTask_id =" + arrGroupTask.get(i).getId();
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

        return view;
    }

    void dialogCreateGroupTask() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_create_group_task);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Create Group Task");

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
//         layoutParams.height = (int) (height * 0.6);
//         layoutParams.width  = (int) (width * 0.9);
        dialogWindow.setAttributes(layoutParams);

        final EditText edtGroupName = dialog.findViewById(R.id.edt_group_name);
        Button btnDone = dialog.findViewById(R.id.btn_done);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = edtGroupName.getText().toString().trim();
                if (groupName.length() <= 0) {
                    Toast.makeText(mContext, "Empty name!", Toast.LENGTH_SHORT).show();
                } else {
                    String insertGroupTask = "INSERT INTO groupTask VALUES(NULL, '" + groupName + "'," + 0 + ")";
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
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_create_group_task);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Edit Group Task");

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
//        layoutParams.height = (int) (height * 0.6);
//        layoutParams.width  = (int) (width * 0.9);
        dialogWindow.setAttributes(layoutParams);

        final EditText edtGroupName = dialog.findViewById(R.id.edt_group_name);
        edtGroupName.setText(groupTask.getName());


        Button btnDone = dialog.findViewById(R.id.btn_done);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = edtGroupName.getText().toString().trim();
                if (groupName.length() <= 0) {
                    Toast.makeText(mContext, "Empty name!", Toast.LENGTH_SHORT).show();
                } else {
                    String insertGroupTask = "UPDATE groupTask SET groupTask_name = '" + groupName + "', groupTask_iconIndex = " + 0 + " WHERE groupTask_id =" + groupTask.getId();
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

    public void updateListViewGroupTask() {
        arrGroupTask = new ArrayList<>();
        Cursor dataGroupTask = database.getData("SELECT * FROM groupTask");
        while (dataGroupTask.moveToNext()) {
            int groupID = dataGroupTask.getInt(0);
            String groupName = dataGroupTask.getString(1);
            int iconIndex = dataGroupTask.getInt(2);
            GroupTask groupTask = new GroupTask(groupID, groupName, iconIndex, new ArrayList<Task>());
            Cursor dataTask = database.getData("SELECT * FROM task WHERE groupTask_id = " + groupID);
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
                groupTask.getArrTask().add(task);
            }
            arrGroupTask.add(groupTask);
        }
        //setTaskReminderAlarmManager();
        groupTaskAdapter = new GroupTaskAdapter(mContext, R.layout.item_group_task, arrGroupTask, this);
        lvGroupTask.setAdapter(groupTaskAdapter);
    }

    void setTaskReminderAlarmManager() {
        for (int i = 0; i < arrGroupTask.size(); i++) {
            for (int j = 0; j < arrGroupTask.get(i).getArrTask().size(); j++) {
                Task task = arrGroupTask.get(i).getArrTask().get(j);
                if (task.isNotification()) {
                    setNotification(task);
                }
            }
        }
    }

    void setNotification(Task task) {
        Intent notifyIntent = new Intent(mContext, ReminderReceiver.class);
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

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        if (task.getRepeat() == 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), pendingIntent);
            mContext.startService(notifyIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), calculateTimeRepeat(task), pendingIntent);
            mContext.startService(notifyIntent);
        }
    }

    long getTimeInMillis(Task task) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(task.getYear(), task.getMonth() - 1, task.getDay(), task.getHour(), task.getMinute(), 0);
        return calendar.getTimeInMillis();
    }

    long calculateTimeRepeat(Task task) {
        final long TIME_OF_DAY = 24 * 60 * 60 * 1000;
        switch (task.getRepeat()) {
            case 1:
                return TIME_OF_DAY;
            case 2:
                return TIME_OF_DAY * 7;
            default:
                return 0;
        }
    }

    @Override
    public void showPopupMenu(Context context, View view, final int i) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_group_task_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_delete_group:
                        String sql = "DELETE FROM groupTask WHERE groupTask_id =" + arrGroupTask.get(i).getId();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_export_data:{
                exportData();
                return true;
            }
            case R.id.menu_import_data:{
                importData();
                return true;
            }

        }
        return false;
    }

    public void exportData() {
        checkPermissionForReadExternalStorage();
        final String APP_DATA_FILE = "taskreminderdata.txt";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path, "/" + APP_DATA_FILE);
        try {
            Toast.makeText(mContext, "Exporting data", Toast.LENGTH_SHORT).show();
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
//            pw.println(arrGroupTask.size());
//            for (int i = 0; i < arrGroupTask.size(); i++) {
//                String groupData = arrGroupTask.get(i).getId() + " " + arrGroupTask.get(i).getName() + " " + arrGroupTask.get(i).getArrTask().size();
//                pw.println(groupData);
//                for (int j = 0; j < arrGroupTask.get(i).getArrTask().size(); j++) {
//                    Task task = arrGroupTask.get(i).getArrTask().get(j);
//                    String taskData = task.getId() + " " + task.getName() + " " + task.getDescribe() + " " + task.getDay() + " " + task.getMonth() + " " + task.getYear() + " " + task.getHour() + " " + task.getMinute() + " " + task.getRepeat() + " " + task.isNotification() + " " + task.getGroupTaskId() + " " + task.isSet();
//                    pw.println(taskData);
//                }
//            }
            Cursor dataGroupTask = database.getData("SELECT * FROM groupTask");
            int count = 0;
            while (dataGroupTask.moveToNext()) {
                int groupID = dataGroupTask.getInt(0);
                String groupName = dataGroupTask.getString(1);
                int iconIndex = dataGroupTask.getInt(2);
                String groupData = groupID+"---"+groupName+"---"+iconIndex+"---"+arrGroupTask.get(count).getArrTask().size();
                count++;
                pw.println(groupData);
                Cursor dataTask = database.getData("SELECT * FROM task WHERE groupTask_id = " + groupID);
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
                    int notify = dataTask.getInt(9);
                    int groupTaskId = dataTask.getInt(10);
                    int isSet = dataTask.getInt(11);
                    String taskData = id+"---"+name+"---"+describe+"---"+day+"---"+month+"---"+year+"---"+hour+"---"+minute+"---"+repeat+"---"+notify+"---"+groupTaskId+"---"+isSet;
                    pw.println(taskData);
                }
            }

            pw.flush();
            pw.close();
            f.close();
            Toast.makeText(mContext, "Export data finished", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("ASD", " File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void checkPermissionForReadExternalStorage() {

        int permission = ContextCompat.checkSelfPermission(mContext
                ,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("SAS", "Permission to read external denied");
            makeRequest();
        }

    }

    public void makeRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                RECORD_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i("SAS", "Permission has been denied by user");
                } else {
                    Log.i("SAS", "Permission has been granted by user");
                }
                return;
            }
        }
    }

    public void importData(){
        checkPermissionForReadExternalStorage();

        final String APP_DATA_FILE = "taskreminderdata.txt";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path, "/" + APP_DATA_FILE);

        try {
            Toast.makeText(mContext, "Importing data", Toast.LENGTH_SHORT).show();
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = reader.readLine();
            while (line != null){
//                Toast.makeText(this, line+"####", Toast.LENGTH_SHORT).show();
                String lineSplit[] = line.split("---");
                int groupId = Integer.parseInt(lineSplit[0]);
                String groupName = lineSplit[1];
                int iconId  = Integer.parseInt(lineSplit[2]);
                int arrTaskSize = Integer.parseInt(lineSplit[3]);
//                Toast.makeText(this, groupId+" "+groupName+" "+iconId, Toast.LENGTH_SHORT).show();
                Cursor check = database.getData("SELECT * FROM groupTask WHERE groupTask_name='"+groupName+"'");
                if(!check.moveToNext()){
                    String sql = "INSERT INTO groupTask VALUES(NULL, '" + groupName + "'," + iconId + ")";
                    database.queryData(sql);
                }
                if(arrTaskSize > 0){
                    for(int i = 0; i < arrTaskSize; i++){
                        line = reader.readLine();
                        String lineSplitTask[] = line.split("---");
                        int taskId = Integer.parseInt(lineSplitTask[0]);
                        String taskName = lineSplitTask[1];
                        String describe = lineSplitTask[2];
                        int day = Integer.parseInt(lineSplitTask[3]);
                        int month = Integer.parseInt(lineSplitTask[4]);
                        int year = Integer.parseInt(lineSplitTask[5]);
                        int hour = Integer.parseInt(lineSplitTask[6]);
                        int minute = Integer.parseInt(lineSplitTask[7]);
                        int repeat = Integer.parseInt(lineSplitTask[8]);
                        int notify = Integer.parseInt(lineSplitTask[9]);
                        int groupTaskId = Integer.parseInt(lineSplitTask[10]);
                        int isSet = Integer.parseInt(lineSplitTask[11]);

                        Cursor checkTask = database.getData("SELECT * FROM task WHERE task_id="+taskId+" AND task_name ='"+taskName+"'");
//                        Cursor groupTask = database.getData("SELECT * FROM groupTask ORDER BY groupTask_id DESC LIMIT 1");
                        if(!checkTask.moveToNext()){
                            String sql = "INSERT INTO task VALUES(NULL, '"+ taskName +"', '"+ describe +"', "+ day +","+ month +
                                    ", "+ year +", " +hour+ ", "+ minute +", "+ repeat +", "+ notify +", "+ groupTaskId +", "+isSet+")";
                            database.queryData(sql);
                        }
                    }
                    line = reader.readLine();
                }else {
                    line = reader.readLine();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateListViewGroupTask();
    }

}
