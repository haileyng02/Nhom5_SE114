package com.example.koffi;

public class Address {
    public String name;
    public String address;
    public Address()
    {

    }

    public Address(String name, String address) {
        this.name= name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
