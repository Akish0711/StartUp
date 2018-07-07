package com.example.ashish.startup.Models;

public class Marks extends MarksID {

    private String testName, Username, Max_marks;
    private int id;
    private boolean isSelected;
    public Marks(){

    }

    public String getMarksID(){
        return marksID;
    }

    public String getMax_marks() {
        return Max_marks;
    }

    public void setMax_marks(String max_marks) {
        Max_marks = max_marks;
    }

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

    public Marks (String username) {

        Username = username;
    }



    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
