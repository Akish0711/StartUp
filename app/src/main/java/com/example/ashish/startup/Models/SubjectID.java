package com.example.ashish.startup.Models;

import android.support.annotation.NonNull;

public class SubjectID {
    public String subjectID;

    public <T extends SubjectID> T withID(@NonNull final String id){
        this.subjectID = id;
        return (T) this;
    }
}
