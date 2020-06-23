package com.example.damdinhdinh.taskreminder.database;

import androidx.lifecycle.LiveData;

import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.List;

public interface TaskDataSource {
    LiveData<List<Task>> getAllTask();

    LiveData<Task> getTaskById(int id);

    void insertTask(Task task);

    void updateTask(Task task);

    void deleteTask(Task task);
}
