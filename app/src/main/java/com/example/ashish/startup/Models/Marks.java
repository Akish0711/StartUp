package com.example.ashish.startup.Models;

import java.util.Comparator;

public class Marks {
    private String Name, Username;
    private int Marks;

    public Marks(){
    }

    public static final Comparator<Marks> BY_NAME_ALPHABETICAL = (marks, t1) -> marks.Username.compareTo(t1.Username);

    public Marks(String name, String username, int marks) {
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

    public int getMarks() {
        return Marks;
    }
    public void setMarks(int marks) {
        Marks = marks;
    }
}
