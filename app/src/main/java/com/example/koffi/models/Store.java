package com.example.koffi.models;

public class Store {
    public String id;
    public String address;
    public String image;
    public String phoneNumber;

    public Store(String id,String address, String image, String phoneNumber) {
        this.id = id;
        this.address = address;
        this.image = image;
        this.phoneNumber = phoneNumber;
    }
}
