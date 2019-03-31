package com.google.vision.Models;

public class Exams extends ExamID{
    private String Name, Max_Marks, Date;

    public Exams(){
    }

    public Exams(String name, String marks, String date) {
        Name = name;
        Max_Marks = marks;
        Date = date;
    }

    public String getMax_Marks() {
        return Max_Marks;
    }
    public void setMax_Marks(String marks) {
        Max_Marks = marks;
    }

    public String getDate() {
        return Date;
    }
    public void setDate(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getMarks() {
        return Max_Marks;
    }
    public void setMarks(String marks) {
        Max_Marks = marks;
    }
}
