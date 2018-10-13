package com.example.ashish.startup.Models;

public class Classes extends ClassID{

    private String Name;
    private String Section;
    private String Total_Students;

    public String getTotal_Students() {
        return Total_Students;
    }

    public void setTotal_Students(String total_Students) {
        Total_Students = total_Students;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public Classes(){

    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public Classes(String name, String section, String total_Students) {
        Name = name;
        Section = section;
        Total_Students = total_Students;
    }
}
