package com.example.ashish.startup.Models;

import android.support.annotation.NonNull;

public class MarksID {
    public String marksID;

    public <T extends MarksID> T withID(@NonNull final String id){
        this.marksID = id;
        return (T) this;
    }
}

