package com.example.ashish.startup.Models;

public class SingleMessage {

    private String Message;

    public String getMessage() {
        return Message;
    }
    public void setMessage(String message) {
        Message = message;
    }

    public SingleMessage(){
    }

    public SingleMessage(String message) {
        Message = message;
    }
}
