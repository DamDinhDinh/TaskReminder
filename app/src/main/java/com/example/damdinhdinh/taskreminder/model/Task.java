package com.example.damdinhdinh.taskreminder.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable {
    private int id;
    private String name;
    private String describe;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private int repeat;
    private ArrayList<String> arrRepeat;
    private boolean notification;

    public Task(int id, String name, String describe, int day, int month, int year, int hour, int minute, int repeat, boolean notification) {
        this.id = id;
        this.name = name;
        this.describe = describe;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
        this.notification = notification;
        arrRepeat = new ArrayList<>();
        arrRepeat.add("Does not repeat");
        arrRepeat.add("Every day");
        arrRepeat.add("Every week");
        arrRepeat.add("Week day");
        arrRepeat.add("Every month");
        arrRepeat.add("Every year");
    }

    public Task(String name, String describe, boolean notification) {
        this.name = name;
        this.describe = describe;
        this.notification = notification;
        arrRepeat = new ArrayList<>();
        arrRepeat.add("Does not repeat");
        arrRepeat.add("Every day");
        arrRepeat.add("Every week");
        arrRepeat.add("Week day");
        arrRepeat.add("Every month");
        arrRepeat.add("Every year");
    }


    public Task(){
        id = -1;
        name = "";
        describe ="";
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getDay() {
        return day;
    }

    public void setDate(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public String getTime24Hour(){
        return String.valueOf(hour +":"+ minute);
    }

    public String getDateYearMonth(){
        return String.valueOf(day +"/"+ month +"/"+ year);
    }

    public String getRepeatType(){
        return arrRepeat.get(repeat);
    }
}