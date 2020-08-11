package com.example.damdinhdinh.taskreminder.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.damdinhdinh.taskreminder.model.GroupTask;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.List;

@Dao
public interface GroupTaskDao {

    @Query("SELECT * FROM group_tasks ORDER BY id")
    LiveData<List<GroupTask>> getAllGroupTask();

    @Query("SELECT * FROM group_tasks WHERE name = :name")
    LiveData<GroupTask> getGroupTaskByName(String name);

    @Query("SELECT * FROM group_tasks WHERE id = :id")
    LiveData<GroupTask> getGroupTaskById(int id);

    @Query("SELECT * FROM tasks WHERE groupTaskId = :groupTaskId")
    LiveData<List<Task>> getAllTaskByGroupTaskId(int groupTaskId);

    @Insert
    void insertGroupTask(GroupTask groupTask);

    @Update
    void updateGroupTask(GroupTask groupTask);

    @Delete
    void deleteGroupTask(GroupTask groupTask);
}
