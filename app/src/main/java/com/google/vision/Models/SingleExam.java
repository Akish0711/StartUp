package com.google.vision.Models;

import java.util.Comparator;

public class SingleExam {
    private String Name, Username;
    private double Marks;

    public SingleExam(){
    }

    public SingleExam(String name, String username, double marks) {
        Name = name;
        Username = username;
        Marks = marks;
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

    public double getMarks() {
        return Marks;
    }
    public void setMarks(double marks) {
        Marks = marks;
    }
}
