package com.example.ashish.startup.Models;

import java.util.Comparator;

public class Attendance {

    private String Name, Username;
    public Attendance(){
    }

    public static final Comparator<Attendance> BY_NAME_ALPHABETICAL = (attendance, t1) -> attendance.Username.compareTo(t1.Username);

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
