package com.google.vision.Models;

import java.util.Comparator;

public class Users {
    private String Name, Username, Uid;
    private boolean Checked = false;

    public Users(){
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public boolean getChecked() {
        return Checked;
    }
    public void setChecked(boolean checked) {
        Checked = checked;
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

    public Users(String name, String username, String uid) {
        Name = name;
        Username = username;
        Uid = uid;
    }
}
