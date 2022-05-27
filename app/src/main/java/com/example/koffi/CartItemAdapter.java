package com.example.koffi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CartItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CartItem> itemArray;

    public CartItemAdapter(Context context, ArrayList itemArray) {
        this.context = context;
        this.itemArray = itemArray;
    }

    @Override
    public int getCount() {
        return itemArray.size();
    }

    @Override
    public Object getItem(int i) {
        return itemArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.listview_cartitem,null);

        LinearLayout deleteBtn =  view.findViewById(R.id.cart_deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemArray.remove(i);
                notifyDataSetChanged();
            }
        });

        TextView quantityText = view.findViewById(R.id.cart_quantity);
        quantityText.setText(itemArray.get(i).quantity+"x");

        TextView nameText = view.findViewById(R.id.cart_name);
        nameText.setText(itemArray.get(i).item);

        String toppingNote = "";
        for (Topping topping : itemArray.get(i).toppings) {
            toppingNote += ", " + topping.name;
        }
        TextView noteText = view.findViewById(R.id.cart_note);
        noteText.setText(itemArray.get(i).note + toppingNote);

        TextView priceText = view.findViewById(R.id.cart_price);
        priceText.setText(itemArray.get(i).price+"đ");

        return view;
    }
}
