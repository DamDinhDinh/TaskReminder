package com.example.damdinhdinh.taskreminder.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "group_tasks")
public class GroupTask implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int icon;

    @Ignore
    private ArrayList<Task> arrTask = new ArrayList<>();

    public GroupTask(int id, String name, int icon, ArrayList<Task> arrTask) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.arrTask = arrTask;
    }

    public GroupTask(){
    }

    public GroupTask(String name, int icon) {
        this.name = name;
        this.icon = icon;;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Task> getArrTask() {
        return arrTask;
    }

    public void setArrTask(ArrayList<Task> arrTask) {
        this.arrTask = arrTask;
    }
}

