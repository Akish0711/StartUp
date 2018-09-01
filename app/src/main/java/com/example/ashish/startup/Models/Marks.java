package com.example.ashish.startup.Models;

import java.util.Comparator;

public class Marks extends MarksID {

    private String Name, Username, Max_marks;
    public String inputMarks;
    private int id;
    private boolean isSelected;
    public Marks(){

    }

    @Override
    public String toString() {
        return "Marks{" +
                "Name='" + Name + '\'' +
                ", Username='" + Username + '\'' +
                ", Max_marks='" + Max_marks + '\'' +
                ", inputMarks='" + inputMarks + '\'' +
                ", id=" + id +
                ", isSelected=" + isSelected +
                '}';
    }

    public static final Comparator<Marks> BY_NAME_ALPHABETICAL = new Comparator<Marks>() {
        @Override
        public int compare(Marks marks, Marks t1) {
            return marks.Username.compareTo(t1.Username);
        }
    };

    public String getInputMarks() {
        return inputMarks;
    }

    public void setInputMarks(String inputMarks) {
        this.inputMarks = inputMarks;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
