package com.example.koffi;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CartItem implements Parcelable {
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

    protected CartItem(Parcel in) {
        cartID = in.readString();
        item = in.readString();
        quantity = in.readInt();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readLong();
        }
        note = in.readString();
        toppings = in.createTypedArrayList(Topping.CREATOR);
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cartID);
        parcel.writeString(item);
        parcel.writeInt(quantity);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(price);
        }
        parcel.writeString(note);
        parcel.writeTypedList(toppings);
    }
}
