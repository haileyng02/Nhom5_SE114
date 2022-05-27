package com.example.koffi;

import java.util.ArrayList;

public class CartItem {
    public String cartID;
    public String item;
    public int quantity;
    public Long price;
    public String note;
    public ArrayList<Topping> toppings;

    public CartItem() {
    }

    public CartItem(String cartID, String item, int quantity, Long price, String note, ArrayList<Topping> toppings) {
        this.cartID = cartID;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.note = note;
        this.toppings = toppings;
    }
}
