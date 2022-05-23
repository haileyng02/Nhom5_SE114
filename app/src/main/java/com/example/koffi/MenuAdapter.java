package com.example.koffi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> implements Filterable {



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String search = charSequence.toString().toLowerCase(Locale.ROOT);
                if (search.isEmpty()) {
                    menuArray = menuArrayOld;
                }
                else {
                    ArrayList<Category> list = new ArrayList<>();
                    for (Category category : menuArrayOld) {
                        ArrayList<Item> itemsArray = new ArrayList<Item>();
                        for (Item item : category.items) {
                            if (item.name.toLowerCase(Locale.ROOT).contains(search)) {
                                Item menuItem = new Item(item.id, item.name, item.image, item.price, item.description);
                                itemsArray.add(menuItem);
                            }
                        }
                        Category categoryNew = new Category(category.id, category.name, category.image, itemsArray);
                        list.add(categoryNew);
                        menuArray = list;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = menuArray;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                menuArray = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        ListView listView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            listView = itemView.findViewById(R.id.menuList);
            text = itemView.findViewById(R.id.categoryName);

            //Bottom Sheet Dialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(itemView.getContext(),R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.bottomsheet_itemdetail,
                    (LinearLayout)itemView.findViewById(R.id.menu_bottomsheet));
            bottomSheetDialog.setContentView(bottomSheetView);

            //Topping ListView
            ListView toppingListView = bottomSheetView.findViewById(R.id.topping_listview);
            ToppingAdapter toppingAdapter = new ToppingAdapter(context);
            toppingListView.setAdapter(toppingAdapter);
            setListViewHeight(toppingListView);



            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Assign data

                    //Show dialog
                    bottomSheetDialog.show();
                }
            });
        }

    }

    Context context;
    ArrayList<Category> menuArray;
    ArrayList<Category> menuArrayOld;

    public MenuAdapter(Context context, ArrayList<Category> menuArray) {
        this.context = context;
        this.menuArray = menuArray;
        this.menuArrayOld = menuArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.recycler_menu,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = menuArray.get(position);

        holder.text.setText(category.name);

        MenuItemAdapter itemAdapter = new MenuItemAdapter(context,menuArray.get(position).items);
        holder.listView.setAdapter(itemAdapter);
        setListViewHeight(holder.listView);
    }

    @Override
    public int getItemCount() {
        return menuArray.size();
    }

    public void setListViewHeight(ListView listview) {
        ListAdapter listadp = listview.getAdapter();
        if (listadp != null) {
            int totalHeight = 0;
            for (int i = 0; i < listadp.getCount(); i++) {
                View listItem = listadp.getView(i, null, listview);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = totalHeight + (listview.getDividerHeight() * (listadp.getCount() - 1));
            listview.setLayoutParams(params);
            //listview.requestLayout();
        }
    }
}
