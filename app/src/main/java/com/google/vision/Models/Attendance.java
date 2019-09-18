package com.google.vision.Models;

import java.util.Comparator;

public class Attendance {
    private String Name, Username, Uid;
    private int Percentage;

    public Attendance(){
    }

    public Attendance(String name, String username, String uid, int percentage) {
        Name = name;
        Username = username;
        Uid = uid;
        Percentage = percentage;
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

    public String getUid() {
        return Uid;
    }
    public void setUid(String uid) {
        Uid = uid;
    }

    public int getPercentage() {
        return Percentage;
    }
    public void setPercentage(int percentage) {
        Percentage = percentage;
    }
}
