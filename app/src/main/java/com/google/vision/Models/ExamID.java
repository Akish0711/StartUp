package com.google.vision.Models;

import androidx.annotation.NonNull;

public class ExamID {
    public String examID;

    public <T extends ExamID> T withID(@NonNull final String id){
        this.examID = id;
        return (T) this;
    }
}
