package com.example.damdinhdinh.taskreminder.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY id")
    LiveData<List<Task>> getAllTask();

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<Task> getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE name = :name")
    LiveData<Task> getTaskByName(String name);

    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}
