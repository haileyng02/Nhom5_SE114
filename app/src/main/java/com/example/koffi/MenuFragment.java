package com.example.koffi;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    FirebaseFirestore db;
    private GridView gridView;
    private RecyclerView recyclerView;
    private ArrayList<Category> menuArray;
    private CategoryAdapter categoryAdapter;
    private MenuAdapter menuAdapter;
    private LinearLayout menuLinear;
    private LinearLayout rootLinear;

    private int orderMethod=0;
    private ImageView methodImage;
    private TextView methodText;
    private TextView addressText;

    String address=null;


    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Toolbar
        Toolbar toolbar = view.findViewById(R.id.menu_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        gridView = view.findViewById(R.id.category_gridview);
        recyclerView = view.findViewById(R.id.menu_recycler);

        menuArray = new ArrayList<Category>();
//        getMenuArray();
        loadMenu();

        categoryAdapter = new CategoryAdapter(getContext(),menuArray);
        gridView.setAdapter(categoryAdapter);


        menuAdapter = new MenuAdapter(getContext(), menuArray);

        recyclerView.setAdapter(menuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Toolbar title (To change category)
        LinearLayout changeCategory = view.findViewById(R.id.changeCategory);
        changeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_category,
                        (LinearLayout)view.findViewById(R.id.menu_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);

                //Handle Grid View
                gridView = bottomSheetView.findViewById(R.id.category_gridview_bottomsheet);
                gridView.setAdapter(categoryAdapter);

                //Show dialog
                bottomSheetDialog.show();
            }
        });

        //Navigate to checkout
        LinearLayout totalPrice = view.findViewById(R.id.totalPrice);
        totalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("method",orderMethod);
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_checkOutFragment,bundle);
            }
        });

        //Change order method
        methodImage = view.findViewById(R.id.menu_methodImage);
        methodText = view.findViewById(R.id.menu_methodText);
        addressText = view.findViewById(R.id.menu_addressText);

        LinearLayout orderMethod = view.findViewById(R.id.menu_ordermethod);
        orderMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_ordermethod,
                        (LinearLayout)view.findViewById(R.id.ordermethod_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);

                //Handle bottom sheet
                //Delivery
                LinearLayout delivery = bottomSheetView.findViewById(R.id.ordermethod_delivery);
                delivery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setOrderMethod(0);
                        bottomSheetDialog.dismiss();
                    }
                });

                //Take away
                LinearLayout takeaway = bottomSheetView.findViewById(R.id.ordermethod_takeaway);
                takeaway.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setOrderMethod(1);
                        bottomSheetDialog.dismiss();
                    }
                });

                //Take away edit
                Button editStoreBtn = bottomSheetView.findViewById(R.id.takeaway_editBtn);
                editStoreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        replaceStoreFragment();
                    }
                });

                //Show dialog
                bottomSheetDialog.show();
            }
        });
    }

    private void loadMenu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                displayMenu(1);
            }
        }).start();
    }
    public void displayMenu(int i) {
            DocumentReference categoryDocument = db.collection("menu").document("category-"+i);
            categoryDocument.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                ArrayList<Item> itemsArray = new ArrayList<Item>();
                                categoryDocument.collection("items").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot itemDocument : task.getResult()) {
                                                    Item menuItem = new Item(itemDocument.getId(), itemDocument.getString("name"),
                                                            itemDocument.getString("image"), itemDocument.getLong("price"), itemDocument.getString("description"));
                                                    itemsArray.add(menuItem);
                                                }
                                                if (document.exists()) {
                                                    Category category = new Category(document.getId(),
                                                            document.getString("name"),document.getString("image"),itemsArray);
                                                    menuArray.add(category);
                                                    categoryAdapter.notifyDataSetChanged();
                                                    menuAdapter.notifyDataSetChanged();
                                                    if (i == 1) displayMenu(2);
                                                    else if (i == 2) displayMenu(3);
                                                    else if (i == 3) displayMenu(4);
                                                    else if (i == 4) displayMenu(5);
                                                    else if (i == 5) displayMenu(6);
                                                    else if (i == 6) displayMenu(7);
                                                    else if (i == 7) displayMenu(8);
                                                }
                                            }
                                        });

                                }
                            else {
                                System.out.println("Error getting documents."+ task.getException());
                            }
                        }
                    });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater optionInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menufrag_option_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
    }

    public void replaceStoreFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,new StoreFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void setOrderMethod(int method) {
        if (method==0 || method==1) {
            orderMethod = method;

            String methodIcon, text;
            if (orderMethod==0) {
                methodIcon="icon_delivery";
                text="Giao đến";
            }
            else {
                methodIcon="icon_takeaway";
                text="Đến lấy tại";
            }

            int drawableId = getView().getResources().getIdentifier(methodIcon, "drawable", getContext().getPackageName());
            methodImage.setImageResource(drawableId);
            methodText.setText(text);
            if (address != null)
                addressText.setText(address);
            else
                addressText.setText("Chọn địa chỉ");
        }
    }
//    public void getMenuArray() {
//        db.collection("menu")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot categoryDocument : task.getResult()) {
//                        ArrayList<Item> itemsArray = new ArrayList<Item>();
//                        db.collection("menu")
//                                .document(categoryDocument.getId())
//                                .collection("items")
//                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot itemDocument : task.getResult()) {
//                                        Item menuItem = new Item(itemDocument.getId(),itemDocument.getString("name"),
//                                                itemDocument.getString("image"),itemDocument.getLong("price"),itemDocument.getString("description"));
//                                        itemsArray.add(menuItem);
//
//                                    }
//                                    Category category = new Category(categoryDocument.getId(),
//                                            categoryDocument.getString("name"),categoryDocument.getString("image"),itemsArray);
//                                    menuArray.add(category);
//                                    categoryAdapter.notifyDataSetChanged();
//                                    menuAdapter.notifyDataSetChanged();
//                                }
//                                else {
//                                    System.out.println("Error getting documents."+ task.getException());
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
//
//    }
}