package com.example.damdinhdinh.taskreminder.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.damdinhdinh.taskreminder.model.GroupTask;
import com.example.damdinhdinh.taskreminder.model.Task;

@Database(entities = {Task.class, GroupTask.class}, version = 1, exportSchema = false)
public abstract class TaskRoomDatabase extends RoomDatabase {

    private static final String DB_NAME = "task_database";

    private static TaskRoomDatabase INSTANCE;

    public static TaskRoomDatabase getDatabase(Context context){
        if (INSTANCE == null){
            synchronized (TaskRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context, TaskRoomDatabase.class, DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TaskDao taskDao();

    public abstract GroupTaskDao groupTaskDao();
}
