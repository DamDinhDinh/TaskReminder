package com.example.damdinhdinh.taskreminder.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.damdinhdinh.taskreminder.model.GroupTask;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository implements TaskDataSource, GroupTaskDataSource {
    private TaskDao mTaskDao;
    private GroupTaskDao mGroupTaskDao;
    private ExecutorService mDbService;

    public TaskRepository(Application application){
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mGroupTaskDao = db.groupTaskDao();
        mDbService = Executors.newSingleThreadExecutor();
    }

    @Override
    public LiveData<List<GroupTask>> getAllGroupTask() {
        return mGroupTaskDao.getAllGroupTask();
    }

    @Override
    public LiveData<GroupTask> getGroupTaskById(int id) {
        return mGroupTaskDao.getGroupTaskById(id);
    }

    @Override
    public LiveData<GroupTask> getGroupTaskByName(String name) {
        return mGroupTaskDao.getGroupTaskByName(name);
    }

    @Override
    public LiveData<Task> getTaskByName(String name) {
        return mTaskDao.getTaskByName(name);
    }

    @Override
    public LiveData<List<Task>> getAllTaskByGroupTaskId(int groupTaskId) {
        return mGroupTaskDao.getAllTaskByGroupTaskId(groupTaskId);
    }

    @Override
    public void insertGroupTask(final GroupTask groupTask) {
        mDbService.execute(new Runnable() {
            @Override
            public void run() {
                mGroupTaskDao.insertGroupTask(groupTask);
            }
        });
    }

    @Override
    public void updateGroupTask(final GroupTask groupTask) {
        mDbService.execute(new Runnable() {
            @Override
            public void run() {
                mGroupTaskDao.updateGroupTask(groupTask);
            }
        });
    }

    @Override
    public void deleteGroupTask(final GroupTask groupTask) {
        mDbService.execute(new Runnable() {
            @Override
            public void run() {
                mGroupTaskDao.deleteGroupTask(groupTask);
            }
        });
    }

    @Override
    public LiveData<List<Task>> getAllTask() {
        return mTaskDao.getAllTask();
    }

    @Override
    public LiveData<Task> getTaskById(int id) {
        return mTaskDao.getTaskById(id);
    }

    @Override
    public void insertTask(final Task task) {
        mDbService.execute(new Runnable() {
            @Override
            public void run() {
                mTaskDao.insertTask(task);
            }
        });
    }

    @Override
    public void updateTask(final Task task) {
        mDbService.execute(new Runnable() {
            @Override
            public void run() {
                mTaskDao.updateTask(task);
            }
        });
    }

    @Override
    public void deleteTask(final Task task) {
        mDbService.execute(new Runnable() {
            @Override
            public void run() {
                mTaskDao.deleteTask(task);
            }
        });
    }

}
