package com.example.ashish.startup.Models;

public class Message {

    private String Message;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    private String Image;


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Message(){
    }

    public Message(String message, String image) {
        Message = message;
        Image = image;
    }
}
