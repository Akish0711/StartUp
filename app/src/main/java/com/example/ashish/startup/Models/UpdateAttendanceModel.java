package com.example.ashish.startup.Models;

import java.util.Comparator;
import java.util.Objects;

public class UpdateAttendanceModel {

    private String timeStamp;

    public UpdateAttendanceModel(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static final Comparator<UpdateAttendanceModel>BY_TIMESTAMP_LATEST = (o1, o2) -> o2.timeStamp.compareTo(o1.timeStamp);

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAttendanceModel that = (UpdateAttendanceModel) o;
        return Objects.equals(getTimeStamp(), that.getTimeStamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimeStamp());
    }
*/
}
