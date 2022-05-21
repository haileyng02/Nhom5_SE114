package com.example.koffi;

import java.util.ArrayList;

public class Category {
    String id;
    String name;
    String image;
    ArrayList<Item> items;

    public Category(String id, String name, String image, ArrayList<Item> items) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.items = items;
    }
}
