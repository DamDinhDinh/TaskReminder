package com.example.damdinhdinh.taskreminder.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GroupTask implements Serializable {
    private int id;
    private String name;
    private int icon;
    private ArrayList<Task> arrTask;

    public GroupTask(int id, String name, int icon, ArrayList<Task> arrTask) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.arrTask = arrTask;
    }

    public GroupTask(){
        id = -1;
        name = "";
        arrTask = new ArrayList<>();
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

