package com.example.koffi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
        getMenuArray();

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

    }


    public void getMenuArray() {
        db.collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot categoryDocument : task.getResult()) {
                        ArrayList<Item> itemsArray = new ArrayList<Item>();
                        db.collection("menu")
                                .document(categoryDocument.getId())
                                .collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot itemDocument : task.getResult()) {
                                        Item menuItem = new Item(itemDocument.getId(),itemDocument.getString("name"),
                                                itemDocument.getString("image"),itemDocument.getLong("price"),itemDocument.getString("description"));
                                        itemsArray.add(menuItem);

                                    }
                                    Category category = new Category(categoryDocument.getId(),
                                            categoryDocument.getString("name"),categoryDocument.getString("image"),itemsArray);
                                    menuArray.add(category);
                                    categoryAdapter.notifyDataSetChanged();
                                    menuAdapter.notifyDataSetChanged();
                                }
                                else {
                                    System.out.println("Error getting documents."+ task.getException());
                                }
                            }
                        });
                    }
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
}