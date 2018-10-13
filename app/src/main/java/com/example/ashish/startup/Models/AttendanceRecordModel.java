package com.example.ashish.startup.Models;

import java.util.Objects;

public class AttendanceRecordModel {
    private String userName,status;

    public AttendanceRecordModel(String userName, String status) {
        this.userName = userName;
        this.status = status;
    }

    public AttendanceRecordModel(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecordModel that = (AttendanceRecordModel) o;
        return Objects.equals(getUserName(), that.getUserName()) &&
                Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getStatus());
    }

    @Override
    public String toString() {
        return "AttendanceRecordModel{" +
                "userName='" + userName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
