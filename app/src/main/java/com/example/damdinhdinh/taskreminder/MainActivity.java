package com.example.damdinhdinh.taskreminder;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damdinhdinh.taskreminder.adapter.GroupTaskAdapter;
import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.GroupTask;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView imgNewGroupTask;
    private TextView tvNewGroupTask;
    private ListView lvGroupTask;
    private DatabaseSQLite database;
    private ArrayList<GroupTask> arrGroupTask;
    private GroupTaskAdapter groupTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgNewGroupTask = findViewById(R.id.img_icon_add_new_group);
        tvNewGroupTask = findViewById(R.id.tv_add_new_group);
        lvGroupTask = findViewById(R.id.lv_group_task);

        arrGroupTask = new ArrayList<>();

        database = new DatabaseSQLite(MainActivity.this, "task.sqlite", null, 1);
        String createTable = "CREATE TABLE IF NOT EXISTS groupTask(groupTask_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "groupTask_name VARCHAR(200), groupTask_iconIndex INTEGER)";
        database.queryData(createTable);
        Cursor dataGroupTask = database.getData("SELECT * FROM groupTask");
        while (dataGroupTask.moveToNext()){
            int groupID = dataGroupTask.getInt(0);
            String groupName = dataGroupTask.getString(1);
            int iconIndex = dataGroupTask.getInt(2);
            GroupTask groupTask = new GroupTask(groupID, groupName, iconIndex, new ArrayList<Integer>());
            Cursor dataTask = database.getData("SELECT task_id FROM task WHERE groupTask_id = "+ groupID);
            while (dataTask.moveToNext()) {
                int id = dataTask.getInt(0);
                groupTask.getArrTask().add(id);
            }
            arrGroupTask.add(groupTask);
        }
        groupTaskAdapter = new GroupTaskAdapter(this, R.layout.item_group_task, arrGroupTask);
        lvGroupTask.setAdapter(groupTaskAdapter);

        tvNewGroupTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCreateGroupTask();
            }
        });
        lvGroupTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentListkTaskActivity = new Intent(MainActivity.this, ListTaskActivity.class);
                intentListkTaskActivity.putExtra("groupTask_id", arrGroupTask.get(i).getId());
                startActivity(intentListkTaskActivity);
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

         final EditText edtnGroupName = dialog.findViewById(R.id.edt_group_name);
         final ImageView imgGroupIcon = dialog.findViewById(R.id.img_group_icon);

         imgGroupIcon.setTag(R.drawable.icons8_tasklist_48);

         Button btnDone = dialog.findViewById(R.id.btn_done);
         Button btnCancel = dialog.findViewById(R.id.btn_cancel);

         btnDone.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String groupName = edtnGroupName.getText().toString().trim();
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

    void updateListViewGroupTask(){
        arrGroupTask = new ArrayList<>();
        Cursor dataGroupTask = database.getData("SELECT * FROM groupTask");
        while (dataGroupTask.moveToNext()){
            int groupID = dataGroupTask.getInt(0);
            String groupName = dataGroupTask.getString(1);
            int iconIndex = dataGroupTask.getInt(2);
            GroupTask groupTask = new GroupTask(groupID, groupName, iconIndex, new ArrayList<Integer>());
            Cursor dataTask = database.getData("SELECT task_id FROM task WHERE groupTask_id = "+ groupID);
            while (dataTask.moveToNext()) {
                int id = dataTask.getInt(0);
                groupTask.getArrTask().add(id);
            }
            arrGroupTask.add(groupTask);
        }
        groupTaskAdapter = new GroupTaskAdapter(this, R.layout.item_group_task, arrGroupTask);
        lvGroupTask.setAdapter(groupTaskAdapter);
    }

}
