package com.example.koffi;

public class Topping implements Comparable<Topping> {
    public Topping(String id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String id;
    public String name;
    public long price;

    @Override
    public int compareTo(Topping topping) {
        return Integer.compare(Integer.parseInt(this.id), Integer.parseInt(topping.id));
    }
}
