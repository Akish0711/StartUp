package com.example.ashish.startup.Models;

public class Subject extends SubjectID{
    private String Subject_Name, Teacher_Name, Teacher_id;
    private int Total_Class,Total_Present;

    public Subject() {
    }

    public Subject(String subject_Name, int total_Class, int total_Present, String teacher_Name, String teacher_id ) {
        Subject_Name = subject_Name;
        Total_Class = total_Class;
        Total_Present = total_Present;
        Teacher_Name = teacher_Name;
        Teacher_id = teacher_id;
    }

    public String getTeacher_id(){
        return Teacher_id;
    }

    public void setTeacher_id(String teacher_id){
        Teacher_id = teacher_id;
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