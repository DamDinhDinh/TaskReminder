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
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.damdinhdinh.taskreminder.database.TaskRepository;
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
import java.util.Objects;

public class ListGroupTaskActivity extends AppCompatActivity implements ShowPopup{
  
  private static final String TAG = "ListGroupTaskActivity";
  
  private ImageView imgNewGroupTask;
  private TextView tvNewGroupTask;
  private ListView lvGroupTask;
  private DatabaseSQLite database;
  private ArrayList<GroupTask> arrGroupTask;
  private GroupTaskAdapter groupTaskAdapter;
  private AlarmManager alarmManager;
  final int RECORD_REQUEST_CODE = 5;

  private TaskRepository mAppRepo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_group_task);

    imgNewGroupTask = findViewById(R.id.img_icon_add_new_group);
    tvNewGroupTask = findViewById(R.id.tv_add_new_group);
    lvGroupTask = findViewById(R.id.lv_group_task);

    mAppRepo = new TaskRepository(getApplication());
    arrGroupTask = new ArrayList<>();

    SharedPreferences sharedPre = getSharedPreferences("setting", 0);

    if (sharedPre.getBoolean("first_time_open_app", true)) {
      Log.d(TAG, "onCreate: first_time_open_app");
      GroupTask defaultToday = new GroupTask("Today", 0);
      GroupTask defaultExercise = new GroupTask("Exercise", 0);
      GroupTask defaultWork = new GroupTask("Work", 0);

      mAppRepo.insertGroupTask(defaultToday);
      mAppRepo.insertGroupTask(defaultExercise);
      mAppRepo.insertGroupTask(defaultWork);

      sharedPre.edit().putBoolean("first_time_open_app", false).apply();
    }

    updateListViewGroupTask();

    tvNewGroupTask.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialogCreateGroupTask();
      }
    });
    
    lvGroupTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d(TAG, "onItemClick: intentListTaskActivity groupTaskId"+arrGroupTask.get(position).getId());
        Intent intentListTaskActivity = new Intent(ListGroupTaskActivity.this, ListTaskActivity.class);
        intentListTaskActivity.putExtra("groupTask_id", arrGroupTask.get(position).getId());
        startActivity(intentListTaskActivity);
      }
    });
    
    lvGroupTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
        PopupMenu popupMenu = new PopupMenu(ListGroupTaskActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_group_task_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
              case R.id.menu_delete_group:
                Log.d(TAG, "onMenuItemClick: delete grouptask"+position);
                mAppRepo.deleteGroupTask(arrGroupTask.get(position));
                updateListViewGroupTask();
                return true;
              case R.id.menu_edit_group:
                Log.d(TAG, "onMenuItemClick: edit grouptask"+position);
                dialogEditGroupTask(arrGroupTask.get(position));
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
    Log.d(TAG, "dialogCreateGroupTask: create");
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_create_group_task);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setTitle("Create Group Task");

    Window dialogWindow = dialog.getWindow();
    WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ListGroupTaskActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
          Toast.makeText(ListGroupTaskActivity.this, "Empty name!", Toast.LENGTH_SHORT).show();
        } else {
          Log.d(TAG, "onClick: create grouptask "+groupName);
          GroupTask newGroupTask = new GroupTask(groupName, 0);
          mAppRepo.insertGroupTask(newGroupTask);
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

  void dialogEditGroupTask(final GroupTask editGroupTask) {
    Log.d(TAG, "dialogEditGroupTask: create");
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_create_group_task);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setTitle("Edit Group Task");

    Window dialogWindow = dialog.getWindow();
    WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ListGroupTaskActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    int height = displayMetrics.heightPixels;
    int width = displayMetrics.widthPixels;
//        layoutParams.height = (int) (height * 0.6);
//        layoutParams.width  = (int) (width * 0.9);
    dialogWindow.setAttributes(layoutParams);

    final EditText edtGroupName = dialog.findViewById(R.id.edt_group_name);
    edtGroupName.setText(editGroupTask.getName());


    Button btnDone = dialog.findViewById(R.id.btn_done);
    Button btnCancel = dialog.findViewById(R.id.btn_cancel);

    btnDone.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String newName = edtGroupName.getText().toString().trim();
        if (newName.length() <= 0) {
          Toast.makeText(ListGroupTaskActivity.this, "Empty name!", Toast.LENGTH_SHORT).show();
        } else {
          Log.d(TAG, "onClick: update grouptask name "+newName);
          editGroupTask.setName(newName);
          mAppRepo.updateGroupTask(editGroupTask);
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
    Log.d(TAG, "updateListViewGroupTask: ");
    arrGroupTask.clear();
    arrGroupTask.addAll(Objects.requireNonNull(mAppRepo.getAllGroupTask().getValue()));
    Log.d(TAG, "updateListViewGroupTask: size "+arrGroupTask.size());

    groupTaskAdapter = new GroupTaskAdapter(this, R.layout.item_group_task, arrGroupTask, this);
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
    if (task.getRepeat() == 0) {
      alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), pendingIntent);
      startService(notifyIntent);
    } else {
      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(task), calculateTimeRepeat(task), pendingIntent);
      startService(notifyIntent);
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
  protected void onStart() {
    super.onStart();
    updateListViewGroupTask();
  }

  public void showPopupMenu(Context context, View view, final int position) {
    PopupMenu popupMenu = new PopupMenu(context, view);
    popupMenu.getMenuInflater().inflate(R.menu.menu_group_task_item, popupMenu.getMenu());
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.menu_delete_group:
            mAppRepo.deleteGroupTask(arrGroupTask.get(position));
            updateListViewGroupTask();
            return true;
          case R.id.menu_edit_group:
            dialogEditGroupTask(arrGroupTask.get(position));
            return true;
        }
        return false;
      }
    });
    popupMenu.show();
  }

  public void showActivityMenu(Context context, View view) {
    PopupMenu popupMenu = new PopupMenu(context, view);
    popupMenu.getMenuInflater().inflate(R.menu.menu_activity, popupMenu.getMenu());
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {

        return false;
      }
    });
    popupMenu.show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_activity, menu);
    return true;
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
    Log.d(TAG, "exportData: start");
    checkPermissionForReadExternalStorage();
    final String APP_DATA_FILE = "taskreminderdata.txt";
    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    File file = new File(path, "/" + APP_DATA_FILE);
    try {
      Toast.makeText(this, "Exporting data", Toast.LENGTH_SHORT).show();
      FileOutputStream f = new FileOutputStream(file);
      PrintWriter pw = new PrintWriter(f);

      for (GroupTask exportGroupTask : arrGroupTask) {
        String exportGroupLine = exportGroupTask.getId()
            +"---"+exportGroupTask.getName()
            +"---"+exportGroupTask.getIcon()
            +"---"+exportGroupTask.getArrTask().size();
        pw.println(exportGroupLine);

        ArrayList<Task> listTaskBelongTo = (ArrayList<Task>) mAppRepo.getAllTaskByGroupTaskId(exportGroupTask.getId()).getValue();
        for (Task exportTask : listTaskBelongTo) {
          String exportTaskLine = exportTask.getId()
              +"---"+exportTask.getName()
              +"---"+exportTask.getDescribe()
              +"---"+exportTask.getDay()
              +"---"+exportTask.getMonth()
              +"---"+exportTask.getYear()
              +"---"+exportTask.getHour()
              +"---"+exportTask.getMinute()
              +"---"+exportTask.getRepeat()
              +"---"+(exportTask.isNotification() ? 1 : 0)
              +"---"+exportTask.getGroupTaskId()
              +"---"+(exportTask.isSet() ? 1 : 0);
          pw.println(exportTaskLine);
        }
      }

      pw.flush();
      pw.close();
      f.close();
      Toast.makeText(this, "Export data finished", Toast.LENGTH_SHORT).show();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      Log.i("ASD", " File not found");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void checkPermissionForReadExternalStorage() {

    int permission = ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      Log.i("SAS", "Permission to read external denied");
      makeRequest();
    }

  }
  public void makeRequest() {
    ActivityCompat.requestPermissions(this,
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
    Log.d(TAG, "importData: start");
    checkPermissionForReadExternalStorage();

    final String APP_DATA_FILE = "taskreminderdata.txt";
    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    File file = new File(path, "/" + APP_DATA_FILE);

    try {
      Toast.makeText(this, "Importing data", Toast.LENGTH_SHORT).show();
      FileInputStream fileInputStream = new FileInputStream(file);
      BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
      String line = reader.readLine();
      while (line != null){
        String lineSplit[] = line.split("---");
        int groupId = Integer.parseInt(lineSplit[0]);
        String groupName = lineSplit[1];
        int iconId  = Integer.parseInt(lineSplit[2]);
        int arrTaskSize = Integer.parseInt(lineSplit[3]);
        Log.d(TAG, "importData: id "+groupId+" name "+groupName+" icon "+iconId);
        
        GroupTask existedGroup = mAppRepo.getGroupTaskByName(groupName).getValue();
        if(existedGroup == null){
          existedGroup = new GroupTask(groupName, iconId);
          mAppRepo.insertGroupTask(existedGroup);
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
            boolean notify = Integer.parseInt(lineSplitTask[9]) == 1;
            int groupTaskId = Integer.parseInt(lineSplitTask[10]);
            boolean isSet = Integer.parseInt(lineSplitTask[11]) == 1;
            
            Task existedTask = mAppRepo.getTaskByName(taskName).getValue();
            if(existedTask == null){
              existedTask = new Task(taskName, describe, day, month, year, hour, minute, repeat, notify, groupTaskId, isSet);
              mAppRepo.insertTask(existedTask);
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
