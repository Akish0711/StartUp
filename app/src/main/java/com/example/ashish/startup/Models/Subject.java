package com.example.ashish.startup.Models;

public class Subject extends SubjectID{
    private String Name,Subject_Name,Username, Teacher_Name;
    private int Percentage,Total_Class,Total_Present;

    public Subject() {
    }

    public Subject(String name, String subject_Name, String username, int percentage, int total_Class, int total_Present) {
        Name = name;
        Subject_Name = subject_Name;
        Username = username;
        Percentage = percentage;
        Total_Class = total_Class;
        Total_Present = total_Present;
        Teacher_Name = Teacher_Name;
    }

    public String getTeacher_Name(){
        return Teacher_Name;
    }

    public void setTeacher_Name(String teacher_name){
        Teacher_Name = teacher_name;
    }

    public String getSubject_Name() {
        return Subject_Name;
    }

    public void setSubject_Name(String subject_Name) {
        Subject_Name = subject_Name;
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

    public int getPercentage() {
        return Percentage;
    }

    public void setPercentage(int percentage) {
        Percentage = percentage;
    }

    public int getTotal_Class() {
        return Total_Class;
    }

    public void setTotal_Class(int total_Class) {
        Total_Class = total_Class;
    }

    public int getTotal_Present() {
        return Total_Present;
    }

    public void setTotal_Present(int total_Present) {
        Total_Present = total_Present;
    }

}