package com.example.ashish.startup.Models;

import java.util.Comparator;

public class Marks {
    private String Name, Username, Uid, Marks;

    public Marks(){
    }

    public static final Comparator<Marks> BY_NAME_ALPHABETICAL = (marks, t1) -> marks.Username.compareTo(t1.Username);

    public Marks(String name, String username, String uid, String marks) {
        Name = name;
        Username = username;
        Uid = uid;
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

    public String getUid() {
        return Uid;
    }
    public void setUid(String uid) {
        Uid = uid;
    }

    public String getMarks() {
        return Marks;
    }
    public void setMarks(String marks) {
        Marks = marks;
    }
}
