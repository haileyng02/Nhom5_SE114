package com.example.koffi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ToppingAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<Topping> toppingArray;

    public ToppingAdapter(Context context, ArrayList<Topping> toppingArray) {
        this.context = context;
        this.toppingArray = toppingArray;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
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
        checkBox.setText(toppingArray.get(i).name);
        TextView price = view.findViewById(R.id.toppingPrice);
        price.setText(toppingArray.get(i).price + "đ");


        return view;
    }

}
