package com.google.vision.Models;

public class SingleMessage {

    private String Name;
    private String URL;
    private int Type;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public SingleMessage(){
    }

    public SingleMessage(String name, String url, int type) {
        Name = name;
        URL = url;
        Type = type;
    }
}
