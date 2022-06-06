package com.example.koffi.models;

import java.util.Date;

public class Order {
    public String id;
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
    public String deliveryNote;
    public int method; //0: delivery, 1: takeaway

    public Order(String id,String userID, String name, String storeID, Date date, int status, String address,
                 String phoneNumber, long subtotal, long ship, long total, String deliveryNote, int method) {
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
        this.deliveryNote = deliveryNote;
        this.method = method;
    }

    public Order(String userID, int status) {
        this.userID = userID;
        this.status = status;
    }


}
