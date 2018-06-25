package com.example.ashish.startup.Models;

import java.util.Comparator;

public class Status {

    public Status(){
    }

    public static final Comparator<Status>BY_NAME_ALPHABETICAL = new Comparator<Status>() {
        @Override
        public int compare(Status status, Status t1) {
            return status.Username.compareTo(t1.Username);
        }
    };

private int Percentage;
    private String Name;

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

    private String Username;

    public Status(int percentage,String name, String username) {
        Percentage = percentage;
        Name = name;
        Username = username;
    }

    public int getPercentage() {
        return Percentage;
    }

    public void setPercentage(int percentage) {
        Percentage = percentage;
    }

}
