package com.example.ashish.startup.Models;

public class Message extends MessageID {

    private String Message;
    private String Time;

    public String getTime() {
        return Time;
    }
    public void setTime(String time) {
        Time = time;
    }
    public String getMessage() {
        return Message;
    }
    public void setMessage(String message) {
        Message = message;
    }

    public Message(){
    }

    public Message(String message, String time) {
        Message = message;
        Time = time;
    }
}
