package com.example.ashish.startup.Models;

public class Message {

    private String Message;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Name;
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

    public Message(String message, int type, String name) {
        Message = message;
        Type = type;
        Name = name;
    }
}
