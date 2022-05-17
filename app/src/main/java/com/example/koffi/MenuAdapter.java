package com.example.koffi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ListView listView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            listView = itemView.findViewById(R.id.menuList);
            text = itemView.findViewById(R.id.categoryName);
        }
    }

    Context context;
    ArrayList<Category> menuArray;

    public MenuAdapter(Context context, ArrayList<Category> menuArray) {
        this.context = context;
        this.menuArray = menuArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.recycler_item_menu,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = menuArray.get(position);

        holder.text.setText(category.name);

        MenuItemAdapter itemAdapter = new MenuItemAdapter(context,menuArray.get(position).items);
        holder.listView.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        return menuArray.size();
    }
}
