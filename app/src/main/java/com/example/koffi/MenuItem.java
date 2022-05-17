package com.example.koffi;

public class MenuItem {
    String id;
    String name;
    String image;
    Long price;
    String description;

    public MenuItem(String id, String name, String image, Long price, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
    }
}
