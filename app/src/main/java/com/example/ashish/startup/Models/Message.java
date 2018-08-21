package com.example.ashish.startup.Models;

public class Message {
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Name;
    private String Message;

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    private String Time;
    private int Type;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public Message(){
    }

    public Message(String message, int type, String name,String time) {
        Message = message;
        Type = type;
        Name = name;
        Time = time;
    }
}
