package com.example.healthcare;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private String name;
    private String address;
    private String contact;
    private String pincode;
    private String date;
    private String time;
    private float price;
    private String type;

    public OrderDetail() {
    }

    public OrderDetail(String name, String address, String contact, String pincode, String date, String time, float price, String type) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.pincode = pincode;
        this.date = date;
        this.time = time;
        this.price = price;
        this.type = type;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
