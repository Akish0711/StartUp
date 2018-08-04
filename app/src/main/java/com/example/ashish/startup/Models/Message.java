package com.example.ashish.startup.Models;

public class Message {

    private String Message;
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

    public Message(String message, int type) {
        Message = message;
        Type = type;
    }
}
