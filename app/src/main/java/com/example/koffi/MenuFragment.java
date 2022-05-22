package com.example.koffi;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                                ArrayList<MenuItem> itemsArray = new ArrayList<MenuItem>();
                                categoryDocument.collection("items").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot itemDocument : task.getResult()) {
                                                    MenuItem menuItem = new MenuItem(itemDocument.getId(), itemDocument.getString("name"),
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

//    public void getMenuArray() {
//        db.collection("menu")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot categoryDocument : task.getResult()) {
//                        ArrayList<MenuItem> itemsArray = new ArrayList<MenuItem>();
//                        db.collection("menu")
//                                .document(categoryDocument.getId())
//                                .collection("items")
//                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot itemDocument : task.getResult()) {
//                                        MenuItem menuItem = new MenuItem(itemDocument.getId(),itemDocument.getString("name"),
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