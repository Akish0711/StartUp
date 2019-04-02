package com.google.vision.Models;

public class Classes extends ClassID{

    private String Name,Section,Institute,Total_Students,Batch;

    public String getTotal_Students() {
        return Total_Students;
    }
    public void setTotal_Students(String total_Students) {
        Total_Students = total_Students;
    }

    public String getInstitute() {
        return Institute;
    }
    public void setInstitute(String institute) {
        Institute = institute;
    }

    public String getBatch() {
        return Batch;
    }
    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getSection() {
        return Section;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public Classes(){
    }

    public Classes(String name, String section, String total_Students, String batch) {
        Name = name;
        Section = section;
        Batch = batch;
    }
}
