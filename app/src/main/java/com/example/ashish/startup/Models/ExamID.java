package com.example.ashish.startup.Models;

import android.support.annotation.NonNull;

public class ExamID {
    public String examID;

    public <T extends ExamID> T withID(@NonNull final String id){
        this.examID = id;
        return (T) this;
    }
}
