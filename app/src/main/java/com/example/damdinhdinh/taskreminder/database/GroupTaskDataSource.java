package com.example.damdinhdinh.taskreminder.database;

import androidx.lifecycle.LiveData;

import com.example.damdinhdinh.taskreminder.model.GroupTask;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.List;

public interface GroupTaskDataSource {

    LiveData<List<GroupTask>> getAllGroupTask();

    LiveData<GroupTask> getGroupTaskByName(String name);

    LiveData<GroupTask> getGroupTaskById(int id);

    LiveData<List<Task>> getAllTaskByGroupTaskId(int groupTaskId);

    void insertGroupTask(GroupTask groupTask);

    void updateGroupTask(GroupTask groupTask);

    void deleteGroupTask(GroupTask groupTask);
}
