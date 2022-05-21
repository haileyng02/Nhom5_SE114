package com.example.koffi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ToppingAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> toppingArray;

    public ToppingAdapter(Context context) {
        this.context = context;

        toppingArray = new ArrayList<String>();
        toppingArray.add("Thạch Dừa/ Coconut Jelly");
        toppingArray.add("Thạch Sương Sáo/Glass Jelly");
        toppingArray.add("Thạch Băng Tuyết");
        toppingArray.add("Trân Châu Caramel/ Caramel Pearl");
        toppingArray.add("Trân Châu Baby");
        toppingArray.add("Trân Châu Hoàng Kim");
        toppingArray.add("Rau Câu");
        toppingArray.add("Pudding");
    }

    @Override
    public int getCount() {
        return toppingArray.size();
    }

    @Override
    public Object getItem(int i) {
        return toppingArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.listview_topping,null);

        CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setText(toppingArray.get(i));

        return view;
    }
}
