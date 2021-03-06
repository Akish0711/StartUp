package com.google.vision.Models;

import androidx.annotation.NonNull;

public class ClassID {
    public String classID;

    public <T extends ClassID> T withID(@NonNull final String id){
        this.classID = id;
        return (T) this;
    }
}
