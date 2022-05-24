package com.example.koffi;

public class CartItem {
    Item item;
    int quantity;
    Long price;
    String note;

    public CartItem(Item item,int quantity, Long price, String note) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.note = note;
    }
}
