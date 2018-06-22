package com.example.ashish.startup.Models;

public class Classes extends ClassID{

  private String Name;

   public Classes(){

   }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Classes(String name) {

        Name = name;
    }
}
