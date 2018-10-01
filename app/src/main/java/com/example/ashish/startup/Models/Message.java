package com.example.ashish.startup.Models;

public class Message extends MessageID {

    private String Message;
    private String Time;

    public String getEdited() {
        return Edited;
    }

    public void setEdited(String edited) {
        Edited = edited;
    }

    private String Edited;

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

    public Message(String message, String time, String edited) {
        Message = message;
        Time = time;
        Edited = edited;
    }
}
