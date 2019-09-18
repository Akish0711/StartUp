package com.google.vision.Models;

import java.util.Comparator;

public class Marks {
    private String Name, Username, Uid;
    private double Marks;

    public Marks(){
    }

    public Marks(String name, String username, double marks, String uid) {
        Name = name;
        Username = username;
        Marks = marks;
        Uid = uid;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getUid() {
        return Uid;
    }
    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUsername() {
        return Username;
    }
    public void setUsername(String username) {
        Username = username;
    }

    public double getMarks() {
        return Marks;
    }
    public void setMarks(double marks) {
        Marks = marks;
    }
}
