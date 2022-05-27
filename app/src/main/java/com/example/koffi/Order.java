package com.example.koffi;

import java.util.Date;

public class Order {
    public String userID;
    public String name;
    public String storeID;
    public Date date;
    public int status; //0: cart, 1: ordered, 2: confirmed, 3: delivered, 4: canceled
    public String address;
    public String phoneNumber;
    public long subtotal;
    public long ship;
    public long total;

    public Order(String userID, int status) {
        this.userID = userID;
        this.status = status;
    }

    public Order(String userID, String name, String storeID, Date date, int status, String address, String phoneNumber, long subtotal, long ship, long total) {
        this.userID = userID;
        this.name = name;
        this.storeID = storeID;
        this.date = date;
        this.status = status;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.subtotal = subtotal;
        this.ship = ship;
        this.total = total;
    }
}