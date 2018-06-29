package com.example.ashish.startup.Models;

import java.util.Comparator;

public class Attendance {

    private String Name, Username;
    private int id;
    private boolean isSelected;
    public Attendance(){

    }

    public static final Comparator<Attendance> BY_NAME_ALPHABETICAL = new Comparator<Attendance>() {
        @Override
        public int compare(Attendance attendance, Attendance t1) {
            return attendance.Username.compareTo(t1.Username);
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Attendance(String name, String username) {
        Name = name;
        Username = username;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
