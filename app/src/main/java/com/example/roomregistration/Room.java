package com.example.roomregistration;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Room implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("capacity")
    private int capacity;
    @SerializedName("remarks")
    private String remarks;


    public Room() {
    }

    public Room(int id, String name, String description, int capacity, String remarks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.remarks = remarks;
        this.capacity = capacity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public void setname(String name) {
        this.name = name;
    }

    public void setcapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setremarks(String remarks) {
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public String getdescription() { return description; }

    public int getcapacity() {
        return capacity;
    }

    public String getname() {
        return name;
    }

    public String getremarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return name + ":    " + description;
    }
}
