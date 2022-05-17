package com.example.koffi;

import java.util.ArrayList;

public class Category {
    String id;
    String name;
    String image;
    ArrayList<MenuItem> items;

    public Category(String id, String name, String image, ArrayList<MenuItem> items) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.items = items;
    }
}
