package com.example.koffi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
//import com.makeramen.roundedimageview.RoundedImageView;

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

            ArrayList<Topping> toppingArray = new ArrayList<Topping>();

            ToppingAdapter toppingAdapter = new ToppingAdapter(context, toppingArray);
            toppingListView.setAdapter(toppingAdapter);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("toppings").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Topping topping = new Topping(documentSnapshot.getId(), documentSnapshot.getString("name"),
                                        documentSnapshot.getLong("price"));
                                toppingArray.add(topping);
                            }
                            for (Topping topping : toppingArray) {
                                System.out.println("result " + topping.name);
                            }
                            toppingAdapter.notifyDataSetChanged();
                            setListViewHeight(toppingListView);
                        }
                    });

            for (Topping topping : toppingArray) {
                System.out.println("result 2 " + topping.name);
            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Assign data
                    Item item = (Item) listView.getItemAtPosition(i);
                    TextView tvName = bottomSheetView.findViewById(R.id.tvName);
                    tvName.setText(item.name);

                    ImageView imageView = bottomSheetView.findViewById(R.id.itemdetail_image);
                    int drawableId = view.getResources().getIdentifier(item.image, "drawable", context.getPackageName());
                    imageView.setImageResource(drawableId);

                    TextView tvPrice = bottomSheetView.findViewById(R.id.tvPrice);
                    tvPrice.setText(item.price.toString());

                    TextView tvDes = bottomSheetView.findViewById(R.id.tvDescription);
                    tvDes.setText(item.description);

                    ImageButton closeView = bottomSheetDialog.findViewById(R.id.itemdetail_closeBtn);
                    closeView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                        }
                    });

                    TextView tvNumber = bottomSheetDialog.findViewById(R.id.tvNumber);
                    Button totalBtn = bottomSheetView.findViewById(R.id.itemTotalPrice);
                    long number = Long.parseLong(tvNumber.getText().toString());
                    tvNumber.setText(Long.toString(number));
                    total = number * item.price;
                    totalBtn.setText(Long.toString(total));

                    ImageButton plusBtn = bottomSheetDialog.findViewById(R.id.plusButton);
                    plusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long number = Long.parseLong(tvNumber.getText().toString()) + 1;
                            tvNumber.setText(Long.toString(number));
                            totalBtn.setText(Long.toString(number * item.price));
                        }
                    });

                    ImageButton minusBtn = bottomSheetDialog.findViewById(R.id.minusButton);
                    minusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long number = Long.parseLong(tvNumber.getText().toString()) - 1;
                            if (number >= 0) {
                                tvNumber.setText(Long.toString(number));
                                totalBtn.setText(Long.toString(number * item.price));
                            }
                        }
                    });

                    RadioButton sizeM = bottomSheetView.findViewById(R.id.sizeM_radio);
                    RadioButton sizeL = bottomSheetView.findViewById(R.id.sizeL_radio);
                    sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                sizeL.setChecked(false);
                                isL = false;
                            }
                            if (isL)
                                totalBtn.setText(Long.toString(total + 6000));
                            else
                                totalBtn.setText(Long.toString(total));
                        }
                    });
                    sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                sizeM.setChecked(false);
                                isL = true;
                            }
                            if (isL)
                                totalBtn.setText(Long.toString(total + 6000));
                            else
                                totalBtn.setText(Long.toString(total));
                        }
                    });


                    //Show dialog
                    bottomSheetDialog.show();
                }
            });
        }

    }
    long total;
    boolean isL = false;

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
