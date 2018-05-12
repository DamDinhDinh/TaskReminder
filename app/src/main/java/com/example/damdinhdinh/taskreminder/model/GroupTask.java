package com.example.damdinhdinh.taskreminder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupTask implements Serializable {
    private int id;
    private String name;
    private int icon;
    private List<Integer> arrTaskID;

    public GroupTask(int id, String name, int icon, List<Integer> arrTaskID) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.arrTaskID = arrTaskID;
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

    public List<Integer> getArrTask() {
        return arrTaskID;
    }

    public void setArrTask(List<Integer> arrTaskID) {
        this.arrTaskID = arrTaskID;
    }
}

