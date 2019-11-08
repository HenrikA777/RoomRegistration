package com.example.roomregistration;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reservation implements Serializable, Comparable<Reservation> {
    @SerializedName("id")
    private int id;
    @SerializedName("fromTime")
    private long fromTime;
    @SerializedName("toTime")
    private long toTime;
    @SerializedName("userId")
    private String userId;
    @SerializedName("purpose")
    private String purpose;
    @SerializedName("roomId")
    private int roomId;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");


    public Reservation() {
    }

    public Reservation(int id, long fromTime, long toTime, String userId, String purpose, int roomId) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.userId = userId;
        this.purpose = purpose;
        this.roomId = roomId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void settoTime(int toTime) {
        this.toTime = toTime;
    }

    public void setfromTime(int fromTime) {
        this.fromTime = fromTime;
    }

    public void setroomId(int roomId) {
        this.roomId = roomId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public void setpurpose(String purpose) {this.purpose = purpose;}

    public int getId() {
        return id;
    }

    public long gettoTime() {
        return toTime;
    }

    public int getroomId() {
        return roomId;
    }

    public long getfromTime() {
        return fromTime;
    }

    public String getpurpose() { return purpose; }

    public String getuserId() {
        return userId;
    }

    public Date getFromDate() { return new Date(getfromTime() * 1000); }

    public Date getToDate() { return new Date(gettoTime() * 1000); }

    @Override
    public int compareTo(Reservation reservation) {
        return (this.gettoTime() < reservation.gettoTime() ? -1 :
                (this.gettoTime() == reservation.gettoTime() ? 0 : 1));
    }

    @NonNull
    @Override
    public String toString() {
        return "Purpose: "+ purpose + "\nFrom: " + dateFormat.format(getFromDate()) + "\nTo: " + dateFormat.format(getToDate());
    }
}
